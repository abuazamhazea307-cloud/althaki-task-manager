package com.example.features.tasks

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.R

class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private var timeoutRunnable: Runnable? = null

    companion object {
        const val ACTION_START = "com.example.ACTION_START_ALARM"
        const val ACTION_STOP = "com.example.ACTION_STOP_ALARM"
        const val ACTION_SNOOZE = "com.example.ACTION_SNOOZE_ALARM"

        const val EXTRA_TASK_ID = "task_id"
        const val EXTRA_TASK_TITLE = "task_title"
        const val EXTRA_TASK_START_TIME = "task_start_time"
        const val EXTRA_RINGTONE_URI = "ringtone_uri"

        private const val NOTIFICATION_ID = 9999
        private const val CHANNEL_ID = "alarm_channel_id"

        var isRinging = false
        var currentTaskId: String? = null
        var currentTaskTitle: String? = null
        var currentTaskStartTime: String? = null
        var currentRingtoneUri: String? = null
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_START) {
            val taskId = intent.getStringExtra(EXTRA_TASK_ID) ?: ""
            val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: ""
            val taskStartTime = intent.getStringExtra(EXTRA_TASK_START_TIME) ?: ""
            val ringtoneUri = intent.getStringExtra(EXTRA_RINGTONE_URI)

            currentTaskId = taskId
            currentTaskTitle = taskTitle
            currentTaskStartTime = taskStartTime
            currentRingtoneUri = ringtoneUri
            isRinging = true

            // Send local broadcast to update any active AlarmActivity
            val activityIntent = Intent("com.example.ALARM_TRIGGERED").apply {
                putExtra(EXTRA_TASK_ID, taskId)
                putExtra(EXTRA_TASK_TITLE, taskTitle)
                putExtra(EXTRA_TASK_START_TIME, taskStartTime)
                putExtra(EXTRA_RINGTONE_URI, ringtoneUri)
            }
            sendBroadcast(activityIntent)

            startForegroundServiceNotification(taskId, taskTitle, taskStartTime, ringtoneUri)
            startRinging(ringtoneUri)
            
            if (com.example.features.settings.ReminderSettingsManager.alarmVibration) {
                startVibrating()
            }

            if (!com.example.features.settings.ReminderSettingsManager.continuousAlarm) {
                val timeoutMs = com.example.features.settings.ReminderSettingsManager.alarmTimeout * 1000L
                timeoutRunnable = Runnable {
                    stopAlarm()
                }
                handler.postDelayed(timeoutRunnable!!, timeoutMs)
            }
        } else if (action == ACTION_STOP) {
            stopAlarm()
        } else if (action == ACTION_SNOOZE) {
            snoozeAlarm()
        }

        return START_NOT_STICKY
    }

    private fun startForegroundServiceNotification(
        taskId: String,
        taskTitle: String,
        taskStartTime: String,
        ringtoneUri: String?
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.reminder_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.reminder_channel_desc)
                enableLights(true)
                enableVibration(true)
                setBypassDnd(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Full Screen Intent to AlarmActivity
        val fullScreenIntent = Intent(this, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_TASK_ID, taskId)
            putExtra(EXTRA_TASK_TITLE, taskTitle)
            putExtra(EXTRA_TASK_START_TIME, taskStartTime)
            putExtra(EXTRA_RINGTONE_URI, ringtoneUri)
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            taskId.hashCode(),
            fullScreenIntent,
            pendingIntentFlags
        )

        // Stop Intent
        val stopIntent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            taskId.hashCode() + 1,
            stopIntent,
            pendingIntentFlags
        )

        // Snooze Intent
        val snoozeIntent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_SNOOZE
        }
        val snoozePendingIntent = PendingIntent.getService(
            this,
            taskId.hashCode() + 2,
            snoozeIntent,
            pendingIntentFlags
        )

        val showHeadsUp = com.example.features.settings.ReminderSettingsManager.reminderNotification

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.reminder_notif_title))
            .setContentText("$taskTitle ($taskStartTime)")
            .setPriority(if (showHeadsUp) NotificationCompat.PRIORITY_MAX else NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(fullScreenPendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .addAction(R.drawable.ic_alarm, getString(R.string.alarm_stop_btn), stopPendingIntent)
            .addAction(R.drawable.ic_alarm, getString(R.string.alarm_snooze_btn), snoozePendingIntent)

        if (showHeadsUp) {
            notificationBuilder.setFullScreenIntent(fullScreenPendingIntent, true)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notificationBuilder.build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notificationBuilder.build())
        }

        if (!showHeadsUp) {
            try {
                startActivity(fullScreenIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startRinging(ringtoneUriStr: String?) {
        try {
            var ringtoneUri: Uri? = null
            if (!ringtoneUriStr.isNullOrBlank()) {
                ringtoneUri = Uri.parse(ringtoneUriStr)
            }
            if (ringtoneUri == null) {
                val globalDefaultStr = com.example.features.settings.ReminderSettingsManager.defaultAlarmSound
                if (!globalDefaultStr.isNullOrBlank()) {
                    ringtoneUri = Uri.parse(globalDefaultStr)
                }
            }
            if (ringtoneUri == null) {
                ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            }
            if (ringtoneUri == null) {
                ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@AlarmService, ringtoneUri!!)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback playback
            try {
                mediaPlayer = MediaPlayer.create(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).apply {
                    isLooping = true
                    start()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun startVibrating() {
        try {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pattern = longArrayOf(0, 500, 500)
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 500, 500), 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopAlarm() {
        cleanup()
        val closeIntent = Intent("com.example.ALARM_DISMISSED")
        sendBroadcast(closeIntent)
        stopSelf()
    }

    private fun snoozeAlarm() {
        val taskId = currentTaskId ?: ""
        val taskTitle = currentTaskTitle ?: ""
        val taskStartTime = currentTaskStartTime ?: ""
        val ringtoneUriStr = currentRingtoneUri

        ReminderScheduler.scheduleSnooze(this, taskId, taskTitle, taskStartTime, ringtoneUriStr)

        cleanup()
        val closeIntent = Intent("com.example.ALARM_DISMISSED")
        sendBroadcast(closeIntent)
        stopSelf()
    }

    private fun cleanup() {
        isRinging = false
        currentTaskId?.let { taskId ->
            com.example.features.settings.ReminderSettingsManager.clearSnoozeCount(this, taskId)
        }
        currentTaskId = null
        currentTaskTitle = null
        currentTaskStartTime = null
        currentRingtoneUri = null

        timeoutRunnable?.let {
            handler.removeCallbacks(it)
            timeoutRunnable = null
        }

        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            vibrator?.cancel()
            vibrator = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        cleanup()
        super.onDestroy()
    }
}
