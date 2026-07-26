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
    
    private const val KEY_ENABLE_ANIMATIONS = "enable_animations"
    private const val KEY_ENABLE_HAPTIC = "enable_haptic"

    var enableAnimations by mutableStateOf(true)
        private set

    var enableHaptic by mutableStateOf(true)
        private set

    /**
     * Initialize settings from SharedPreferences on application start.
     */
    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        enableAnimations = prefs.getBoolean(KEY_ENABLE_ANIMATIONS, true)
        enableHaptic = prefs.getBoolean(KEY_ENABLE_HAPTIC, true)
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
        setEnableAnimations(context, true)
        setEnableHaptic(context, true)
    }
}
