package com.dark.ble

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
internal fun ProfileScreen(navController: NavHostController) {
    Scaffold {
        Text(text = "Profile screen")
    }
}