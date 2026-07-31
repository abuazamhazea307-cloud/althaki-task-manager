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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Alignment
import androidx.compose.animation.*
import androidx.compose.material3.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.BorderStroke
import android.content.Intent
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
    super.onCreate(savedInstanceState)
    val startTime = System.currentTimeMillis()

    // Start background initialization ASAP on a background dispatcher, running in parallel with UI setup
    lifecycleScope.launch(kotlinx.coroutines.Dispatchers.IO) {
      com.example.features.settings.GeneralSettingsManager.init(applicationContext)
      com.example.features.settings.TaskSettingsManager.init(applicationContext)
      com.example.features.settings.ReminderSettingsManager.init(applicationContext)
      com.example.features.tasks.TaskLocalStore.initAsync(applicationContext)
      com.example.features.tasks.TomorrowAutoMigrationEngine.checkAndMigrate(applicationContext)
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
      androidx.compose.runtime.remember {
        com.example.debug.StartupTracer.mark("SET_CONTENT_BEGIN")
        Unit
      }
      androidx.compose.runtime.SideEffect {
        com.example.debug.StartupTracer.mark("FIRST_COMPOSITION")
      }

      androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier
          .fillMaxSize()
          .drawWithContent {
            drawContent()
            if (!isFirstFrameDrawn) {
              isFirstFrameDrawn = true
              com.example.debug.StartupTracer.mark("FIRST_FRAME_DRAWN")
              com.example.debug.StartupTracer.mark("FIRST_FRAME_VISIBLE")
            }
          }
      ) {
        MyApplicationTheme {
          val navController = rememberNavController()
          Box(
            modifier = Modifier.fillMaxSize()
          ) {
            NavGraph(navController = navController)

            val activeAlertState by com.example.features.tasks.AlarmNotificationManager.activeAlert.collectAsState()
            val context = androidx.compose.ui.platform.LocalContext.current

            activeAlertState?.let { alert ->
              AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
              ) {
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .statusBarsPadding()
                    .shadow(12.dp, RoundedCornerShape(20.dp)),
                  shape = RoundedCornerShape(20.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                  )
                ) {
                  Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                      Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                          .size(48.dp)
                          .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                            shape = CircleShape
                          )
                      ) {
                        Text(
                          text = "💎",
                          style = MaterialTheme.typography.headlineMedium
                        )
                      }

                      Column(
                        modifier = Modifier.weight(1f)
                      ) {
                        Text(
                          text = alert.taskTitle,
                          style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                          ),
                          maxLines = 2,
                          overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                          text = alert.taskStartTime,
                          style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                          )
                        )
                      }
                    }

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                      Button(
                        onClick = {
                          val stopIntent = Intent(context, com.example.features.tasks.AlarmService::class.java).apply {
                            action = com.example.features.tasks.AlarmService.ACTION_STOP
                          }
                          context.startService(stopIntent)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.primary,
                          contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                      ) {
                        Text(
                          text = "✅ تم التنفيذ",
                          style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                          )
                        )
                      }

                      OutlinedButton(
                        onClick = {
                          val snoozeIntent = Intent(context, com.example.features.tasks.AlarmService::class.java).apply {
                            action = com.example.features.tasks.AlarmService.ACTION_SNOOZE
                          }
                          context.startService(snoozeIntent)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                          contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                          width = 1.dp,
                          color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                      ) {
                        Text(
                          text = "⏰ تأجيل 5 دقائق",
                          style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                          )
                        )
                      }
                    }
                  }
                }
              }
            }
          }
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

