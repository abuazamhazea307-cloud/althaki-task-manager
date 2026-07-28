package com.example

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.lifecycleScope
import androidx.compose.ui.draw.drawWithContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.launch
import com.example.features.tasks.createNotificationChannel
import com.example.navigation.NavGraph
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ThemeManager

@SuppressLint("InvalidFragmentVersionForActivityResult")
class MainActivity : ComponentActivity() {

  private var isFirstFrameDrawn = false

  init {
    com.example.debug.StartupTracer.mark("MAIN_ACTIVITY_CONSTRUCTOR")
  }

  private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted: Boolean ->
    // Permisssion granted/denied handler
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    com.example.debug.StartupTracer.mark("MAIN_ACTIVITY_ONCREATE_BEGIN")
    val splashScreen = installSplashScreen()
    super.onCreate(savedInstanceState)
    splashScreen.setKeepOnScreenCondition {
      !isFirstFrameDrawn
    }
    ThemeManager.init(this)
    enableEdgeToEdge()

    // Initialize notification channel
    createNotificationChannel(this)

    // Request notification permission dynamically on Android 13+ (API 33)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(
          this,
          Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }

    com.example.debug.StartupTracer.mark("BEFORE_SET_CONTENT")

    setContent {
      com.example.debug.StartupTracer.mark("SET_CONTENT_BEGIN")
      androidx.compose.runtime.SideEffect {
        com.example.debug.StartupTracer.mark("FIRST_COMPOSITION")
      }

      // Run background initialization after composition starts, keeping the main thread free
      androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
          com.example.features.settings.GeneralSettingsManager.init(applicationContext)
          com.example.features.settings.TaskSettingsManager.init(applicationContext)
          com.example.features.settings.ReminderSettingsManager.init(applicationContext)
          com.example.features.tasks.TaskLocalStore.initAsync(applicationContext)
          com.example.features.tasks.TomorrowAutoMigrationEngine.checkAndMigrate(applicationContext)
        }
      }

      androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier
          .fillMaxSize()
          .drawWithContent {
            drawContent()
            isFirstFrameDrawn = true
            com.example.debug.StartupTracer.mark("FIRST_FRAME_DRAWN")
            com.example.debug.StartupTracer.mark("FIRST_FRAME_VISIBLE")
          }
      ) {
        MyApplicationTheme {
          val navController = rememberNavController()
          NavGraph(navController = navController)
        }
      }
    }
  }

  override fun onStart() {
    super.onStart()
    lifecycleScope.launch(kotlinx.coroutines.Dispatchers.IO) {
      com.example.features.tasks.TomorrowAutoMigrationEngine.checkAndMigrate(applicationContext)
    }
  }
}

