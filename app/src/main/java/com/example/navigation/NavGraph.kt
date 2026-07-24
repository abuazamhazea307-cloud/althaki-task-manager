package com.example.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.features.home.HomeScreen
import com.example.features.settings.AboutScreen
import com.example.features.settings.LanguageInfoScreen
import com.example.features.settings.SettingsScreen
import com.example.features.settings.GeneralSettingsScreen
import com.example.features.settings.TaskSettingsScreen
import com.example.features.splash.SplashScreen
import com.example.features.tasks.TasksScreen

import com.example.features.settings.GeneralSettingsManager

@Composable
fun NavGraph(navController: NavHostController) {
  val startRoute = if (GeneralSettingsManager.showSplash) Screen.Splash.route else Screen.Home.route
  NavHost(
    navController = navController,
    startDestination = startRoute
  ) {
    composable(Screen.Splash.route) {
      SplashScreen(navController = navController)
    }
    composable(Screen.Home.route) {
      HomeScreen(navController = navController)
    }
    composable(Screen.Tasks.route) {
      TasksScreen(navController = navController)
    }
    composable(Screen.Settings.route) {
      SettingsScreen(navController = navController)
    }
    composable(Screen.About.route) {
      AboutScreen(navController = navController)
    }
    composable(Screen.LanguageInfo.route) {
      LanguageInfoScreen(navController = navController)
    }
    composable(Screen.GeneralSettings.route) {
      GeneralSettingsScreen(navController = navController)
    }
    composable(Screen.TaskSettings.route) {
      TaskSettingsScreen(navController = navController)
    }
  }
}
