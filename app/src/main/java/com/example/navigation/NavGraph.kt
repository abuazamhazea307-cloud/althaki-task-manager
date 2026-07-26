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
import com.example.features.settings.ReminderSettingsScreen
import com.example.features.tasks.TasksScreen
import com.example.features.tasks.TomorrowTasksScreen

@Composable
fun NavGraph(navController: NavHostController) {
  NavHost(
    navController = navController,
    startDestination = Screen.Home.route
  ) {
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
    composable(Screen.ReminderSettings.route) {
      ReminderSettingsScreen(navController = navController)
    }
    composable(Screen.TomorrowTasks.route) {
      TomorrowTasksScreen(navController = navController)
    }
  }
}
