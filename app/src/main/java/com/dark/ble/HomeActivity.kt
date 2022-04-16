package com.dark.ble

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.dark.ble.data.BLEConstant
import com.dark.ble.ui.theme.BLETheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BLETheme {
                HomeNavigation()
            }
        }
    }
}