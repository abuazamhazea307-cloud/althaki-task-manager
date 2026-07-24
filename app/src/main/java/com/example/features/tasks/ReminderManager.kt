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
        if (!task.reminderEnabled || task.isCompleted || task.startTime == null) {
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
        ringtoneUriStr: String?
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

        val snoozeMin = com.example.features.settings.ReminderSettingsManager.defaultSnoozeDuration
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

            // Start AlarmService to trigger persistent notification, sound, vibration, and full-screen intent
            val serviceIntent = Intent(context, AlarmService::class.java).apply {
                this.action = AlarmService.ACTION_START
                putExtra(AlarmService.EXTRA_TASK_ID, taskId)
                putExtra(AlarmService.EXTRA_TASK_TITLE, taskTitle)
                putExtra(AlarmService.EXTRA_TASK_START_TIME, taskStartTime)
                putExtra(AlarmService.EXTRA_RINGTONE_URI, ringtoneUri)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } else if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == "android.intent.action.TIME_SET" ||
            action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            // Re-register all alarms on system events
            val localStore = TaskLocalStore(context)
            val tasks = localStore.loadTasks() ?: emptyList()
            for (task in tasks) {
                ReminderScheduler.scheduleReminder(context, task)
            }
        }
    }
}
