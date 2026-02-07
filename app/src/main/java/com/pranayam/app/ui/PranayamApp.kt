package com.pranayam.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.pranayam.app.di.UserSessionManager
import com.pranayam.app.ui.navigation.PranayamNavGraph
import com.pranayam.app.ui.theme.PranayamTheme

@Composable
fun PranayamApp(sessionManager: UserSessionManager) {
    PranayamTheme {
        val navController = rememberNavController()
        PranayamNavGraph(
            navController = navController,
            sessionManager = sessionManager
        )
    }
}
