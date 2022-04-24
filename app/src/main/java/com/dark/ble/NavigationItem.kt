package com.dark.ble

sealed class NavigationItem(var route: String, var icon: Int, var title: String) {
    object Home : NavigationItem(Screen.ScanScreen.route, R.drawable.ic_home, "Home")
    object Profile : NavigationItem(Screen.ProfileScreen.route, R.drawable.ic_profile, "Profile")
}