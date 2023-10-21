package com.dark.ble.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = White,
    primaryVariant = White,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

private val NParkPalette = lightColors(
    primary = ble_theme_primary,
    primaryVariant = ble_theme_primary_light,
    onPrimary = ble_theme_onPrimary,
    secondary = ble_theme_secondary,
    background = ble_theme_background,
)

@Composable
fun BLETheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    //val colors = if (darkTheme) {
    //    DarkColorPalette
    //} else {
    //    LightColorPalette
    //}
    val colors = NParkPalette

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}