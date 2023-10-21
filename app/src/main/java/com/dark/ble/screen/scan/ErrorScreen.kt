package com.dark.ble.screen.scan

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.dark.ble.R
import com.dark.ble.data.ble.BLEViewModel
import com.dark.ble.data.ble.BleState
import com.dark.ble.screen.Screen
import com.dark.ble.ui.standardColumn
import com.dark.ble.utils.Constants.TAG

/**
 * Screen to scan and connect to BLE controller
 *
 * @param navController NavHostController
 */
@Composable
internal fun ErrorScreen(bleViewModel: BLEViewModel, navController: NavHostController) {
    if (bleViewModel.bleScanState == BleState.BLE_STATE_SCAN_TIMEOUT) {
        bleViewModel.bleScanState = BleState.BLE_STATE_STOP
        Log.d(TAG, "BLE_STATE_STOP DUE TO SCAN TIMEOUT")
    }
    val errorMessage = "Device Not Found !"

    Log.d(TAG, "Error Screen Start")

    val connectionState by remember { mutableStateOf(errorMessage) }
    var ticks by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.standardColumn().align(Alignment.Center)) {
            ErrorPicture()

            Column(modifier = Modifier.standardColumn()) {
                Text(
                    text = connectionState,
                    fontSize = 20.sp,
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                )
            }
        }

        if (ticks == 2) {
            ticks = 0
            bleViewModel.bleScanState = BleState.BLE_STATE_IDLE
            navController.navigate(Screen.ScanScreen.route)
        }
    }
}

@Composable
fun ErrorPicture() {
    Column(modifier = Modifier.standardColumn()) {
        AsyncImage(
            model = R.drawable.warning_logo,
            contentDescription = "App Logo",
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 100.dp, vertical = 2.dp),
            contentScale = ContentScale.Fit,
        )

    }
}

