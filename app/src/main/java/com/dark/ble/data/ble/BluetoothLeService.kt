package com.dark.ble.data.ble

import android.app.Activity
import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dark.ble.screen.getActivity
import com.dark.ble.utils.Constants
import com.dark.ble.utils.Constants.TAG

class BluetoothLeService : Service() {

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var connectionState = STATE_DISCONNECTED
    private val context = this

    fun initialize(): Boolean {
        val bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            Log.d(TAG, "Unable to obtain a BluetoothAdapter.")
            return false
        }
        return true
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothLeService {
            return this@BluetoothLeService
        }
    }

    private val bluetoothGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
                connectionState = STATE_CONNECTED
                broadcastUpdate(ACTION_GATT_CONNECTED)
                for (perms in Constants.PERMISSIONS) {
                    if (ContextCompat.checkSelfPermission(context, perms) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(context.getActivity() as Activity, arrayOf(perms),
                            Constants.PERMISSION_CODE
                        )
                    }
                }
                Log.d(TAG, "Discovering services : ${bluetoothGatt?.discoverServices()}")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                connectionState = STATE_DISCONNECTED
                broadcastUpdate(ACTION_GATT_DISCONNECTED)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.d(TAG, "onServicesDiscovered received: $status")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            Log.d(TAG, "onCharacteristicRead received: $characteristic")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            Log.d(TAG, "onCharacteristicChanged received: $characteristic")
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
        }
    }

    fun connect(address: String): Boolean {
        bluetoothAdapter?.let { adapter ->
            try {
                val device = adapter.getRemoteDevice(address)
                // connect to the GATT server on the device
                for (perms in Constants.PERMISSIONS) {
                    if (ContextCompat.checkSelfPermission(this, perms) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this.getActivity() as Activity, arrayOf(perms),
                            Constants.PERMISSION_CODE
                        )
                    }
                }
                bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback)
                return true
            } catch (exception: IllegalArgumentException) {
                Log.d(TAG, "Device not found with provided address.  Unable to connect.")
                return false
            }
        } ?: run {
            Log.d(TAG, "BluetoothAdapter not initialized")
            return false
        }
    }

    fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
        bluetoothGatt?.let { gatt ->
            for (perms in Constants.PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(this, perms) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this.getActivity() as Activity, arrayOf(perms),
                        Constants.PERMISSION_CODE
                    )
                }
            }
            gatt.readCharacteristic(characteristic)
        } ?: run {
            Log.d(TAG, "BluetoothGatt not initialized")
        }
    }

    fun getSupportedGattServices(): List<BluetoothGattService?>? {
        return bluetoothGatt?.services
    }

    private fun broadcastUpdate(action: String) {
        Log.d(TAG, "Broadcasting update : ${Intent(action)}")
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic) {
        val intent = Intent(action)
        val data: ByteArray? = characteristic.value
        if (data?.isNotEmpty() == true) {
            val hexString: String = data.joinToString(separator = " ") {
                String.format("%02X", it)
            }
            intent.putExtra(EXTRA_DATA, "$data\n$hexString")
        }
        sendBroadcast(intent)
    }


    override fun onUnbind(intent: Intent?): Boolean {
        close()
        return super.onUnbind(intent)
    }

    private fun close() {
        for (perms in Constants.PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    perms
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this.getActivity() as Activity,
                    arrayOf(perms),
                    Constants.PERMISSION_CODE
                )
            }
        }
        bluetoothGatt?.let { gatt ->
            gatt.close()
            bluetoothGatt = null
        }
    }

    companion object {
        const val ACTION_GATT_CONNECTED =
            "com.dark.ble.data.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED =
            "com.dark.ble.data.ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED =
            "com.dark.ble.data.ACTION_GATT_SERVICES_DISCOVERED"
        const val ACTION_DATA_AVAILABLE =
            "com.dark.ble.data.ACTION_DATA_AVAILABLE"
        const val EXTRA_DATA =
            "com.dark.ble.data.EXTRA_DATA"

        private const val STATE_DISCONNECTED = 0
        private const val STATE_CONNECTED = 2
    }
}