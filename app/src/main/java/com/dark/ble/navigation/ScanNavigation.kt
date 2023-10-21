package com.dark.ble.navigation

import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dark.ble.data.ble.BLEViewModel
import com.dark.ble.screen.Screen
import com.dark.ble.screen.scan.DeviceScreen
import com.dark.ble.screen.scan.ErrorScreen
import com.dark.ble.screen.scan.ScanScreen
import com.dark.ble.screen.setting.SettingsScreen

@Composable
fun ScanNavigation(
    navController: NavHostController,
    showSnackbar: (String, SnackbarDuration) -> Unit,
    bleViewModel: BLEViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ScanScreen.route,
    ) {
        // Device Screen
        composable(
            route = "${Screen.DeviceScreen.route}/{deviceAddress}",
            arguments = listOf(navArgument("deviceAddress") { type = NavType.StringType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("deviceAddress")?.let {
                DeviceScreen(bleViewModel = bleViewModel, navController = navController,deviceAddress = it)
            }
        }

        // Settings Screen
        composable(route = Screen.SettingsScreen.route) {
            SettingsScreen(navController = navController, bleViewModel = bleViewModel, showSnackbar = showSnackbar)
        }

        // Scan Screen
        composable(route = Screen.ScanScreen.route) {
            ScanScreen(
                bleViewModel = bleViewModel,
                navController = navController,
                showSnackbar = showSnackbar
            )
        }

        // Error Screen
        composable(route = Screen.ErrorScreen.route) {
            ErrorScreen( bleViewModel = bleViewModel, navController = navController)
        }
    }
}