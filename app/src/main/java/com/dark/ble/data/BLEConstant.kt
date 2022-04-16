package com.dark.ble.data

import android.Manifest

object BLEConstant {
    const val PERMISSION_CODE = 1
    const val SCAN_PERIOD: Long = 10000
    const val TAG = "BLETest"

    val PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
}