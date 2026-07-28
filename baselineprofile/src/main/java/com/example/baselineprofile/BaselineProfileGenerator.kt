package com.example.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val baselineRule = BaselineProfileRule()

    @Test
    fun generate() = baselineRule.collect(
        packageName = "com.aistudio.alzhakitaskmanager.qpxmwl",
        includeInStartupProfile = true
    ) {
        // App startup
        pressHome()
        startActivityAndWait()

        // 1. Wait for Home Screen / NavGraph & first frame
        device.wait(Until.hasObject(By.res("home_screen_root")), 5000)

        // 2. Navigation between main screens & opening tasks list
        val todayTasksTab = device.findObject(By.text("Today's Tasks")) ?: device.findObject(By.text("Today’s Tasks"))
        todayTasksTab?.click()
        device.waitForIdle()

        // 3. Creating a task
        val addTaskButton = device.findObject(By.desc("Add Task")) ?: device.findObject(By.text("Add Task"))
        addTaskButton?.click()
        device.waitForIdle()

        // Type task details
        val taskTitleInput = device.findObject(By.focused(true)) ?: device.findObject(By.clazz("android.widget.EditText"))
        taskTitleInput?.text = "Benchmark Task Title"
        device.waitForIdle()

        // Click Save/Add Task
        val saveTaskButton = device.findObject(By.text("Save")) ?: device.findObject(By.text("Add")) ?: device.findObject(By.text("Ok")) ?: device.findObject(By.text("OK"))
        saveTaskButton?.click()
        device.waitForIdle()

        // 4. Editing a task
        val taskItem = device.findObject(By.text("Benchmark Task Title"))
        taskItem?.click()
        device.waitForIdle()

        val editTitleInput = device.findObject(By.focused(true)) ?: device.findObject(By.clazz("android.widget.EditText"))
        editTitleInput?.text = "Updated Benchmark Task"
        device.waitForIdle()

        val updateButton = device.findObject(By.text("Save")) ?: device.findObject(By.text("Update")) ?: device.findObject(By.text("OK"))
        updateButton?.click()
        device.waitForIdle()

        // 5. Deleting a task
        val deleteButton = device.findObject(By.desc("Delete")) ?: device.findObject(By.desc("Delete Task"))
        deleteButton?.click()
        device.waitForIdle()

        // 6. Navigation to settings
        val settingsTab = device.findObject(By.text("Settings"))
        settingsTab?.click()
        device.waitForIdle()

        // 7. Go back / Return Home
        device.pressBack()
        device.waitForIdle()

        val homeTab = device.findObject(By.text("Home"))
        homeTab?.click()
        device.waitForIdle()
    }
}
