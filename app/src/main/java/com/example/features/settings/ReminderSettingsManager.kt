package com.example.features.settings

import android.content.Context
import android.media.RingtoneManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object ReminderSettingsManager {
    private const val PREFS_NAME = "reminder_settings_prefs"
    private const val SNOOZE_PREFS_NAME = "active_snooze_counts"

    // Core keys
    const val KEY_REMINDER_BY_DEFAULT = "reminder_by_default"
    const val KEY_DEFAULT_ALARM_SOUND = "default_alarm_sound"
    const val KEY_DEFAULT_SNOOZE_DURATION = "default_snooze_duration"
    const val KEY_MAX_SNOOZE_COUNT = "max_snooze_count"
    const val KEY_ALARM_VIBRATION = "alarm_vibration"
    const val KEY_CONTINUOUS_ALARM = "continuous_alarm"
    const val KEY_ALARM_TIMEOUT = "alarm_timeout"
    const val KEY_IGNORE_COMPLETED_TASKS = "ignore_completed_tasks"
    const val KEY_REMINDER_NOTIFICATION = "reminder_notification"

    // Defaults
    const val DEFAULT_SNOOZE_DUR = 10 // minutes
    const val DEFAULT_MAX_SNOOZE = -1 // -1 means Unlimited
    const val DEFAULT_ALARM_TIMEOUT_SEC = 300 // 5 minutes (300s)

    // Reactively exposed Compose states for immediate UI updates
    var reminderByDefault by mutableStateOf(true)
        private set

    var defaultAlarmSound by mutableStateOf("")
        private set

    var defaultSnoozeDuration by mutableStateOf(DEFAULT_SNOOZE_DUR)
        private set

    var maxSnoozeCount by mutableStateOf(DEFAULT_MAX_SNOOZE)
        private set

    var alarmVibration by mutableStateOf(true)
        private set

    var continuousAlarm by mutableStateOf(true)
        private set

    var alarmTimeout by mutableStateOf(DEFAULT_ALARM_TIMEOUT_SEC)
        private set

    var ignoreCompletedTasks by mutableStateOf(true)
        private set

    var reminderNotification by mutableStateOf(true)
        private set

    // Internal fallback cache for generic extensibility (OCP support)
    private val genericCache = mutableMapOf<String, Any>()

    /**
     * Initializes the settings from local storage.
     */
    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        reminderByDefault = prefs.getBoolean(KEY_REMINDER_BY_DEFAULT, true)
        
        // Default default alarm sound
        val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)?.toString() ?: ""
        defaultAlarmSound = prefs.getString(KEY_DEFAULT_ALARM_SOUND, defaultUri) ?: defaultUri
        
        defaultSnoozeDuration = prefs.getInt(KEY_DEFAULT_SNOOZE_DURATION, DEFAULT_SNOOZE_DUR)
        maxSnoozeCount = prefs.getInt(KEY_MAX_SNOOZE_COUNT, DEFAULT_MAX_SNOOZE)
        alarmVibration = prefs.getBoolean(KEY_ALARM_VIBRATION, true)
        continuousAlarm = prefs.getBoolean(KEY_CONTINUOUS_ALARM, true)
        alarmTimeout = prefs.getInt(KEY_ALARM_TIMEOUT, DEFAULT_ALARM_TIMEOUT_SEC)
        ignoreCompletedTasks = prefs.getBoolean(KEY_IGNORE_COMPLETED_TASKS, true)
        reminderNotification = prefs.getBoolean(KEY_REMINDER_NOTIFICATION, true)

        // Load all saved settings into generic cache for OCP extensibility
        prefs.all.forEach { (key, value) ->
            if (value != null) {
                genericCache[key] = value
            }
        }
    }

    fun setReminderByDefault(context: Context, value: Boolean) {
        reminderByDefault = value
        saveBoolean(context, KEY_REMINDER_BY_DEFAULT, value)
    }

    fun setDefaultAlarmSound(context: Context, value: String) {
        defaultAlarmSound = value
        saveString(context, KEY_DEFAULT_ALARM_SOUND, value)
    }

    fun setDefaultSnoozeDuration(context: Context, value: Int) {
        defaultSnoozeDuration = value
        saveInt(context, KEY_DEFAULT_SNOOZE_DURATION, value)
    }

    fun setMaxSnoozeCount(context: Context, value: Int) {
        maxSnoozeCount = value
        saveInt(context, KEY_MAX_SNOOZE_COUNT, value)
    }

    fun setAlarmVibration(context: Context, value: Boolean) {
        alarmVibration = value
        saveBoolean(context, KEY_ALARM_VIBRATION, value)
    }

    fun setContinuousAlarm(context: Context, value: Boolean) {
        continuousAlarm = value
        saveBoolean(context, KEY_CONTINUOUS_ALARM, value)
    }

    fun setAlarmTimeout(context: Context, value: Int) {
        alarmTimeout = value
        saveInt(context, KEY_ALARM_TIMEOUT, value)
    }

    fun setIgnoreCompletedTasks(context: Context, value: Boolean) {
        ignoreCompletedTasks = value
        saveBoolean(context, KEY_IGNORE_COMPLETED_TASKS, value)
    }

    fun setReminderNotification(context: Context, value: Boolean) {
        reminderNotification = value
        saveBoolean(context, KEY_REMINDER_NOTIFICATION, value)
    }

    /**
     * Restores all core reminder settings back to their default values.
     */
    fun restoreDefaults(context: Context) {
        setReminderByDefault(context, true)
        val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)?.toString() ?: ""
        setDefaultAlarmSound(context, defaultUri)
        setDefaultSnoozeDuration(context, DEFAULT_SNOOZE_DUR)
        setMaxSnoozeCount(context, DEFAULT_MAX_SNOOZE)
        setAlarmVibration(context, true)
        setContinuousAlarm(context, true)
        setAlarmTimeout(context, DEFAULT_ALARM_TIMEOUT_SEC)
        setIgnoreCompletedTasks(context, true)
        setReminderNotification(context, true)
    }

    // --- Active Snooze Count Persistence (saves through device reboots & app restarts) ---

    fun getSnoozeCount(context: Context, taskId: String): Int {
        val prefs = context.getSharedPreferences(SNOOZE_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(taskId, 0)
    }

    fun incrementSnoozeCount(context: Context, taskId: String) {
        val prefs = context.getSharedPreferences(SNOOZE_PREFS_NAME, Context.MODE_PRIVATE)
        val current = prefs.getInt(taskId, 0)
        prefs.edit().putInt(taskId, current + 1).apply()
    }

    fun clearSnoozeCount(context: Context, taskId: String) {
        val prefs = context.getSharedPreferences(SNOOZE_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(taskId).apply()
    }

    // --- Extensibility API (OCP support) ---

    @Suppress("UNCHECKED_CAST")
    fun <T> getPreference(context: Context, key: String, defaultValue: T): T {
        val cached = genericCache[key]
        if (cached != null) {
            return cached as T
        }
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return when (defaultValue) {
            is Boolean -> prefs.getBoolean(key, defaultValue) as T
            is String -> prefs.getString(key, defaultValue) as T
            is Int -> prefs.getInt(key, defaultValue) as T
            is Long -> prefs.getLong(key, defaultValue) as T
            is Float -> prefs.getFloat(key, defaultValue) as T
            else -> defaultValue
        }
    }

    fun <T> setPreference(context: Context, key: String, value: T) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        when (value) {
            is Boolean -> {
                editor.putBoolean(key, value)
                genericCache[key] = value
            }
            is String -> {
                editor.putString(key, value)
                genericCache[key] = value
            }
            is Int -> {
                editor.putInt(key, value)
                genericCache[key] = value
            }
            is Long -> {
                editor.putLong(key, value)
                genericCache[key] = value
            }
            is Float -> {
                editor.putFloat(key, value)
                genericCache[key] = value
            }
        }
        editor.apply()
    }

    private fun saveString(context: Context, key: String, value: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(key, value).apply()
        genericCache[key] = value
    }

    private fun saveBoolean(context: Context, key: String, value: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(key, value).apply()
        genericCache[key] = value
    }

    private fun saveInt(context: Context, key: String, value: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(key, value).apply()
        genericCache[key] = value
    }
}
