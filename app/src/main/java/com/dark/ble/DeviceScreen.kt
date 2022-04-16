package com.dark.ble

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
internal fun DeviceScreen(navController: NavHostController) {
    Scaffold() {
        Column() {
            Text(text = "Test")
        }
    }
}