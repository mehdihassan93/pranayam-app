package com.pranayam.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PranayamColors.RosePrimary,
    secondary = PranayamColors.PurplePrimary,
    tertiary = PranayamColors.WarmAccent,
    background = PranayamColors.BackgroundLight,
    surface = Color.White,
    error = PranayamColors.Error,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = PranayamColors.TextPrimaryLight,
    onSurface = PranayamColors.TextPrimaryLight,
    onError = Color.White
)

@Composable
fun PranayamTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
