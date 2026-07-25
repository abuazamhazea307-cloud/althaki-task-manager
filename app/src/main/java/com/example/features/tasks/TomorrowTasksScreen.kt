package com.example.features.tasks

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.R
import com.example.navigation.Screen
import com.example.features.settings.TaskSettingsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TomorrowTasksScreen(navController: NavController) {
  val context = androidx.compose.ui.platform.LocalContext.current
  val taskStore = remember { TaskLocalStore(context) }

  // Initialize tasks from local storage
  val tasks = remember {
    val saved = taskStore.loadTasks()
    val list = mutableStateListOf<Task>()
    if (saved != null) {
      list.addAll(saved)
    }
    list
  }

  // Save tasks to local storage whenever the list changes
  LaunchedEffect(tasks.toList()) {
    taskStore.saveTasks(tasks.toList())
  }

  var showAddDialog by remember { mutableStateOf(false) }

  var showBottomSheet by remember { mutableStateOf(false) }
  var selectedTaskForSheet by remember { mutableStateOf<Task?>(null) }

  var showEditDialog by remember { mutableStateOf(false) }
  var taskToEdit by remember { mutableStateOf<Task?>(null) }

  var showDeleteConfirm by remember { mutableStateOf(false) }
  var taskToDelete by remember { mutableStateOf<Task?>(null) }

  // Coroutine scope and map to track active jobs for pending completion
  val coroutineScope = rememberCoroutineScope()
  val pendingTasks = remember { mutableStateMapOf<String, Job>() }

  val tomorrow = getTomorrowDateString()

  // Filter tasks: display only those targeted for tomorrow and of type "tomorrow".
  val sortedTasks = remember(
    tasks.size,
    tasks.toList(),
    tomorrow,
    TaskSettingsManager.showCompleted,
    TaskSettingsManager.sortBy,
    TaskSettingsManager.taskOrder
  ) {
    val tomorrowTasks = tasks.filter { it.targetDate == tomorrow && it.taskDay == "tomorrow" }
    val filtered = if (TaskSettingsManager.showCompleted) {
      tomorrowTasks
    } else {
      tomorrowTasks.filter { !it.isCompleted }
    }

    val sorted = when (TaskSettingsManager.sortBy) {
      TaskSettingsManager.SORT_START_TIME -> {
        filtered.sortedWith(
          compareBy<Task> { it.startTime.isNullOrBlank() }
            .thenBy { it.startTime ?: "" }
            .thenBy { it.createdAt }
        )
      }
      TaskSettingsManager.SORT_TITLE -> {
        filtered.sortedWith(
          compareBy<Task> { it.title.lowercase() }
            .thenBy { it.createdAt }
        )
      }
      else -> { // SORT_CREATION_DATE
        filtered.sortedBy { it.createdAt }
      }
    }

    if (TaskSettingsManager.taskOrder == TaskSettingsManager.ORDER_NEWEST_FIRST) {
      sorted.reversed()
    } else {
      sorted
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize().testTag("tomorrow_tasks_screen_root"),
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
      ) {
        NavigationBarItem(
          selected = false,
          onClick = {
            navController.navigate(Screen.Home.route) {
              popUpTo(Screen.Home.route) { inclusive = true }
              launchSingleTop = true
            }
          },
          icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.home)) },
          label = { Text(stringResource(R.string.home), style = MaterialTheme.typography.labelSmall) }
        )
        NavigationBarItem(
          selected = false,
          onClick = {
            navController.navigate(Screen.Tasks.route) {
              popUpTo(Screen.Home.route) { saveState = true }
              launchSingleTop = true
              restoreState = true
            }
          },
          icon = { Icon(Icons.Default.CheckCircle, contentDescription = stringResource(R.string.today_tasks)) },
          label = { Text(stringResource(R.string.today_tasks), style = MaterialTheme.typography.labelSmall) }
        )
        NavigationBarItem(
          selected = true,
          onClick = { /* Already on Tomorrow Tasks */ },
          icon = { Icon(Icons.Default.Event, contentDescription = stringResource(R.string.tomorrow_tasks)) },
          label = { Text(stringResource(R.string.tomorrow_tasks), style = MaterialTheme.typography.labelSmall) }
        )
        NavigationBarItem(
          selected = false,
          onClick = {
            navController.navigate(Screen.Settings.route) {
              popUpTo(Screen.Home.route) { saveState = true }
              launchSingleTop = true
              restoreState = true
            }
          },
          icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings)) },
          label = { Text(stringResource(R.string.settings), style = MaterialTheme.typography.labelSmall) }
        )
      }
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 12.dp).testTag("add_tomorrow_task_fab")
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = stringResource(R.string.dialog_add_title),
          modifier = Modifier.size(24.dp)
        )
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(paddingValues)
        .padding(18.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = stringResource(R.string.tomorrow_tasks),
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Tasks List or Empty State
      if (sortedTasks.isEmpty()) {
        Box(
          modifier = Modifier.fillMaxSize().weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
          ) {
            Icon(
              imageVector = Icons.Default.HourglassEmpty,
              contentDescription = stringResource(R.string.empty_desc),
              tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
              modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = stringResource(R.string.empty_desc),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
              )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = stringResource(R.string.empty_subtitle),
              style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
              )
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxWidth().weight(1f),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(sortedTasks, key = { it.id }) { task ->
            val isPending = pendingTasks.containsKey(task.id)
            TaskRow(
              task = task,
              isPending = isPending,
              onToggleComplete = { toggledTask ->
                if (toggledTask.isCompleted) {
                  // Direct transition back to uncompleted
                  val index = tasks.indexOfFirst { it.id == toggledTask.id }
                  if (index != -1) {
                    tasks[index] = toggledTask.copy(isCompleted = false, completedAt = null)
                  }
                } else {
                  // If it's already pending, another click cancels the completion workflow
                  if (pendingTasks.containsKey(toggledTask.id)) {
                    pendingTasks[toggledTask.id]?.cancel()
                    pendingTasks.remove(toggledTask.id)
                  } else {
                    // Set to pending with a 3-second delay
                    val job = coroutineScope.launch {
                      delay(3000L)
                      val index = tasks.indexOfFirst { it.id == toggledTask.id }
                      if (index != -1) {
                        tasks[index] = toggledTask.copy(
                          isCompleted = true,
                          completedAt = System.currentTimeMillis()
                        )
                      }
                      pendingTasks.remove(toggledTask.id)
                    }
                    pendingTasks[toggledTask.id] = job
                  }
                }
              },
              onLongClick = { clickedTask ->
                selectedTaskForSheet = clickedTask
                showBottomSheet = true
              }
            )
          }
        }
      }
    }
  }

  // Add Task Dialog Overlay (defaults to "tomorrow")
  if (showAddDialog) {
    AddTaskDialog(
      onDismiss = { showAddDialog = false },
      onAddTask = { newTask ->
        tasks.add(newTask)
        showAddDialog = false
      },
      defaultTaskDay = "tomorrow"
    )
  }

  // Edit Task Dialog Overlay
  if (showEditDialog && taskToEdit != null) {
    AddTaskDialog(
      onDismiss = {
        showEditDialog = false
        taskToEdit = null
      },
      taskToEdit = taskToEdit,
      onAddTask = { updatedTask ->
        val index = tasks.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
          tasks[index] = updatedTask
        }
        showEditDialog = false
        taskToEdit = null
      }
    )
  }

  // Delete Task Confirmation Dialog Overlay
  if (showDeleteConfirm && taskToDelete != null) {
    AlertDialog(
      onDismissRequest = {
        showDeleteConfirm = false
        taskToDelete = null
      },
      title = {
        Text(
          text = stringResource(R.string.delete_confirm_title),
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      },
      text = {
        Text(
          text = stringResource(R.string.delete_confirm_desc),
          style = MaterialTheme.typography.bodyMedium
        )
      },
      confirmButton = {
        Button(
          onClick = {
            val task = taskToDelete
            if (task != null) {
              // Cancel alarm first
              ReminderScheduler.cancelReminder(context, task.id)
              // Delete task
              tasks.removeIf { it.id == task.id }
            }
            showDeleteConfirm = false
            taskToDelete = null
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
          ),
          modifier = Modifier.testTag("confirm_delete_tomorrow_button")
        ) {
          Text(
            text = stringResource(R.string.bottom_sheet_delete),
            color = Color.White
          )
        }
      },
      dismissButton = {
        TextButton(
          onClick = {
            showDeleteConfirm = false
            taskToDelete = null
          },
          modifier = Modifier.testTag("cancel_delete_tomorrow_button")
        ) {
          Text(text = stringResource(R.string.btn_cancel))
        }
      },
      modifier = Modifier.testTag("delete_confirmation_tomorrow_dialog")
    )
  }

  // Bottom Sheet Overlay
  if (showBottomSheet && selectedTaskForSheet != null) {
    ModalBottomSheet(
      onDismissRequest = {
        showBottomSheet = false
        selectedTaskForSheet = null
      },
      sheetState = rememberModalBottomSheetState(),
      modifier = Modifier.testTag("tomorrow_task_options_bottom_sheet")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 36.dp, start = 24.dp, end = 24.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Text(
          text = selectedTaskForSheet!!.title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.fillMaxWidth().testTag("bottom_sheet_tomorrow_task_title")
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

        // Edit
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
              val task = selectedTaskForSheet
              showBottomSheet = false
              selectedTaskForSheet = null
              taskToEdit = task
              showEditDialog = true
            }
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .testTag("bottom_sheet_tomorrow_edit_option"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = stringResource(R.string.bottom_sheet_edit),
            tint = MaterialTheme.colorScheme.primary
          )
          Text(
            text = stringResource(R.string.bottom_sheet_edit),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
          )
        }

        // Delete
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
              val task = selectedTaskForSheet
              showBottomSheet = false
              selectedTaskForSheet = null
              taskToDelete = task
              showDeleteConfirm = true
            }
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .testTag("bottom_sheet_tomorrow_delete_option"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(R.string.bottom_sheet_delete),
            tint = MaterialTheme.colorScheme.error
          )
          Text(
            text = stringResource(R.string.bottom_sheet_delete),
            style = MaterialTheme.typography.bodyLarge.copy(
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.error
            )
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Cancel
        OutlinedButton(
          onClick = {
            showBottomSheet = false
            selectedTaskForSheet = null
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("bottom_sheet_tomorrow_cancel_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(
            text = stringResource(R.string.bottom_sheet_cancel),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    }
  }
}
