package com.example.features.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Singleton manager to persist and handle reactive general settings.
 */
object GeneralSettingsManager {
    private const val PREFS_NAME = "general_settings_prefs"
    
    private const val KEY_SHOW_SPLASH = "show_splash"
    private const val KEY_SPLASH_DURATION = "splash_duration"
    private const val KEY_ENABLE_ANIMATIONS = "enable_animations"
    private const val KEY_ENABLE_HAPTIC = "enable_haptic"

    const val DURATION_SHORT = "short"
    const val DURATION_NORMAL = "normal"
    const val DURATION_LONG = "long"

    // Reactive Compose states for easy use in composables
    var showSplash by mutableStateOf(true)
        private set

    var splashDuration by mutableStateOf(DURATION_NORMAL)
        private set

    var enableAnimations by mutableStateOf(true)
        private set

    var enableHaptic by mutableStateOf(true)
        private set

    /**
     * Initialize settings from SharedPreferences on application start.
     */
    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        showSplash = prefs.getBoolean(KEY_SHOW_SPLASH, true)
        splashDuration = prefs.getString(KEY_SPLASH_DURATION, DURATION_NORMAL) ?: DURATION_NORMAL
        enableAnimations = prefs.getBoolean(KEY_ENABLE_ANIMATIONS, true)
        enableHaptic = prefs.getBoolean(KEY_ENABLE_HAPTIC, true)
    }

    fun setShowSplash(context: Context, value: Boolean) {
        showSplash = value
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SHOW_SPLASH, value).apply()
    }

    fun setSplashDuration(context: Context, value: String) {
        splashDuration = value
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_SPLASH_DURATION, value).apply()
    }

    fun setEnableAnimations(context: Context, value: Boolean) {
        enableAnimations = value
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ENABLE_ANIMATIONS, value).apply()
    }

    fun setEnableHaptic(context: Context, value: Boolean) {
        enableHaptic = value
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ENABLE_HAPTIC, value).apply()
    }

    /**
     * Restore only General Settings to default values.
     */
    fun restoreDefaults(context: Context) {
        setShowSplash(context, true)
        setSplashDuration(context, DURATION_NORMAL)
        setEnableAnimations(context, true)
        setEnableHaptic(context, true)
    }
}
