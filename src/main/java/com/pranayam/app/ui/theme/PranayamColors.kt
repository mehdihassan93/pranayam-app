package com.pranayam.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object PranayamColors {
    // Primary Gradient
    val RosePrimary = Color(0xFFD946A6)
    val RoseSecondary = Color(0xFFEC4899)
    val PurplePrimary = Color(0xFF8B5CF6)
    
    // Accent
    val WarmAccent = Color(0xFFF59E0B)
    val GoldAccent = Color(0xFFFBBF24)
    
    // Semantic
    val Success = Color(0xFF10B981)
    val Warning = Color(0xFFF59E0B)
    val Error = Color(0xFFEF4444)
    val Info = Color(0xFF3B82F6)
    
    // WhatsApp-inspired Chat Colors
    val ChatBubbleSent = Color(0xFFDCF8C6)      // Light green
    val ChatBubbleReceived = Color(0xFFFFFFFF)   // White
    val ChatBubbleSentDark = Color(0xFF056162)   // Dark teal
    val ChatBubbleReceivedDark = Color(0xFF262D31) // Dark gray
    val ChatBackground = Color(0xFFECE5DD)        // Beige
    val ChatBackgroundDark = Color(0xFF0B141A)    // Very dark
    val ChatInputBg = Color(0xFFF0F2F5)           // Light gray
    val ChatInputBgDark = Color(0xFF2A2F32)       // Dark gray
    
    // Light Mode
    val BackgroundLight = Color(0xFFFAFAFA)
    val SurfaceLight = Color(0xFFFFFFFF)
    val TextPrimaryLight = Color(0xFF1F1F1F)
    val TextSecondaryLight = Color(0xFF737373)
    val TextTertiaryLight = Color(0xFF999999)
    val DividerLight = Color(0xFFE5E5E5)
    
    // Dark Mode
    val BackgroundDark = Color(0xFF0F0F0F)
    val SurfaceDark = Color(0xFF1A1A1A)
    val TextPrimaryDark = Color(0xFFF5F5F5)
    val TextSecondaryDark = Color(0xFFA3A3A3)
    val TextTertiaryDark = Color(0xFF666666)
    val DividerDark = Color(0xFF2D2D2D)
}

val PrimaryGradient = Brush.linearGradient(
    colors = listOf(
        PranayamColors.RosePrimary,
        PranayamColors.RoseSecondary
    )
)

val PrimaryVerticalGradient = Brush.verticalGradient(
    colors = listOf(
        PranayamColors.RosePrimary,
        PranayamColors.PurplePrimary
    )
)

val GoldGradient = Brush.linearGradient(
    colors = listOf(
        PranayamColors.WarmAccent,
        PranayamColors.GoldAccent
    )
)
