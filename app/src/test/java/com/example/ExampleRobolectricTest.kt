package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.features.tasks.Task
import com.example.features.tasks.TaskLocalStore
import com.example.features.tasks.TomorrowAutoMigrationEngine
import com.example.features.tasks.getCurrentDateString
import com.example.features.tasks.getTomorrowDateString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.Shadows.shadowOf
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private fun getFutureTimeToday(): String {
    val cal = Calendar.getInstance()
    // If we are close to midnight, return 11:59 PM
    if (cal.get(Calendar.HOUR_OF_DAY) >= 23 && cal.get(Calendar.MINUTE) >= 30) {
      return "11:59 PM"
    }
    cal.add(Calendar.MINUTE, 30)
    val sdf = SimpleDateFormat("hh:mm a", Locale.US)
    return sdf.format(cal.time)
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertTrue(
      "App name should be either 'مدير المهام' or 'Task Manager'",
      appName == "مدير المهام" || appName == "Task Manager" || appName == "الذكي | مدير المهام"
    )
  }

  @Test
  fun `test 1 - tomorrow task with start time and reminder does not schedule alarm`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val localStore = TaskLocalStore(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    val shadowAlarmManager = shadowOf(alarmManager)
    
    // Reset any previously scheduled alarms
    shadowAlarmManager.scheduledAlarms.clear()
    
    val tomorrowDate = getTomorrowDateString()
    val tomorrowTask = Task(
      id = "req_1_tomorrow_task",
      title = "Tomorrow Task Req 1",
      targetDate = tomorrowDate,
      taskDay = "tomorrow",
      startTime = "10:00 AM",
      reminderEnabled = true,
      ringtoneUri = "default_tone"
    )
    
    localStore.saveTasks(listOf(tomorrowTask))
    
    val hasAlarm = shadowAlarmManager.scheduledAlarms.any { 
      val shadowPending = shadowOf(it.operation)
      shadowPending.savedIntent.getStringExtra("task_id") == "req_1_tomorrow_task"
    }
    
    assertFalse("A task in tomorrow list must NOT schedule an alarm", hasAlarm)
  }

  @Test
  fun `test 2 - tomorrow task schedules alarm after auto migration to today`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val localStore = TaskLocalStore(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    val shadowAlarmManager = shadowOf(alarmManager)
    
    shadowAlarmManager.scheduledAlarms.clear()
    
    val todayDate = getCurrentDateString()
    val tomorrowDate = getTomorrowDateString()
    val futureTimeToday = getFutureTimeToday()
    
    val tomorrowTask = Task(
      id = "req_2_migration_task",
      title = "Tomorrow Task Req 2",
      targetDate = tomorrowDate,
      taskDay = "tomorrow",
      startTime = futureTimeToday,
      reminderEnabled = true
    )
    
    localStore.saveTasks(listOf(tomorrowTask))
    
    // Simulate day change in shared preferences
    val migrationPrefs = context.getSharedPreferences("tomorrow_migration_prefs", Context.MODE_PRIVATE)
    migrationPrefs.edit().putString("last_migration_date", "2020-01-01").apply()
    
    // Run migration
    TomorrowAutoMigrationEngine.checkAndMigrate(context)
    
    val migratedTasks = localStore.loadTasks() ?: emptyList()
    val migratedTask = migratedTasks.find { it.id == "req_2_migration_task" }
    
    // Verify properties updated
    assertEquals("today", migratedTask?.taskDay)
    assertEquals(todayDate, migratedTask?.targetDate)
    
    // Verify alarm scheduled
    val hasAlarm = shadowAlarmManager.scheduledAlarms.any { 
      val shadowPending = shadowOf(it.operation)
      shadowPending.savedIntent.getStringExtra("task_id") == "req_2_migration_task"
    }
    assertTrue("Alarm should be scheduled after tomorrow task migrates to today", hasAlarm)
  }

  @Test
  fun `test 3 - tomorrow task without start time does not schedule alarm after migration`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val localStore = TaskLocalStore(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    val shadowAlarmManager = shadowOf(alarmManager)
    
    shadowAlarmManager.scheduledAlarms.clear()
    
    val tomorrowDate = getTomorrowDateString()
    val tomorrowTaskNoTime = Task(
      id = "req_3_no_time_task",
      title = "Tomorrow Task Req 3 No Time",
      targetDate = tomorrowDate,
      taskDay = "tomorrow",
      startTime = null,
      reminderEnabled = true
    )
    
    localStore.saveTasks(listOf(tomorrowTaskNoTime))
    
    val migrationPrefs = context.getSharedPreferences("tomorrow_migration_prefs", Context.MODE_PRIVATE)
    migrationPrefs.edit().putString("last_migration_date", "2020-01-01").apply()
    
    TomorrowAutoMigrationEngine.checkAndMigrate(context)
    
    val hasAlarm = shadowAlarmManager.scheduledAlarms.any { 
      val shadowPending = shadowOf(it.operation)
      shadowPending.savedIntent.getStringExtra("task_id") == "req_3_no_time_task"
    }
    assertFalse("No alarm should be scheduled if the migrated task has no start time", hasAlarm)
  }

  @Test
  fun `test 4 - tomorrow task with reminder disabled does not schedule alarm after migration`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val localStore = TaskLocalStore(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    val shadowAlarmManager = shadowOf(alarmManager)
    
    shadowAlarmManager.scheduledAlarms.clear()
    
    val tomorrowDate = getTomorrowDateString()
    val tomorrowTaskNoReminder = Task(
      id = "req_4_no_reminder_task",
      title = "Tomorrow Task Req 4 No Reminder",
      targetDate = tomorrowDate,
      taskDay = "tomorrow",
      startTime = getFutureTimeToday(),
      reminderEnabled = false
    )
    
    localStore.saveTasks(listOf(tomorrowTaskNoReminder))
    
    val migrationPrefs = context.getSharedPreferences("tomorrow_migration_prefs", Context.MODE_PRIVATE)
    migrationPrefs.edit().putString("last_migration_date", "2020-01-01").apply()
    
    TomorrowAutoMigrationEngine.checkAndMigrate(context)
    
    val hasAlarm = shadowAlarmManager.scheduledAlarms.any { 
      val shadowPending = shadowOf(it.operation)
      shadowPending.savedIntent.getStringExtra("task_id") == "req_4_no_reminder_task"
    }
    assertFalse("No alarm should be scheduled if the migrated task has reminder disabled", hasAlarm)
  }

  @Test
  fun `test 5 - modifying tomorrow task properties does not trigger alarms`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val localStore = TaskLocalStore(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    val shadowAlarmManager = shadowOf(alarmManager)
    
    shadowAlarmManager.scheduledAlarms.clear()
    
    val tomorrowDate = getTomorrowDateString()
    val tomorrowTask = Task(
      id = "req_5_mod_task",
      title = "Tomorrow Task Req 5",
      targetDate = tomorrowDate,
      taskDay = "tomorrow",
      startTime = "10:00 AM",
      reminderEnabled = true
    )
    
    localStore.saveTasks(listOf(tomorrowTask))
    
    // Modify task in tomorrow's screen (e.g. change time, reminder status, alarm tone)
    val modifiedTomorrowTask = tomorrowTask.copy(
      startTime = "11:00 AM",
      reminderEnabled = false,
      ringtoneUri = "new_tone",
      title = "Tomorrow Task Req 5 Modified"
    )
    
    localStore.saveTasks(listOf(modifiedTomorrowTask))
    
    val hasAlarm = shadowAlarmManager.scheduledAlarms.any { 
      val shadowPending = shadowOf(it.operation)
      shadowPending.savedIntent.getStringExtra("task_id") == "req_5_mod_task"
    }
    assertFalse("Modifying a tomorrow task must NOT schedule any alarm", hasAlarm)
  }

  @Test
  fun `test 6 - after migration only one alarm exists and no duplicates`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val localStore = TaskLocalStore(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    val shadowAlarmManager = shadowOf(alarmManager)
    
    shadowAlarmManager.scheduledAlarms.clear()
    
    val tomorrowDate = getTomorrowDateString()
    val tomorrowTask = Task(
      id = "req_6_one_alarm",
      title = "Tomorrow Task Req 6",
      targetDate = tomorrowDate,
      taskDay = "tomorrow",
      startTime = getFutureTimeToday(),
      reminderEnabled = true
    )
    
    localStore.saveTasks(listOf(tomorrowTask))
    
    val migrationPrefs = context.getSharedPreferences("tomorrow_migration_prefs", Context.MODE_PRIVATE)
    migrationPrefs.edit().putString("last_migration_date", "2020-01-01").apply()
    
    TomorrowAutoMigrationEngine.checkAndMigrate(context)
    
    val alarmCount = shadowAlarmManager.scheduledAlarms.count { 
      val shadowPending = shadowOf(it.operation)
      shadowPending.savedIntent.getStringExtra("task_id") == "req_6_one_alarm"
    }
    assertEquals("Should have exactly one scheduled alarm after migration", 1, alarmCount)
  }

  @Test
  fun `test 7 - modifying migrated task in today list updates alarm correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val localStore = TaskLocalStore(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    val shadowAlarmManager = shadowOf(alarmManager)
    
    shadowAlarmManager.scheduledAlarms.clear()
    
    val todayDate = getCurrentDateString()
    val todayTask = Task(
      id = "req_7_mod_today",
      title = "Today Task Req 7",
      targetDate = todayDate,
      taskDay = "today",
      startTime = getFutureTimeToday(),
      reminderEnabled = true
    )
    
    localStore.saveTasks(listOf(todayTask))
    
    // Modify target time of today's task to another future time (e.g. 50 mins from now)
    val cal = Calendar.getInstance()
    cal.add(Calendar.MINUTE, 50)
    val futureTime2 = SimpleDateFormat("hh:mm a", Locale.US).format(cal.time)
    
    val updatedTodayTask = todayTask.copy(startTime = futureTime2)
    localStore.saveTasks(listOf(updatedTodayTask))
    
    // Ensure alarm is still scheduled
    val hasAlarm = shadowAlarmManager.scheduledAlarms.any { 
      val shadowPending = shadowOf(it.operation)
      shadowPending.savedIntent.getStringExtra("task_id") == "req_7_mod_today"
    }
    assertTrue("Alarm should still be present and updated", hasAlarm)
  }

  @Test
  fun `test 8 - deleting task cancels scheduled alarms`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val localStore = TaskLocalStore(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    val shadowAlarmManager = shadowOf(alarmManager)
    
    shadowAlarmManager.scheduledAlarms.clear()
    
    val todayDate = getCurrentDateString()
    val todayTask = Task(
      id = "req_8_delete_task",
      title = "Today Task Req 8",
      targetDate = todayDate,
      taskDay = "today",
      startTime = getFutureTimeToday(),
      reminderEnabled = true
    )
    
    localStore.saveTasks(listOf(todayTask))
    
    // Verify alarm exists
    var hasAlarm = shadowAlarmManager.scheduledAlarms.any { 
      val shadowPending = shadowOf(it.operation)
      shadowPending.savedIntent.getStringExtra("task_id") == "req_8_delete_task"
    }
    assertTrue("Alarm should be scheduled initially", hasAlarm)
    
    // Delete the task by saving empty list
    localStore.saveTasks(emptyList())
    
    // Verify alarm is cancelled
    hasAlarm = shadowAlarmManager.scheduledAlarms.any { 
      val shadowPending = shadowOf(it.operation)
      shadowPending.savedIntent.getStringExtra("task_id") == "req_8_delete_task"
    }
    assertFalse("Alarm must be cancelled when task is deleted", hasAlarm)
  }

  @Test
  fun `test 9 - completing task cancels scheduled alarm`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val localStore = TaskLocalStore(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    val shadowAlarmManager = shadowOf(alarmManager)
    
    shadowAlarmManager.scheduledAlarms.clear()
    
    val todayDate = getCurrentDateString()
    val todayTask = Task(
      id = "req_9_complete_task",
      title = "Today Task Req 9",
      targetDate = todayDate,
      taskDay = "today",
      startTime = getFutureTimeToday(),
      reminderEnabled = true,
      isCompleted = false
    )
    
    localStore.saveTasks(listOf(todayTask))
    
    // Complete the task
    val completedTask = todayTask.copy(isCompleted = true)
    localStore.saveTasks(listOf(completedTask))
    
    val hasAlarm = shadowAlarmManager.scheduledAlarms.any { 
      val shadowPending = shadowOf(it.operation)
      shadowPending.savedIntent.getStringExtra("task_id") == "req_9_complete_task"
    }
    assertFalse("Alarm must be cancelled when task is marked as completed", hasAlarm)
  }

  @Test
  fun `test 10 - uncompleting today task recreates scheduled alarm`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val localStore = TaskLocalStore(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    val shadowAlarmManager = shadowOf(alarmManager)
    
    shadowAlarmManager.scheduledAlarms.clear()
    
    val todayDate = getCurrentDateString()
    val completedTodayTask = Task(
      id = "req_10_uncomplete",
      title = "Today Task Req 10",
      targetDate = todayDate,
      taskDay = "today",
      startTime = getFutureTimeToday(),
      reminderEnabled = true,
      isCompleted = true
    )
    
    localStore.saveTasks(listOf(completedTodayTask))
    
    // Ensure no alarm exists for completed task
    var hasAlarm = shadowAlarmManager.scheduledAlarms.any { 
      val shadowPending = shadowOf(it.operation)
      shadowPending.savedIntent.getStringExtra("task_id") == "req_10_uncomplete"
    }
    assertFalse("Completed task should not have an active alarm", hasAlarm)
    
    // Uncomplete the task
    val activeTodayTask = completedTodayTask.copy(isCompleted = false)
    localStore.saveTasks(listOf(activeTodayTask))
    
    // Ensure alarm is scheduled now
    hasAlarm = shadowAlarmManager.scheduledAlarms.any { 
      val shadowPending = shadowOf(it.operation)
      shadowPending.savedIntent.getStringExtra("task_id") == "req_10_uncomplete"
    }
    assertTrue("Alarm must be recreated when task is returned to active", hasAlarm)
  }
}
