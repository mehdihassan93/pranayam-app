package com.pranayam.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

enum class WindowSize {
    COMPACT,  // < 600dp (phones)
    MEDIUM,   // 600-840dp (tablets portrait)
    EXPANDED  // > 840dp (tablets landscape, foldables)
}

@Composable
fun rememberWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current
    return when {
        configuration.screenWidthDp < 600 -> WindowSize.COMPACT
        configuration.screenWidthDp < 840 -> WindowSize.MEDIUM
        else -> WindowSize.EXPANDED
    }
}

/**
 * A wrapper component that demonstrates how the UI adapts to different screen sizes.
 */
@Composable
fun AdaptiveLayoutContainer(
    windowSize: WindowSize,
    compactContent: @Composable () -> Unit,
    mediumContent: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit
) {
    when (windowSize) {
        WindowSize.COMPACT -> compactContent()
        WindowSize.MEDIUM -> mediumContent()
        WindowSize.EXPANDED -> expandedContent()
    }
}
