package com.dark.ble.navigation

import com.dark.ble.R
import com.dark.ble.screen.Screen

sealed class NavigationItem(val route: String, val icon: Int, val iconOutline: Int, val title: String) {
    object Scan : NavigationItem(route = Screen.ScanScreen.route, icon = R.drawable.ic_bluetooth_searching, iconOutline = R.drawable.ic_bluetooth, title = "Scan")
    object Settings : NavigationItem(route = Screen.SettingsScreen.route, icon = R.drawable.ic_settings, iconOutline = R.drawable.ic_settings_outline, title = "Settings")
}