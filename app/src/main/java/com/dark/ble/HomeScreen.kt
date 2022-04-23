package com.dark.ble

import android.app.Activity
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.dark.ble.data.BLEConstant
import com.dark.ble.data.BLEConstant.TAG
import com.dark.ble.data.BLERepository

@Composable
internal fun HomeScreen(navController: NavHostController) {

    val context = LocalContext.current
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter = bluetoothManager.adapter
    val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    var scanning = false
    val handler = Handler(Looper.getMainLooper())

    /**
     * Check if bluetooth access is granted or not
     * launcher not used as of now, but can be used later to ask user for perms again if denied
     */
    if (!bluetoothAdapter.isEnabled) {
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "PERMISSION GRANTED")
            } else {
                Log.d(TAG, "PERMISSION DENIED")
            }
        }.launch(android.Manifest.permission.BLUETOOTH)
    }

    /**
     * Scan callback for bluetoothLeScanner
     */
    val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            BLERepository.addDevice(result.device, context)
            Log.d(TAG, "Device scanned : ${result.device}")
        }
    }

    /**
     * As per android docs :
     * If you declare any dangerous permissions, and if your app is installed on a device that runs
     * Android 6.0 (API level 23) or higher, you must request the dangerous permissions at runtime
     * by following the steps in this guide.
     *
     * We have to ask for perms like this before we can use anything that needs those perms
     * during runtime. The specific permissions are declared @ BLEConstant.PERMISSIONS
     */
    for (perms in BLEConstant.PERMISSIONS) {
        if (ContextCompat.checkSelfPermission(context, perms) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context.getActivity() as Activity, arrayOf(perms), BLEConstant.PERMISSION_CODE)
        }
    }

    /**
     * Simple scaffold to display button and data
     */
    Scaffold(
        backgroundColor = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    BLERepository.clearDeviceList()
                    scanning = scanLeDevice(context, scanning, leScanCallback, handler, bluetoothLeScanner)
                }
            ) {
                Text(text = "Scan")
            }
            Text(
                text = "Available Devices",
                modifier = Modifier.padding(all = 16.dp)
            )
            LazyColumn {
                items(BLERepository.getDeviceList()) { device ->
                    Button(
                        onClick = {
                            bluetoothLeScanner.stopScan(leScanCallback)
                            navController.navigate(Screen.DeviceScreen.route + "/${device.address}")
                        },
                        modifier = Modifier.padding(all = 16.dp)
                    ) {
                        Column {
                            if (device.name != null)
                                Text(text = "Name: ${device.name}")
                            if (device.address != null)
                                Text(text = "Address: ${device.address}")
                        }
                    }
                }
            }
        }
    }
}


/**
 * We use this extension function since the context may not be the ComponentActivity, but a
 * ContextWrapper, which will cause the functions that use it to fail. Hence if it is a
 * ContextWrapper we call getActivity() recursively until we get the Activity
 */
fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}


/**
 * Simply scans for BLE devices nearby.
 *
 * @param context: The context for perms
 * @param scanning: Check if device is scanning already
 * @param leScanCallback: Callback object to give results
 * @param handler: Lets us stop scanning after period of time
 * @param bluetoothLeScanner: Scanner object
 */
fun scanLeDevice(
    context: Context,
    scanning: Boolean,
    leScanCallback: ScanCallback,
    handler: Handler,
    bluetoothLeScanner: BluetoothLeScanner
): Boolean {
    for (perms in BLEConstant.PERMISSIONS) {
        if (ContextCompat.checkSelfPermission(context, perms) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context.getActivity() as Activity, arrayOf(perms), BLEConstant.PERMISSION_CODE)
        }
    }
    return if (!scanning) {
        Log.d(TAG, "Scanning")
        handler.postDelayed({
            Log.d(TAG, "Stop Scanning")
            if (!scanning)
                bluetoothLeScanner.stopScan(leScanCallback)
        }, BLEConstant.SCAN_PERIOD)
        bluetoothLeScanner.startScan(leScanCallback)
        true
    } else {
        Log.d(TAG, "Stop Scanning")
        bluetoothLeScanner.stopScan(leScanCallback)
        false
    }
}