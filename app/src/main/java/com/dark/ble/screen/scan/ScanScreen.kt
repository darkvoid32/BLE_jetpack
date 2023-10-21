package com.dark.ble.screen.scan

import android.annotation.SuppressLint
import android.bluetooth.le.*
import android.content.*
import android.os.*
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.dark.ble.R
import com.dark.ble.data.ble.BLEViewModel
import com.dark.ble.data.ble.BleState
import com.dark.ble.ui.DeviceCard
import com.dark.ble.ui.standardColumn
import com.dark.ble.utils.Constants.TAG
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

/**
 * Screen to scan and connect to BLE device
 *
 * @param navController NavHostController
 */
@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("MissingPermission")
@Composable
internal fun ScanScreen(
    bleViewModel: BLEViewModel,
    navController: NavHostController,
    showSnackbar: (String, SnackbarDuration) -> Unit,
) {
    val context = LocalContext.current

    val refreshScope = rememberCoroutineScope()
    val refreshing = remember { mutableStateOf(value = false) }
    fun refresh() = refreshScope.launch {
        refreshing.value = true
        delay(timeMillis = 500)
        bleViewModel.startScan(context = context)
        refreshing.value = false
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing.value,
        onRefresh = ::refresh
    )

    val connectionState = remember { mutableStateOf(value = "Scanning for device") }
    val deviceList = bleViewModel.leDeviceList.collectAsState()
    val bluetoothFunctionality = bleViewModel.bluetoothFunctionality.observeAsState()

    if (bleViewModel.bleScanState != BleState.BLE_STATE_SCANNING) {
        bleViewModel.bleScanState = BleState.BLE_STATE_IDLE
    }

    bleViewModel.CheckBluetoothEnabled(context = context, showSnackbar = showSnackbar)

    if (bleViewModel.bleScanState == BleState.BLE_STATE_IDLE) {
        Log.d(TAG, "Start Scanning!")
        bleViewModel.startScan(context = context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(state = pullRefreshState)
            .verticalScroll(rememberScrollState())
    ) {
        // No Devices
        if (deviceList.value.isEmpty()) {
            Column(
                modifier = Modifier
                    .standardColumn()
                    .align(Alignment.Center)
            ) {
                Column(modifier = Modifier.standardColumn()) {
                    AsyncImage(
                        model = R.drawable.scan_icon,
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(horizontal = 100.dp, vertical = 2.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
                Column(modifier = Modifier.standardColumn()) {
                    Text(
                        text = if (bluetoothFunctionality.value == true) connectionState.value else "Bluetooth not enabled!",
                        fontSize = 20.sp,
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                deviceList.value.forEach {
                    DeviceCard(it.address, navController = navController)
                }
            }
        }
        PullRefreshIndicator(refreshing = refreshing.value, state = pullRefreshState, modifier = Modifier.align(TopCenter))
    }
}
