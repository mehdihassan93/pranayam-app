package com.pranayam.app.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Auth : Screen("auth")
    object MatchCelebration : Screen("match_celebration/{userPhoto}/{matchPhoto}/{matchName}") {
        fun createRoute(userPhoto: String, matchPhoto: String, matchName: String) =
            "match_celebration/$userPhoto/$matchPhoto/$matchName"
    }
    object Onboarding : Screen("onboarding")
    object Main : Screen("main")
    object EditProfile : Screen("edit_profile")
    object ProfileDetail : Screen("profile_detail/{profileId}") {
        fun createRoute(profileId: String) = "profile_detail/$profileId"
    }
    object Chat : Screen("chat/{conversationId}") {
        fun createRoute(conversationId: String) = "chat/$conversationId"
    }
}

sealed class MainTab(val route: String) {
    object Home : MainTab("home")
    object Matches : MainTab("matches")
    object Profile : MainTab("profile")
    object Settings : MainTab("settings")
}
