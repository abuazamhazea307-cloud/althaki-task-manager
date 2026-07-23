package com.example.features.tasks

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

/**
 * Modern, lightweight, and thread-safe local persistence layer for Task data.
 * Wraps SharedPreferences and handles serialization seamlessly using Moshi codegen.
 */
class TaskLocalStore(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("tasks_prefs", Context.MODE_PRIVATE)
    private val moshi = Moshi.Builder().build()
    private val taskListType = Types.newParameterizedType(List::class.java, Task::class.java)
    private val jsonAdapter = moshi.adapter<List<Task>>(taskListType)

    /**
     * Saves the entire list of tasks to SharedPreferences in an asynchronous manner.
     */
    fun saveTasks(tasks: List<Task>) {
        val json = jsonAdapter.toJson(tasks)
        sharedPreferences.edit().putString("saved_tasks", json).apply()
    }

    /**
     * Loads the saved list of tasks. Returns null if no tasks have been saved yet.
     */
    fun loadTasks(): List<Task>? {
        val migrated = sharedPreferences.getBoolean("demo_tasks_migrated_v1", false)
        val json = sharedPreferences.getString("saved_tasks", null)

        if (!migrated) {
            if (json != null) {
                try {
                    val tasks = jsonAdapter.fromJson(json)
                    if (tasks != null) {
                        val demoIds = setOf("1", "2", "3", "4")
                        val filtered = tasks.filterNot { it.id in demoIds }
                        if (filtered.size != tasks.size) {
                            saveTasks(filtered)
                            sharedPreferences.edit().putBoolean("demo_tasks_migrated_v1", true).apply()
                            return filtered
                        }
                    }
                } catch (e: Exception) {
                    // Ignore
                }
            }
            sharedPreferences.edit().putBoolean("demo_tasks_migrated_v1", true).apply()
        }

        if (json == null) return null
        return try {
            jsonAdapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }
}
