package com.pranayam.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object Spacing {
    val XXS = 4.dp   // Tiny gaps
    val XS = 8.dp    // Base unit
    val S = 12.dp    // Small spacing
    val M = 16.dp    // Default padding
    val L = 24.dp    // Large spacing
    val XL = 32.dp   // Extra large
    val XXL = 48.dp  // Massive spacing
    val XXXL = 64.dp // Hero spacing
}

object CornerRadius {
    val XS = 4.dp    // Subtle rounding
    val S = 8.dp     // Small cards
    val M = 12.dp    // Input fields
    val L = 16.dp    // Cards
    val XL = 20.dp   // Large cards
    val XXL = 24.dp  // Buttons
    val Pill = 999.dp // Fully rounded
}

object Elevation {
    val None = 0.dp
    val Small = 2.dp    // Subtle elevation
    val Medium = 4.dp   // Cards
    val Large = 8.dp    // Modals
    val XLarge = 16.dp  // Floating action buttons
}

object IconSize {
    val XS = 16.dp
    val S = 20.dp
    val M = 24.dp  // Default
    val L = 32.dp
    val XL = 48.dp
    val XXL = 80.dp // Profile photos in cards
}

// Shadow colors with opacity
val ShadowLight = Color.Black.copy(alpha = 0.08f)
val ShadowDark = Color.Black.copy(alpha = 0.24f)
