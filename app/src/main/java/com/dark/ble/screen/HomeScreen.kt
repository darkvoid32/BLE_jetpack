package com.dark.ble.screen

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dark.ble.data.ble.BLEViewModel
import com.dark.ble.navigation.NavigationItem
import com.dark.ble.navigation.ScanNavigation
import com.dark.ble.ui.rememberSnackbarAppState
import com.dark.ble.ui.theme.ble_theme_onSecondary
import com.dark.ble.ui.theme.ble_theme_secondary
import com.dark.ble.utils.navigateSingleTop
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
internal fun HomeScreen(
    bleViewModel: BLEViewModel
) {
    val navScanController = rememberNavController()
    val snackbarAppState = rememberSnackbarAppState()

    // We hid the bars during splashscreen so we have to show them again
    ShowBars()
    // Setting up the overall screen settings
    // In the scaffold contains all the individual screen data
    Scaffold(
        //topBar = { BLEAppBar() },
        bottomBar = { BottomNavigationBar(navController = navScanController) },
        scaffoldState = snackbarAppState.scaffoldState
    ) { innerPadding ->
        Box(modifier= Modifier.padding(innerPadding)) {
            ScanNavigation(
                navController = navScanController,
                showSnackbar = { message, duration -> snackbarAppState.showSnackbar(message = message, duration = duration) },
                bleViewModel = bleViewModel,
            )
        }
    }
}

@Composable
fun ShowBars() {
    rememberSystemUiController().apply {
        this.isSystemBarsVisible = true
        this.isNavigationBarVisible = true
        this.isStatusBarVisible = true
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Scan,
        NavigationItem.Settings
    )
    val scanScreenGroup = listOf(Screen.ScanScreen.route, Screen.DeviceScreen.route)
    val settingsScreenGroup = listOf(Screen.SettingsScreen.route)
    BottomNavigation(backgroundColor = ble_theme_secondary) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route.toString()
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    when {
                        scanScreenGroup.any { currentRoute.contains(it) } -> GetIcon(item = item, screen = Screen.ScanScreen.route)
                        settingsScreenGroup.any { currentRoute.contains(it) } -> GetIcon(item = item, screen = Screen.SettingsScreen.route)
                        else -> Icon(painter = painterResource(id = item.iconOutline), contentDescription = item.title)
                    }
                },
                selectedContentColor = ble_theme_onSecondary,
                unselectedContentColor = ble_theme_onSecondary.copy(0.4f),
                selected = when {
                    scanScreenGroup.any { currentRoute.contains(it) } -> item.route == Screen.ScanScreen.route
                    settingsScreenGroup.any { currentRoute.contains(it) } -> item.route == Screen.SettingsScreen.route
                    else -> false
                },
                onClick = {
                    navigateSingleTop(navController = navController, route = item.route)
                }
            )
        }
    }
}

@Composable
fun GetIcon(item: NavigationItem, screen: String) {
    if (item.route == screen)
        Icon(painter = painterResource(id = item.icon), contentDescription = item.title)
    else
        Icon(painter = painterResource(id = item.iconOutline), contentDescription = item.title)
}

/**
 * We use this extension function since the context may not be the ComponentActivity, but a
 * ContextWrapper, which will cause the functions that use it to fail. Hence if it is a
 * ContextWrapper we call getActivity() recursively until we get the Activity
 *
 * @return ComponentActivity? object if activity exists
 */
fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}