package com.dark.ble.ui

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dark.ble.R
import com.dark.ble.ui.theme.ble_theme_onPrimary
import com.dark.ble.ui.theme.ble_theme_primary

fun Modifier.standardRow(): Modifier = this.then(
    fillMaxWidth().padding(8.dp)
)

fun Modifier.standardColumn(): Modifier = this.then(
    fillMaxWidth().padding(all = 16.dp)
)

fun Modifier.imageCard(launcher: ManagedActivityResultLauncher<String, Uri?>): Modifier = this.then(
    size(100.dp).clickable { launcher.launch("image/*") }
)

fun Modifier.deviceCard(): Modifier = this.then(
    fillMaxWidth()
        .clip(RoundedCornerShape(4.dp))
        .padding(16.dp)
        //.clickable { navController.navigate(route = route) }
        .shadow(4.dp)
)

@Composable
fun DeviceCard(address: String, navController: NavHostController) {
    Card(modifier = Modifier.deviceCard()) {
        Row(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max)) {
            // Image Card
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .weight(weight = 0.3f)
                    .padding(all = 10.dp)
                    .size(64.dp)
            ) {
                androidx.compose.material.Icon(
                    painter = painterResource(id = R.drawable.ic_bluetooth),
                    contentDescription = "BLE Icon",
                    modifier = Modifier.wrapContentSize()
                )
            }

            // Text
            Column(
                modifier = Modifier
                    .weight(weight = 0.7f)
                    .align(Alignment.CenterVertically)
            ) {
                androidx.compose.material.Text(
                    text = address,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
            }

            // Icon
            androidx.compose.material.Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "Go to Pet Profile",
                modifier = Modifier
                    .weight(weight = 0.15f)
                    .align(Alignment.CenterVertically)
                    .padding(all = 16.dp)
            )
        }
    }
}


@Composable
fun ScreenTitle(title: String, navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = ble_theme_primary)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(start = 12.dp)
                .align(alignment = Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_left),
                contentDescription = "Back Button",
                tint = Color.Unspecified
            )
        }

        Text(
            text = title,
            fontSize = 18.sp,
            color = ble_theme_onPrimary,
            modifier = Modifier
                .padding(all = 16.dp)
                .align(alignment = Alignment.Center)
        )
    }
}


@Composable
fun Title(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = ble_theme_primary)
    ) {
        IconButton(
            onClick = {},
            modifier = Modifier
                .padding(start = 10.dp)
                .align(alignment = Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_notification),
                contentDescription = "Back Button",
                tint = Color.Unspecified

            )
        }

        Text(
            text = title,
            fontSize = 18.sp,
            //   fontWeight = FontWeight.Bold,
            color = ble_theme_onPrimary,
            modifier = Modifier
                .padding(all = 16.dp)
                .align(alignment = Alignment.CenterEnd)
        )
    }
}