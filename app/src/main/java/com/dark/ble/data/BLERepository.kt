package com.dark.ble.data

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dark.ble.getActivity

object BLERepository {

    private val leDeviceList =  mutableStateListOf<BluetoothDevice>()

    fun getDeviceList(): MutableList<BluetoothDevice> {
        return leDeviceList
    }

    fun getDevice(bluetoothDeviceAddress: String, context: Context): BluetoothDevice? {
        for (perms in BLEConstant.PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, perms) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context.getActivity() as Activity, arrayOf(perms), BLEConstant.PERMISSION_CODE)
            }
        }
        for (device in leDeviceList) {
            if (device.address == bluetoothDeviceAddress)
                return device
        }
        return null
    }

    fun addDevice(bluetoothDevice: BluetoothDevice, context: Context) {
        for (perms in BLEConstant.PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, perms) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context.getActivity() as Activity, arrayOf(perms), BLEConstant.PERMISSION_CODE)
            }
        }
        if (bluetoothDevice !in leDeviceList)
            leDeviceList.add(bluetoothDevice)
    }

    fun removeDevice(bluetoothDeviceAddress: String, context: Context) {
        for (perms in BLEConstant.PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, perms) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context.getActivity() as Activity, arrayOf(perms), BLEConstant.PERMISSION_CODE)
            }
        }
        for(device in leDeviceList) {
            if (device.address == bluetoothDeviceAddress)
                leDeviceList.remove(device)
        }
    }

    fun clearDeviceList() {
        leDeviceList.clear()
    }
}