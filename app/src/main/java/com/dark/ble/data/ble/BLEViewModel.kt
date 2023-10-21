package com.dark.ble.data.ble

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dark.ble.screen.getActivity
import com.dark.ble.utils.Constants
import com.dark.ble.utils.Constants.TAG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BLEViewModel(application: Application) : AndroidViewModel(application) {

    // Map<BluetoothDevice, <Moving_Average_Rssi, Total_Scan_Count>>
    private val bluetoothManager = application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

    private var _leDeviceList = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val leDeviceList: MutableStateFlow<List<BluetoothDevice>>
        get() = _leDeviceList

    var bluetoothFunctionality: MutableLiveData<Boolean> = MutableLiveData(false)
    var bleScanState: BleState = BleState.BLE_STATE_IDLE


    // Scan callback for bluetoothLeScanner
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)

            viewModelScope.launch {
                addDevice(
                    bluetoothDevice = result.device,
                )
            }

            // Not necessary, but returns batch results early
            // https://stackoverflow.com/questions/27040086/onbatchscanresults-is-not-called-in-android-ble
            // bluetoothLeScanner.flushPendingScanResults(this)
        }
    }

    fun getDevice(bluetoothDeviceAddress: String): BluetoothDevice? {
        val bluetoothDevice = leDeviceList.value.filter { it.address == bluetoothDeviceAddress }
        return if (bluetoothDevice.isNotEmpty()) bluetoothDevice.first() else null
    }

    suspend fun addDevice(bluetoothDevice: BluetoothDevice) {
        Log.d(TAG, "Found suitable address to add: ${bluetoothDevice.address}")

        if (leDeviceList.value.contains(bluetoothDevice)) return

        Log.d(TAG, "Adding: ${bluetoothDevice.address}")

        leDeviceList.value = leDeviceList.value.plus(bluetoothDevice)
    }

    private fun clearDeviceList() {
        leDeviceList.value = emptyList()
    }

    @SuppressLint("MissingPermission")
    fun startScan(context: Context) {


        bleScanState = BleState.BLE_STATE_SCANNING
        clearDeviceList()

        checkBLEPermissions(context = context)

//        val scanTimer = object : CountDownTimer(Constants.SCAN_PERIOD, Constants.COUNTDOWN_INTERVAL) {
//            override fun onTick(millisUntilFinished: Long) {
//                if (bleScanState == BleState.BLE_STATE_STOP) {
//                    checkBLEPermissions(context = context)
//                    bluetoothLeScanner.stopScan(leScanCallback)
//                    this.cancel()
//                }
//            }
//
//            override fun onFinish() {
//                checkBLEPermissions(context = context)
//                bluetoothLeScanner.stopScan(leScanCallback)
//                bleScanState = BleState.BLE_STATE_SCAN_TIMEOUT
//            }
//        }

        if (bluetoothAdapter.isEnabled) {
            val scanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
                .setReportDelay(0)
                .build()

            bluetoothLeScanner.startScan(null, scanSettings, leScanCallback)

//            scanTimer.start()
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan(context: Context) {
        checkBLEPermissions(context = context)
        bluetoothLeScanner.stopScan(leScanCallback)
        bleScanState = BleState.BLE_STATE_STOP
    }

    @Composable
    fun CheckBluetoothEnabled(context: Context, showSnackbar: (String, SnackbarDuration) -> Unit) {
        // Check if bluetooth access is granted or not launcher not used as of now,
        // but can be used later to ask user for perms again if denied
        if (bluetoothAdapter == null) {
            bluetoothFunctionality.value = false
            showSnackbar("Device does not have bluetooth!", SnackbarDuration.Short)
            return
        }

        if (bluetoothAdapter.isEnabled) {
            bluetoothFunctionality.value = true
            return
        }

        // Launcher to handle asking for permissions
        val permissionLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                Log.d(TAG, if (isGranted) "PERMISSION GRANTED" else "PERMISSION DENIED")
            }

        // Launcher to handle enabling bluetooth function
        val bluetoothLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    bluetoothFunctionality.value = true
                } else {
                    showSnackbar("Please enable bluetooth to scan dog tags", SnackbarDuration.Short)
                    bluetoothFunctionality.value = false
                }
            }

        SideEffect { permissionLauncher.launch(Manifest.permission.BLUETOOTH) }

        checkBLEPermissions(context = context)

        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        SideEffect { bluetoothLauncher.launch(enableBtIntent) }
    }

    /**
     * As per android docs :
     * If you declare any dangerous permissions, and if your app is installed on a device that runs
     * Android 6.0 (API level 23) or higher, you must request the dangerous permissions at runtime
     * by following the steps in this guide.
     *
     * We have to ask for perms like this before we can use anything that needs those perms
     * during runtime. The specific permissions are declared @ Constants.PERMISSIONS
     */
    private fun checkBLEPermissions(context: Context) {
        for (perms in Constants.PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    perms
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context.getActivity() as Activity,
                    arrayOf(perms),
                    Constants.PERMISSION_CODE
                )
            }
        }
    }
}

enum class BleState {
    BLE_STATE_IDLE,
    BLE_STATE_SCANNING,
    BLE_STATE_SCAN_TIMEOUT,
    BLE_STATE_STOP
}