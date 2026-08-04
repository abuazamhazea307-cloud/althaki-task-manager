package com.example.features.tasks

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar
import java.util.Locale

/**
 * Utility function to create the Notification Channel.
 */
fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "task_reminders_channel"
        val channelName = context.getString(R.string.reminder_channel_name)
        val channelDesc = context.getString(R.string.reminder_channel_desc)
        val importance = NotificationManager.IMPORTANCE_HIGH
        
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDesc
            enableLights(true)
            enableVibration(true)
        }
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

/**
 * Parses targetDate (yyyy-MM-dd) and startTime (h:mm a) into epoch milliseconds.
 */
fun parseAlarmTime(targetDate: String, startTime: String): Long? {
    try {
        val cleanTime = startTime.trim().uppercase(Locale.US)
        val isPm = cleanTime.contains("PM") || cleanTime.contains("مساءً")
        val isAm = cleanTime.contains("AM") || cleanTime.contains("صباحًا")
        
        val regex = java.util.regex.Pattern.compile("(\\d{1,2}):(\\d{2})")
        val matcher = regex.matcher(cleanTime)
        if (matcher.find()) {
            val hourStr = matcher.group(1) ?: return null
            val minuteStr = matcher.group(2) ?: return null
            var hour = hourStr.toIntOrNull() ?: return null
            val minute = minuteStr.toIntOrNull() ?: return null
            
            if (isPm && hour < 12) {
                hour += 12
            } else if (isAm && hour == 12) {
                hour = 0
            }
            
            val dateParts = targetDate.split("-")
            if (dateParts.size == 3) {
                val year = dateParts[0].toIntOrNull() ?: return null
                val month = dateParts[1].toIntOrNull() ?: return null
                val day = dateParts[2].toIntOrNull() ?: return null
                
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month - 1) // 0-based
                    set(Calendar.DAY_OF_MONTH, day)
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                return calendar.timeInMillis
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

/**
 * Helper scheduler for managing task reminders.
 */
object ReminderScheduler {

    fun scheduleReminder(context: Context, task: Task) {
        if (!task.reminderEnabled || task.isCompleted || task.startTime == null || task.taskDay == "tomorrow") {
            cancelReminder(context, task.id)
            return
        }

        val alarmTime = parseAlarmTime(task.targetDate, task.startTime) ?: return
        
        // Only schedule for future times
        if (alarmTime <= System.currentTimeMillis()) {
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.example.ACTION_SHOW_REMINDER"
            putExtra("task_id", task.id)
            putExtra("task_title", task.title)
            putExtra("task_start_time", task.startTime)
            putExtra("ringtone_uri", task.ringtoneUri)
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            pendingIntentFlags
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
            }
        } catch (e: SecurityException) {
            // Fallback in case exact alarm permission was denied / revoked at runtime
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
            }
        }
    }

    fun scheduleSnooze(
        context: Context,
        taskId: String,
        taskTitle: String,
        taskStartTime: String,
        ringtoneUriStr: String?,
        snoozeMin: Int = 5
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.example.ACTION_SHOW_REMINDER"
            putExtra("task_id", taskId)
            putExtra("task_title", taskTitle)
            putExtra("task_start_time", taskStartTime)
            putExtra("ringtone_uri", ringtoneUriStr)
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode(),
            intent,
            pendingIntentFlags
        )

        val alarmTime = System.currentTimeMillis() + snoozeMin * 60 * 1000

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
            }
        } catch (e: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
            }
        }
    }

    fun cancelReminder(context: Context, taskId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.example.ACTION_SHOW_REMINDER"
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_NO_CREATE
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode(),
            intent,
            pendingIntentFlags
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}

