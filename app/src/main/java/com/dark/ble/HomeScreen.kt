package com.dark.ble

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
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
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.dark.ble.data.BLEConstant
import com.dark.ble.data.BLEConstant.TAG

@Composable
internal fun HomeScreen(navController: NavHostController) {

    val context = LocalContext.current
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter = bluetoothManager.adapter
    val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    val bluetoothGatt: BluetoothGatt

    var scanning = false
    val handler = Handler(Looper.getMainLooper())
    val leDeviceList = remember { mutableStateListOf<BluetoothDevice>() }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG,"PERMISSION GRANTED")
        } else {
            Log.d(TAG,"PERMISSION DENIED")
        }
    }

    if (!bluetoothAdapter.isEnabled) {
        launcher.launch(android.Manifest.permission.BLUETOOTH)
    }

    val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            leDeviceList.add(result.device)
            Log.d(TAG, "${result.device}")
        }
    }

    for (perms in BLEConstant.PERMISSIONS) {
        if (ContextCompat.checkSelfPermission(context, perms) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context.getActivity() as Activity, arrayOf(perms), BLEConstant.PERMISSION_CODE)
        }
    }

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
                    scanning =
                        scanLeDevice(context, scanning, leScanCallback, handler, bluetoothLeScanner)
                }
            ) {
                Text(text = "Scan")
            }
            Text(
                text = "Available Devices",
                modifier = Modifier.padding(all = 16.dp)
            )
            LazyColumn {
                items(leDeviceList) { device ->
                    Button(
                        onClick = {
                            //bluetoothGatt = device.connectGatt(context, false, gattCallback)
                        },
                        modifier = Modifier.padding(all = 16.dp)
                    ) {
                        Text(text = device.name)
                        Text(text = device.address)
                    }
                }
            }
        }
    }
}

fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

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