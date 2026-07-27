package com.example.features.tasks

import android.content.Context
import android.util.Log

/**
 * Tomorrow Auto Migration Engine
 * Automatically migrates uncompleted "tomorrow" tasks to "today" when a new day starts.
 * Runs efficiently without permanent background services or loops.
 */
object TomorrowAutoMigrationEngine {
    private const val TAG = "TomorrowAutoMigration"
    private const val PREFS_NAME = "tomorrow_migration_prefs"
    private const val KEY_LAST_MIGRATION_DATE = "last_migration_date"

    /**
     * Checks if a new day has started since the last migration, and performs migration if needed.
     */
    @Synchronized
    fun checkAndMigrate(context: Context) {
        com.example.debug.StartupTracer.mark("MIGRATION_BEGIN")
        val today = getCurrentDateString()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastMigrationDate = prefs.getString(KEY_LAST_MIGRATION_DATE, null)

        Log.d(TAG, "checkAndMigrate: today=$today, lastMigrationDate=$lastMigrationDate")

        if (lastMigrationDate != today) {
            val localStore = TaskLocalStore(context)
            val tasks = localStore.loadTasks()?.toMutableList() ?: mutableListOf()

            var migratedCount = 0
            val updatedTasks = tasks.map { task ->
                if (task.taskDay == "tomorrow" && !task.isCompleted) {
                    migratedCount++
                    task.copy(
                        taskDay = "today",
                        targetDate = today
                    )
                } else {
                    task
                }
            }

            if (migratedCount > 0) {
                Log.d(TAG, "Migrated $migratedCount tomorrow tasks to today.")
                localStore.saveTasks(updatedTasks)
            } else {
                // If there are no tasks to migrate but it is a new day, we still save the date
                // to avoid re-checking in future calls today.
                Log.d(TAG, "No uncompleted tomorrow tasks to migrate.")
            }

            // Save the new migration date to ensure it only runs once per day
            prefs.edit().putString(KEY_LAST_MIGRATION_DATE, today).apply()
        }
        com.example.debug.StartupTracer.mark("MIGRATION_END")
    }
}
