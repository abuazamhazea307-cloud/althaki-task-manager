package com.example.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Tasks : Screen("tasks")
    object Settings : Screen("settings")
    object About : Screen("about")
    object LanguageInfo : Screen("language_info")
    object GeneralSettings : Screen("general_settings")
}
