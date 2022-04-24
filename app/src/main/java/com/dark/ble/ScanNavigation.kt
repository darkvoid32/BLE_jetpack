package com.dark.ble

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ScanNavigation(navController: NavHostController) {
    val animationDuration = 400

    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.ScanScreen.route,
    ) {
        // Scan Screen
        composable(
            route = Screen.ScanScreen.route,
            popEnterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.End, animationSpec = tween(animationDuration))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
        ) { ScanScreen(navController = navController) }

        // Device Screen
        composable(
            route = Screen.DeviceScreen.route + "/{deviceAddress}",
            arguments = listOf(navArgument("deviceAddress") { type = NavType.StringType }),
            popEnterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.End, animationSpec = tween(animationDuration))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Down, animationSpec = tween(animationDuration))
            },
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("deviceAddress")?.let {
                DeviceScreen(navController = navController, deviceAddress = it)
            }
        }

        // Profile Screen
        composable(
            route = Screen.ProfileScreen.route,
            popEnterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.End, animationSpec = tween(animationDuration))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Down, animationSpec = tween(animationDuration))
            },
        ) { ProfileScreen(navController = navController) }
    }
}