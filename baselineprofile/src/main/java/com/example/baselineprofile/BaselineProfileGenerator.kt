package com.example.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import androidx.test.uiautomator.UiObject2
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val baselineRule = BaselineProfileRule()

    @Test
    fun generate() = baselineRule.collect(
        packageName = "com.alzhaki.taskmanager",
        includeInStartupProfile = true
    ) {
        // App startup
        pressHome()
        startActivityAndWait()

        val pName = "com.alzhaki.taskmanager"

        // Helper to wait and retrieve an object
        fun waitForObject(by: androidx.test.uiautomator.BySelector, desc: String, timeout: Long = 5000): UiObject2 {
            val found = device.wait(Until.hasObject(by), timeout)
            assertTrue("Critical UI element not found: $desc", found)
            val obj = device.findObject(by)
            assertNotNull("UI object vanished after wait: $desc", obj)
            return obj
        }

        // 1. Wait for Home Screen
        waitForObject(By.res(pName, "home_screen_root"), "Home Screen Root")

        // 2. Click "Go to Tasks" button on Home screen to go to Tasks Screen
        val goToTasksBtn = waitForObject(By.res(pName, "go_to_tasks_button"), "Go To Tasks Button")
        goToTasksBtn.click()
        device.waitForIdle()

        // Verify Tasks screen appeared
        waitForObject(By.res(pName, "tasks_screen_root"), "Tasks Screen Root")

        // 3. Creating a task
        val addTaskFab = waitForObject(By.res(pName, "add_task_fab"), "Add Task FAB")
        addTaskFab.click()
        device.waitForIdle()

        // Verify dialog opened
        waitForObject(By.res(pName, "add_task_dialog_root"), "Add Task Dialog Root")

        // Enter title
        val titleInput = waitForObject(By.res(pName, "task_title_input"), "Task Title Input")
        titleInput.text = "Baseline Profile Task Title"
        device.waitForIdle()

        // Click Save Task
        val saveTaskBtn = waitForObject(By.res(pName, "save_task_button"), "Save Task Button")
        saveTaskBtn.click()
        device.waitForIdle()

        // Verify task exists in list
        waitForObject(By.text("Baseline Profile Task Title"), "Created Task in List")

        // 4. Editing the task
        val taskItemText = waitForObject(By.text("Baseline Profile Task Title"), "Task text to long-press")
        taskItemText.longClick()
        device.waitForIdle()

        // Verify Bottom Sheet is displayed
        waitForObject(By.res(pName, "task_options_bottom_sheet"), "Task Options Bottom Sheet")

        // Click edit option
        val editOption = waitForObject(By.res(pName, "bottom_sheet_edit_option"), "Edit Task Option")
        editOption.click()
        device.waitForIdle()

        // Verify Edit dialog opened
        waitForObject(By.res(pName, "add_task_dialog_root"), "Add Task Dialog Root for Editing")

        // Edit title
        val editTitleInput = waitForObject(By.res(pName, "task_title_input"), "Task Title Input for Editing")
        editTitleInput.text = "Baseline Profile Task Title Updated"
        device.waitForIdle()

        // Click Save Button
        val saveEditBtn = waitForObject(By.res(pName, "save_task_button"), "Save Task Button for Editing")
        saveEditBtn.click()
        device.waitForIdle()

        // Verify updated task exists in list
        waitForObject(By.text("Baseline Profile Task Title Updated"), "Updated Task in List")

        // 5. Deleting the task
        val updatedTaskItemText = waitForObject(By.text("Baseline Profile Task Title Updated"), "Updated Task text to long-press")
        updatedTaskItemText.longClick()
        device.waitForIdle()

        // Verify bottom sheet opened
        waitForObject(By.res(pName, "task_options_bottom_sheet"), "Task Options Bottom Sheet for Deletion")

        // Click delete option
        val deleteOption = waitForObject(By.res(pName, "bottom_sheet_delete_option"), "Delete Task Option")
        deleteOption.click()
        device.waitForIdle()

        // Verify confirmation dialog opened
        waitForObject(By.res(pName, "delete_confirmation_dialog"), "Delete Confirmation Dialog")

        // Confirm delete
        val confirmDeleteBtn = waitForObject(By.res(pName, "confirm_delete_button"), "Confirm Delete Button")
        confirmDeleteBtn.click()
        device.waitForIdle()

        // Verify task disappeared from screen
        val deleted = device.wait(Until.gone(By.text("Baseline Profile Task Title Updated")), 5000)
        assertTrue("Task was not deleted or is still visible", deleted)

        // 6. Navigate to Tomorrow's Tasks Screen
        val tomorrowTab = waitForObject(By.res(pName, "nav_tab_tomorrow_tasks"), "Tomorrow's Tasks Navigation Tab")
        tomorrowTab.click()
        device.waitForIdle()

        // Verify tomorrow tasks screen is open
        waitForObject(By.res(pName, "tomorrow_tasks_screen_root"), "Tomorrow Tasks Screen Root")

        // 7. Navigate to Settings Screen
        val settingsTab = waitForObject(By.res(pName, "nav_tab_settings"), "Settings Navigation Tab")
        settingsTab.click()
        device.waitForIdle()

        // Verify settings screen is open
        waitForObject(By.res(pName, "settings_screen_title"), "Settings Screen Title")

        // 8. Go back to Home Screen
        val homeTab = waitForObject(By.res(pName, "nav_tab_home"), "Home Navigation Tab")
        homeTab.click()
        device.waitForIdle()

        // Verify back on home screen
        waitForObject(By.res(pName, "home_screen_root"), "Home Screen Root at End")
    }
}
