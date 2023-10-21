package com.dark.ble.utils

import android.Manifest
import android.net.Uri
import com.dark.ble.R

fun getURLForResource(resourceId: Int): String {
    //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
    return Uri.parse("android.resource://" + R::class.java.getPackage()!!.name + "/" + resourceId)
        .toString()
}

object Constants {
    // BLE
    const val PERMISSION_CODE = 1
    const val SCAN_PERIOD: Long = 10000
    const val COUNTDOWN_INTERVAL: Long = 1000
    const val SCAN_PERIOD_SEC: Int = (SCAN_PERIOD / 1000).toInt()

    val PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT
    )

    const val REQUEST_ENABLE_BT = 1

    // Logging
    const val TAG = "BLE"
}