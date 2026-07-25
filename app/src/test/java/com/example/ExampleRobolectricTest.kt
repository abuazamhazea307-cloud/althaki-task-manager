package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.features.tasks.Task
import com.example.features.tasks.TaskLocalStore
import com.example.features.tasks.TomorrowAutoMigrationEngine
import com.example.features.tasks.getCurrentDateString
import com.example.features.tasks.getTomorrowDateString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

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
  fun `test tomorrow auto migration engine`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val localStore = TaskLocalStore(context)
    
    // Clear and set up test tasks
    val todayDate = getCurrentDateString()
    val tomorrowDate = getTomorrowDateString()
    
    val task1 = Task(
      id = "test_1",
      title = "Task Today",
      targetDate = todayDate,
      taskDay = "today"
    )
    val task2 = Task(
      id = "test_2",
      title = "Task Tomorrow Active",
      targetDate = tomorrowDate,
      taskDay = "tomorrow",
      isCompleted = false
    )
    val task3 = Task(
      id = "test_3",
      title = "Task Tomorrow Completed",
      targetDate = tomorrowDate,
      taskDay = "tomorrow",
      isCompleted = true
    )
    
    localStore.saveTasks(listOf(task1, task2, task3))
    
    // Set last migration date to a previous day in shared prefs to simulate a date change
    val migrationPrefs = context.getSharedPreferences("tomorrow_migration_prefs", Context.MODE_PRIVATE)
    migrationPrefs.edit().putString("last_migration_date", "2020-01-01").apply()
    
    // Execute migration
    TomorrowAutoMigrationEngine.checkAndMigrate(context)
    
    // Load migrated tasks
    val migratedTasks = localStore.loadTasks() ?: emptyList()
    
    val migratedActive = migratedTasks.find { it.id == "test_2" }
    val migratedCompleted = migratedTasks.find { it.id == "test_3" }
    
    // Verify that the active tomorrow task is migrated to today
    assertEquals("today", migratedActive?.taskDay)
    assertEquals(todayDate, migratedActive?.targetDate)
    
    // Verify that the completed tomorrow task remained untouched
    assertEquals("tomorrow", migratedCompleted?.taskDay)
    assertEquals(tomorrowDate, migratedCompleted?.targetDate)
  }
}
