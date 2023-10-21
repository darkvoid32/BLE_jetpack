package com.dark.ble.screen.setting

import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.dark.ble.data.ble.BLEViewModel

@Composable
internal fun SettingsScreen(navController: NavHostController, bleViewModel: BLEViewModel, showSnackbar: (String, SnackbarDuration) -> Unit) {
    bleViewModel.stopScan(context = LocalContext.current)
}