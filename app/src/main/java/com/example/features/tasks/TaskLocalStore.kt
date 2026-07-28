package com.example.features.tasks

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Modern, lightweight, and thread-safe local persistence layer for Task data.
 * Wraps SharedPreferences and handles serialization seamlessly using Moshi codegen.
 * Integrates a reactive StateFlow so updates are instantly pushed to all observers.
 */
class TaskLocalStore(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("tasks_prefs", Context.MODE_PRIVATE)

    companion object {
        private val _tasksFlow = MutableStateFlow<List<Task>>(emptyList())
        val tasksFlow: StateFlow<List<Task>> = _tasksFlow.asStateFlow()
        @Volatile
        private var isInitialized = false
        @Volatile
        private var isInitializing = false

        private val moshi by lazy { Moshi.Builder().build() }
        private val taskListType by lazy { Types.newParameterizedType(List::class.java, Task::class.java) }
        private val jsonAdapter by lazy { moshi.adapter<List<Task>>(taskListType) }

        fun initAsync(context: Context) {
            synchronized(TaskLocalStore::class.java) {
                if (isInitialized || isInitializing) return
                isInitializing = true
            }
            try {
                com.example.debug.StartupTracer.mark("TASK_STORE_BEGIN")
                val store = TaskLocalStore(context)
                val loaded = store.loadTasksInternal() ?: emptyList()
                synchronized(TaskLocalStore::class.java) {
                    _tasksFlow.value = loaded
                    isInitialized = true
                    isInitializing = false
                }
                com.example.debug.StartupTracer.mark("TASK_STORE_END")
            } catch (e: Exception) {
                synchronized(TaskLocalStore::class.java) {
                    isInitializing = false
                }
            }
        }
    }

    init {
        // No heavy synchronous loading in constructor init.
        // It will be loaded asynchronously via initAsync, or on-demand via loadTasks() if accessed synchronously.
    }

    private fun loadTasksInternal(): List<Task>? {
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
                            saveTasksInternal(filtered)
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

    private fun saveTasksInternal(tasks: List<Task>) {
        val json = jsonAdapter.toJson(tasks)
        sharedPreferences.edit().putString("saved_tasks", json).apply()
    }

    /**
     * Saves the entire list of tasks to SharedPreferences and updates the reactive StateFlow.
     */
    fun saveTasks(tasks: List<Task>) {
        val oldTasks = _tasksFlow.value
        val oldTasksMap = oldTasks.associateBy { it.id }

        saveTasksInternal(tasks)
        _tasksFlow.value = tasks

        // Sync reminders based on the changes
        val newIds = tasks.map { it.id }.toSet()
        for (oldTask in oldTasks) {
            if (oldTask.id !in newIds) {
                ReminderScheduler.cancelReminder(context, oldTask.id)
            }
        }

        for (task in tasks) {
            val oldTask = oldTasksMap[task.id]
            if (oldTask == null) {
                ReminderScheduler.scheduleReminder(context, task)
            } else {
                if (task.isCompleted != oldTask.isCompleted ||
                    task.reminderEnabled != oldTask.reminderEnabled ||
                    task.startTime != oldTask.startTime ||
                    task.targetDate != oldTask.targetDate ||
                    task.taskDay != oldTask.taskDay
                ) {
                    if (task.isCompleted) {
                        ReminderScheduler.cancelReminder(context, task.id)
                    } else {
                        ReminderScheduler.scheduleReminder(context, task)
                    }
                }
            }
        }
    }

    /**
     * Loads the saved list of tasks from the reactive memory cache.
     */
    fun loadTasks(): List<Task>? {
        val current = _tasksFlow.value
        if (current.isNotEmpty()) return current
        
        val loaded = loadTasksInternal()
        if (loaded != null) {
            _tasksFlow.value = loaded
            return loaded
        }
        return null
    }
}
