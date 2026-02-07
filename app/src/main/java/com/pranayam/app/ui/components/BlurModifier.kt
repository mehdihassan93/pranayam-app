package com.pranayam.app.ui.components

import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Applies a blur effect to a composable when enabled.
 * Falls back to no blur on older Android versions (< API 31).
 *
 * @param enabled Whether to apply the blur effect
 * @param radius The blur radius in Dp
 */
fun Modifier.conditionalBlur(
    enabled: Boolean,
    radius: Dp
): Modifier = if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    this.blur(radius)
} else {
    this
}

/**
 * Standard blur values for guest mode
 */
object GuestBlur {
    val PhotoBlur = 8.dp
    val InfoBlur = 16.dp
}
