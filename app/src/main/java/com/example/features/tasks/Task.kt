package com.example.features.tasks

import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

fun getCurrentDateString(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return sdf.format(Date())
}

/**
 * Clean data model representing a Task in the "Al-Thaki" (الذكي) application.
 * Adheres to standard Clean Architecture guidelines.
 */
@JsonClass(generateAdapter = true)
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val category: String = "work",
    val createdAt: Long = System.currentTimeMillis(),
    val targetDate: String = getCurrentDateString(),
    val isRolledOver: Boolean = false,
    val startTime: String? = null,
    val reminderEnabled: Boolean = false,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val ringtoneUri: String? = null
)
