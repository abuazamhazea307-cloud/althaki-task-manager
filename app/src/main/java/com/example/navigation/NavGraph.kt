package com.example.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.features.home.HomeScreen
import com.example.features.settings.AboutScreen
import com.example.features.settings.LanguageInfoScreen
import com.example.features.settings.SettingsScreen
import com.example.features.splash.SplashScreen
import com.example.features.tasks.TasksScreen

@Composable
fun NavGraph(navController: NavHostController) {
  NavHost(
    navController = navController,
    startDestination = Screen.Splash.route
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
  }
}
