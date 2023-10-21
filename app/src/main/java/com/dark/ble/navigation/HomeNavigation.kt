package com.dark.ble.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dark.ble.data.ble.BLEViewModel
import com.dark.ble.screen.HomeScreen
import com.dark.ble.screen.Screen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeNavigation() {
    val navController: NavHostController = rememberAnimatedNavController()
    // Should change to add args instead of add like this, no need to add !! also
    val bleViewModel: BLEViewModel = viewModel()

    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route,
    ) {
        // Home Screen
        composable(
            route = Screen.HomeScreen.route,
        ) { HomeScreen(bleViewModel = bleViewModel) }
    }
}