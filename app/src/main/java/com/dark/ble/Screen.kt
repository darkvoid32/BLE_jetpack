package com.dark.ble

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home_screen")
    object ScanScreen : Screen("scan_screen")
    object DeviceScreen : Screen("device_screen")
    object ProfileScreen : Screen("profile_screen")
}