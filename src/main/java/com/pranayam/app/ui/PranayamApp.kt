package com.pranayam.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.pranayam.app.ui.navigation.PranayamNavGraph
import com.pranayam.app.ui.theme.PranayamTheme

@Composable
fun PranayamApp() {
    PranayamTheme {
        val navController = rememberNavController()
        PranayamNavGraph(navController = navController)
    }
}

/**
 * A root theme wrapper for the application.
 * In a real project, this would be defined in UI/Theme/Theme.kt
 */
@Composable
fun PranayamTheme(content: @Composable () -> Unit) {
    androidx.compose.material3.MaterialTheme(
        colorScheme = androidx.compose.material3.lightColorScheme(),
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
