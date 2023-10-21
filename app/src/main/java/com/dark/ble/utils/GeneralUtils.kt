package com.dark.ble.utils

import androidx.navigation.NavController

fun navigateSingleTop(navController: NavController, route: String) {
    navController.navigate(route) {
        navController.graph.startDestinationRoute?.let { route -> popUpTo(route) }
        launchSingleTop = true
    }
}