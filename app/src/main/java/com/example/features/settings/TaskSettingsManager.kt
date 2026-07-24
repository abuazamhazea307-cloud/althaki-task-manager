package com.example.features.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Singleton manager to persist, handle, and reactively expose Task Settings.
 * Designed with the Open/Closed Principle (OCP) in mind to be fully extensible
 * for future task preferences without altering existing APIs or breaking compatibility.
 */
object TaskSettingsManager {
    private const val PREFS_NAME = "task_settings_prefs"

    // Core keys
    const val KEY_TASK_ORDER = "task_order"
    const val KEY_SHOW_COMPLETED = "show_completed"
    const val KEY_SORT_BY = "sort_by"
    const val KEY_SHOW_TASK_TIME = "show_task_time"
    const val KEY_SHOW_CATEGORIES = "show_categories"

    // Core option values
    const val ORDER_OLDEST_FIRST = "oldest_first"
    const val ORDER_NEWEST_FIRST = "newest_first"

    const val SORT_CREATION_DATE = "creation_date"
    const val SORT_START_TIME = "start_time"
    const val SORT_TITLE = "title"

    // Reactively exposed Compose states for immediate UI updates
    var taskOrder by mutableStateOf(ORDER_OLDEST_FIRST)
        private set

    var showCompleted by mutableStateOf(true)
        private set

    var sortBy by mutableStateOf(SORT_CREATION_DATE)
        private set

    var showTaskTime by mutableStateOf(true)
        private set

    var showCategories by mutableStateOf(true)
        private set

    // Internal fallback cache for generic extensibility (OCP support)
    private val genericCache = mutableMapOf<String, Any>()

    /**
     * Initializes the settings from local storage.
     */
    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        taskOrder = prefs.getString(KEY_TASK_ORDER, ORDER_OLDEST_FIRST) ?: ORDER_OLDEST_FIRST
        showCompleted = prefs.getBoolean(KEY_SHOW_COMPLETED, true)
        sortBy = prefs.getString(KEY_SORT_BY, SORT_CREATION_DATE) ?: SORT_CREATION_DATE
        showTaskTime = prefs.getBoolean(KEY_SHOW_TASK_TIME, true)
        showCategories = prefs.getBoolean(KEY_SHOW_CATEGORIES, true)

        // Load all saved settings into generic cache for OCP extensibility
        prefs.all.forEach { (key, value) ->
            if (value != null) {
                genericCache[key] = value
            }
        }
    }

    fun setTaskOrder(context: Context, value: String) {
        taskOrder = value
        saveString(context, KEY_TASK_ORDER, value)
    }

    fun setShowCompleted(context: Context, value: Boolean) {
        showCompleted = value
        saveBoolean(context, KEY_SHOW_COMPLETED, value)
    }

    fun setSortBy(context: Context, value: String) {
        sortBy = value
        saveString(context, KEY_SORT_BY, value)
    }

    fun setShowTaskTime(context: Context, value: Boolean) {
        showTaskTime = value
        saveBoolean(context, KEY_SHOW_TASK_TIME, value)
    }

    fun setShowCategories(context: Context, value: Boolean) {
        showCategories = value
        saveBoolean(context, KEY_SHOW_CATEGORIES, value)
    }

    /**
     * Restores all core task settings back to their default values.
     */
    fun restoreDefaults(context: Context) {
        setTaskOrder(context, ORDER_OLDEST_FIRST)
        setShowCompleted(context, true)
        setSortBy(context, SORT_CREATION_DATE)
        setShowTaskTime(context, true)
        setShowCategories(context, true)
    }

    // --- Extensibility API (OCP support) ---

    /**
     * Retrieves any preference dynamically. Allows future options to be accessed 
     * without modifying the core class implementation.
     */
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

    /**
     * Saves any preference dynamically. Enables future options to be written
     * without modifying the core class implementation.
     */
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
}