/**
 * BroadcastReceiver triggered by AlarmManager to post notifications offline.
 */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == "com.example.ACTION_SHOW_REMINDER") {
            val taskId = intent.getStringExtra("task_id") ?: return
            val taskTitle = intent.getStringExtra("task_title") ?: return
            val taskStartTime = intent.getStringExtra("task_start_time") ?: ""
            val ringtoneUri = intent.getStringExtra("ringtone_uri")

            // SAFETY: Never trigger deleted or reminder disabled tasks
            val localStore = TaskLocalStore(context)
            val task = localStore.loadTasks()?.find { it.id == taskId }
            if (task == null || !task.reminderEnabled) {
                return
            }

            if (com.example.features.settings.ReminderSettingsManager.ignoreCompletedTasks && task.isCompleted) {
                return
            }

            // Directly post high-importance notification with sound, vibration, and actions
            showReminderNotification(context, taskId, taskTitle, taskStartTime, ringtoneUri)
        } else if (action == "com.example.ACTION_COMPLETE") {
            val taskId = intent.getStringExtra("task_id") ?: return
            markTaskAsCompleted(context, taskId)
            dismissNotification(context, taskId)
            context.sendBroadcast(Intent("com.example.ALARM_DISMISSED"))
        } else if (action == "com.example.ACTION_SNOOZE") {
            val taskId = intent.getStringExtra("task_id") ?: return
            val taskTitle = intent.getStringExtra("task_title") ?: return
            val taskStartTime = intent.getStringExtra("task_start_time") ?: ""
            val ringtoneUri = intent.getStringExtra("ringtone_uri")

            ReminderScheduler.scheduleSnooze(context, taskId, taskTitle, taskStartTime, ringtoneUri, snoozeMin = 5)
            dismissNotification(context, taskId)
            context.sendBroadcast(Intent("com.example.ALARM_DISMISSED"))
        } else if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == "android.intent.action.TIME_SET" ||
            action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            // Re-register all alarms on system events
            TomorrowAutoMigrationEngine.checkAndMigrate(context)
            val localStore = TaskLocalStore(context)
            val tasks = localStore.loadTasks() ?: emptyList()
            for (task in tasks) {
                ReminderScheduler.scheduleReminder(context, task)
            }
        }
    }

    private fun markTaskAsCompleted(context: Context, taskId: String) {
        try {
            val store = TaskLocalStore(context)
            val tasks = store.loadTasks() ?: emptyList()
            val updatedTasks = tasks.map {
                if (it.id == taskId) {
                    it.copy(isCompleted = true, completedAt = System.currentTimeMillis())
                } else {
                    it
                }
            }
            store.saveTasks(updatedTasks)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dismissNotification(context: Context, taskId: String) {
        AlarmSoundPlayer.stop()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(taskId.hashCode())
        AlarmNotificationManager.dismissAlert()
        com.example.features.settings.ReminderSettingsManager.clearSnoozeCount(context, taskId)
    }
}

/**
 * Singleton sound and vibration player for alarms without requiring a Foreground Service.
 */
object AlarmSoundPlayer {
    private var mediaPlayer: android.media.MediaPlayer? = null
    private var vibrator: android.os.Vibrator? = null
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private var timeoutRunnable: Runnable? = null

    fun start(context: Context, ringtoneUriStr: String?) {
        stop()
        try {
            val uri = getNotificationSoundUri(context, ringtoneUriStr)
            mediaPlayer = android.media.MediaPlayer().apply {
                setDataSource(context, uri)
                setAudioAttributes(
                    android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (com.example.features.settings.ReminderSettingsManager.alarmVibration) {
            try {
                vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
                    vibratorManager.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    context.getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
                }
                val pattern = longArrayOf(0, 500, 250, 500)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(android.os.VibrationEffect.createWaveform(pattern, 0))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(pattern, 0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val timeoutSec = com.example.features.settings.ReminderSettingsManager.alarmTimeout
        if (timeoutSec > 0) {
            val runnable = Runnable { stop() }
            timeoutRunnable = runnable
            handler.postDelayed(runnable, timeoutSec * 1000L)
        }
    }

    fun stop() {
        timeoutRunnable?.let { handler.removeCallbacks(it) }
        timeoutRunnable = null

        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
        }

        try {
            vibrator?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            vibrator = null
        }
    }
}

/**
 * Utility to get sound URI for notification.
 */
fun getNotificationSoundUri(context: Context, customUriStr: String?): android.net.Uri {
    if (!customUriStr.isNullOrBlank()) {
        try {
            return android.net.Uri.parse(customUriStr)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    val defaultSettingStr = com.example.features.settings.ReminderSettingsManager.defaultAlarmSound
    if (!defaultSettingStr.isNullOrBlank()) {
        try {
            return android.net.Uri.parse(defaultSettingStr)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM)
        ?: android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
        ?: android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_RINGTONE)
}

/**
 * Directly posts a high-priority Heads-Up Notification with actions for complete and snooze.
 */
fun showReminderNotification(
    context: Context,
    taskId: String,
    taskTitle: String,
    taskStartTime: String,
    ringtoneUriStr: String?
) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "task_reminders_channel"
    val soundUri = getNotificationSoundUri(context, ringtoneUriStr)

    // Start continuous ringtone and vibration
    AlarmSoundPlayer.start(context, ringtoneUriStr)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelName = context.getString(R.string.reminder_channel_name)
        val channelDesc = context.getString(R.string.reminder_channel_desc)
        val audioAttributes = android.media.AudioAttributes.Builder()
            .setUsage(android.media.AudioAttributes.USAGE_ALARM)
            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = channelDesc
            enableLights(true)
            enableVibration(com.example.features.settings.ReminderSettingsManager.alarmVibration)
            if (com.example.features.settings.ReminderSettingsManager.alarmVibration) {
                vibrationPattern = longArrayOf(0, 500, 250, 500)
            }
            setSound(soundUri, audioAttributes)
            setBypassDnd(true)
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
    }

    // Full Screen Intent to AlarmActivity
    val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra("task_id", taskId)
        putExtra("task_title", taskTitle)
        putExtra("task_start_time", taskStartTime)
        putExtra("ringtone_uri", ringtoneUriStr)
    }

    val pendingFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }

    val fullScreenPendingIntent = PendingIntent.getActivity(
        context,
        taskId.hashCode(),
        fullScreenIntent,
        pendingFlags
    )

    // Complete Intent
    val completeIntent = Intent(context, ReminderReceiver::class.java).apply {
        action = "com.example.ACTION_COMPLETE"
        putExtra("task_id", taskId)
    }
    val completePendingIntent = PendingIntent.getBroadcast(
        context,
        taskId.hashCode() + 100,
        completeIntent,
        pendingFlags
    )

    // Snooze Intent
    val snoozeIntent = Intent(context, ReminderReceiver::class.java).apply {
        action = "com.example.ACTION_SNOOZE"
        putExtra("task_id", taskId)
        putExtra("task_title", taskTitle)
        putExtra("task_start_time", taskStartTime)
        putExtra("ringtone_uri", ringtoneUriStr)
    }
    val snoozePendingIntent = PendingIntent.getBroadcast(
        context,
        taskId.hashCode() + 200,
        snoozeIntent,
        pendingFlags
    )

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(taskTitle)
        .setContentText(if (taskStartTime.isNotBlank()) taskStartTime else context.getString(R.string.nav_tasks))
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setContentIntent(fullScreenPendingIntent)
        .setFullScreenIntent(fullScreenPendingIntent, true)
        .setAutoCancel(true)
        .setOngoing(false)
        .setSound(soundUri)
        .setVibrate(if (com.example.features.settings.ReminderSettingsManager.alarmVibration) longArrayOf(0, 500, 250, 500) else null)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .addAction(R.drawable.ic_alarm, "✅ تم التنفيذ", completePendingIntent)
        .addAction(R.drawable.ic_alarm, "⏰ تأجيل 5 دقائق", snoozePendingIntent)

    val notification = builder.build()
    notification.flags = notification.flags or android.app.Notification.FLAG_INSISTENT

    notificationManager.notify(taskId.hashCode(), notification)

    AlarmNotificationManager.showAlert(taskId, taskTitle, taskStartTime)
}
