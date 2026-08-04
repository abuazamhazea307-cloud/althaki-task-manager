package com.example.features.tasks

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlarmActivity : ComponentActivity() {

    private val alarmDismissReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.example.ALARM_DISMISSED") {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Wake screen, show above lockscreen, keep screen awake
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Register dismiss receiver
        val filter = IntentFilter("com.example.ALARM_DISMISSED")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(alarmDismissReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(alarmDismissReceiver, filter)
        }

        val taskId = intent.getStringExtra("task_id") ?: ""
        val taskTitle = intent.getStringExtra("task_title") ?: "Task Reminder"
        val taskStartTime = intent.getStringExtra("task_start_time") ?: ""
        val ringtoneUri = intent.getStringExtra("ringtone_uri")

        AlarmSoundPlayer.start(this, ringtoneUri)

        val initialShowSnooze = intent.getBooleanExtra("show_snooze_dialog", false)

        setContent {
            MyApplicationTheme {
                val maxSnooze = com.example.features.settings.ReminderSettingsManager.maxSnoozeCount
                val snoozeCount = com.example.features.settings.ReminderSettingsManager.getSnoozeCount(this, taskId)
                val showSnoozeButton = maxSnooze == -1 || snoozeCount < maxSnooze

                var showSnoozeDialog by remember { mutableStateOf(initialShowSnooze) }

                AlarmScreen(
                    taskTitle = taskTitle,
                    taskStartTime = taskStartTime,
                    showSnoozeButton = showSnoozeButton,
                    onStopClick = {
                        AlarmSoundPlayer.stop()
                        val completeIntent = Intent(this, ReminderReceiver::class.java).apply {
                            action = "com.example.ACTION_COMPLETE"
                            putExtra("task_id", taskId)
                        }
                        sendBroadcast(completeIntent)
                        finish()
                    },
                    onSnoozeClick = {
                        showSnoozeDialog = true
                    }
                )

                if (showSnoozeDialog) {
                    val defaultSnooze = com.example.features.settings.ReminderSettingsManager.defaultSnoozeDuration
                    var selectedDuration by remember { mutableStateOf(defaultSnooze) }
                    val snoozeOptions = listOf(5, 10, 15, 30)

                    AlertDialog(
                        onDismissRequest = { showSnoozeDialog = false },
                        title = {
                            Text(
                                text = "مدة الغفوة",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                snoozeOptions.forEach { duration ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedDuration = duration }
                                            .padding(vertical = 6.dp)
                                    ) {
                                        RadioButton(
                                            selected = (selectedDuration == duration),
                                            onClick = { selectedDuration = duration }
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "$duration دقائق",
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                                        )
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showSnoozeDialog = false
                                    AlarmSoundPlayer.stop()
                                    com.example.features.settings.ReminderSettingsManager.incrementSnoozeCount(this, taskId)
                                    val snoozeIntent = Intent(this, ReminderReceiver::class.java).apply {
                                        action = "com.example.ACTION_SNOOZE"
                                        putExtra("task_id", taskId)
                                        putExtra("task_title", taskTitle)
                                        putExtra("task_start_time", taskStartTime)
                                        putExtra("ringtone_uri", ringtoneUri)
                                        putExtra("snooze_duration", selectedDuration)
                                    }
                                    sendBroadcast(snoozeIntent)
                                    finish()
                                }
                            ) {
                                Text("تأكيد الغفوة", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showSnoozeDialog = false }) {
                                Text("إلغاء")
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        if (isFinishing) {
            AlarmSoundPlayer.stop()
        }
        try {
            unregisterReceiver(alarmDismissReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
}

@Composable
fun AlarmScreen(
    taskTitle: String,
    taskStartTime: String,
    showSnoozeButton: Boolean = true,
    onStopClick: () -> Unit,
    onSnoozeClick: () -> Unit
) {
    val context = LocalContext.current
    var currentTime by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        while (true) {
            currentTime = formatter.format(Date())
            delay(1000)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(120.dp)
                        .scale(pulseScale)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = "Alarm Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = currentTime,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("alarm_current_time")
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = taskTitle,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("alarm_task_title")
                )

                if (taskStartTime.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = taskStartTime,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("alarm_task_time")
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onStopClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .testTag("alarm_stop_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = context.getString(R.string.alarm_stop_btn),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                if (showSnoozeButton) {
                    OutlinedButton(
                        onClick = onSnoozeClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("alarm_snooze_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy()
                    ) {
                        Text(
                            text = context.getString(R.string.alarm_snooze_btn),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}
