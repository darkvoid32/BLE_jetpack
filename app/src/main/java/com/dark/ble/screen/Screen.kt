package com.dark.ble.screen

sealed class Screen(val route: String) {
    object HomeScreen : Screen(ScreenDestinations.HOME_SCREEN)
    // Scanning & Device
    object ErrorScreen : Screen(ScreenDestinations.ERROR_SCREEN)
    object ScanScreen : Screen(ScreenDestinations.SCAN_SCREEN)
    object DeviceScreen : Screen(ScreenDestinations.DEVICE_SCREEN)
    // Setting
    object SettingsScreen : Screen(ScreenDestinations.SETTINGS_SCREEN)
}