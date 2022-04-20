package com.dark.ble

import android.app.Service
import android.bluetooth.BluetoothGattService
import android.content.*
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dark.ble.data.BLEConstant.TAG
import com.dark.ble.data.BLERepository
import com.dark.ble.data.BluetoothLeService
import kotlinx.coroutines.launch
import java.lang.Exception

@Composable
inline fun <reified BoundService : Service, reified BoundServiceBinder : Binder>
        rememberBoundLocalService(crossinline getService: @DisallowComposableCalls BoundServiceBinder.() -> BoundService): BoundService? {
    val context: Context = LocalContext.current
    var boundService: BoundService? by remember(context) { mutableStateOf(null) }
    val serviceConnection: ServiceConnection = remember(context) {
        object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                boundService = (service as BoundServiceBinder).getService()
            }

            override fun onServiceDisconnected(arg0: ComponentName) {
                boundService = null
            }
        }
    }
    DisposableEffect(context, serviceConnection) {
        context.bindService(
            Intent(context, BoundService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
        onDispose { context.unbindService(serviceConnection) }
    }
    return boundService
}

@Composable
internal fun DeviceScreen(navController: NavHostController, deviceAddress: String) {

    var bluetoothLeService =
        rememberBoundLocalService<BluetoothLeService, BluetoothLeService.LocalBinder> { getService() }
    val context = LocalContext.current
    val device = BLERepository.getDevice(deviceAddress, context)
    val gattServices = remember { mutableStateListOf<BluetoothGattService?>() }
    val gattExtraData = remember { mutableStateOf("text") }
    val connectionState = remember { mutableStateOf("text") }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    /**
     * Handles various events fired by the Service.
     * ACTION_GATT_CONNECTED: connected to a GATT server.
     * ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
     * ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
     * ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
     *                        or notification operations.
     **/
    val gattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "GATT update receiver : ${intent.action}")
            when (intent.action) {
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    connectionState.value = "Connected"
                    try {
                        Log.d(TAG, "Gatt services : ${bluetoothLeService?.getSupportedGattServices()}")
                        for (gattService in bluetoothLeService!!.getSupportedGattServices()!!) {
                            gattServices.add(gattService)
                        }
                    } catch (e: Exception) {
                        Log.d(TAG, "Get supported gatt services : $e")
                    }
                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    connectionState.value = "Disconnected"
                }
                BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED -> {
                    if (bluetoothLeService != null && bluetoothLeService!!.getSupportedGattServices() != null) {
                        for (gattService in bluetoothLeService!!.getSupportedGattServices()!!) {
                            gattServices.add(gattService)
                        }
                    }
                }
                BluetoothLeService.ACTION_DATA_AVAILABLE -> {
                    gattExtraData.value =
                        intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toString()
                }
            }
        }
    }

    fun makeGattUpdateIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
            addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
            addAction(BluetoothLeService.EXTRA_DATA)
        }
    }

    /**
     * Code to manage Service lifecycle.
     */
    val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            bluetoothLeService = (service as BluetoothLeService.LocalBinder).getService()
            if (bluetoothLeService != null) {
                bluetoothLeService.let { bluetooth ->
                    if (!bluetooth!!.initialize()) {
                        Log.d(TAG, "Unable to initialize Bluetooth")
                        coroutineScope.launch { snackbarHostState.showSnackbar("Bluetooth not initialised") }
                    }
                }
            }
            Log.d(TAG, "Connecting to bluetooth device : $deviceAddress")
            context.registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())
            bluetoothLeService!!.connect(deviceAddress)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            bluetoothLeService = null
        }
    }

    // Lets bind the bluetoothLeService to our activity
    val gattServiceIntent = Intent(context, BluetoothLeService::class.java)
    context.bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

    Scaffold(
        bottomBar = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        Column(
            Modifier.padding(all = 16.dp)
        ) {
            Text(
                text = "Connection : ${connectionState.value}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(
                        alignment = Alignment.CenterHorizontally
                    )
            )
            Text(
                text = "Address : $deviceAddress",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(
                        alignment = Alignment.CenterHorizontally
                    )
            )
            Text(
                text = "Device : $device",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(
                        alignment = Alignment.CenterHorizontally
                    )
            )

            for (gattService in gattServices) {
                Text(
                    text = "UUID : ${gattService!!.uuid}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(
                            alignment = Alignment.CenterHorizontally
                        )
                )
                Text(
                    text = "Gatt Service : $gattService",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(
                            alignment = Alignment.CenterHorizontally
                        )
                )
                for (gattCharacteristic in gattService.characteristics) {
                    Text(
                        text = "Gatt Characteristic : $gattCharacteristic",
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(
                                alignment = Alignment.CenterHorizontally
                            )
                    )
                }
            }
        }
    }
}