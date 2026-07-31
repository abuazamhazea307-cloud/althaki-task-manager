package com.example.features.tasks

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AlarmNotificationManager {
    data class ActiveAlert(
        val taskId: String,
        val taskTitle: String,
        val taskStartTime: String
    )

    private val _activeAlert = MutableStateFlow<ActiveAlert?>(null)
    val activeAlert: StateFlow<ActiveAlert?> = _activeAlert.asStateFlow()

    fun showAlert(taskId: String, taskTitle: String, taskStartTime: String) {
        _activeAlert.value = ActiveAlert(taskId, taskTitle, taskStartTime)
    }

    fun dismissAlert() {
        _activeAlert.value = null
    }
}
