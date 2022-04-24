package com.dark.ble

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeNavigation() {
    val navController: NavHostController = rememberAnimatedNavController()
    val animationDuration = 400

    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route,
    ) {

        // Home Screen
        composable(
            route = Screen.HomeScreen.route,
            popEnterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.End, animationSpec = tween(animationDuration))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
        ) { HomeScreen(navController = navController) }
    }
}