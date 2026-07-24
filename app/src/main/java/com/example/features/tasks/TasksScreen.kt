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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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

fun getYesterdayDateString(): String {
  val cal = java.util.Calendar.getInstance()
  cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
  val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
  return sdf.format(cal.time)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TasksScreen(navController: NavController) {
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

  val today = getCurrentDateString()

  // Daily rollover effect: When screen loads, find tasks from previous days that are incomplete and roll them over
  LaunchedEffect(today) {
    tasks.forEachIndexed { index, task ->
      if (!task.isCompleted && task.targetDate < today) {
        tasks[index] = task.copy(targetDate = today, isRolledOver = true)
      }
    }
  }

  // Filter tasks: display only those targeted for the current day (Today).
  // This correctly excludes completed tasks from previous days, while including rolled over incomplete tasks.
  val sortedTasks = remember(
    tasks.size,
    tasks.toList(),
    today,
    TaskSettingsManager.showCompleted,
    TaskSettingsManager.sortBy,
    TaskSettingsManager.taskOrder
  ) {
    val todayTasks = tasks.filter { it.targetDate == today }
    val filtered = if (TaskSettingsManager.showCompleted) {
      todayTasks
    } else {
      todayTasks.filter { !it.isCompleted }
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
    modifier = Modifier.fillMaxSize().testTag("tasks_screen_root"),
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
          icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.nav_home)) },
          label = { Text(stringResource(R.string.nav_home), style = MaterialTheme.typography.labelSmall) }
        )
        NavigationBarItem(
          selected = true,
          onClick = { /* Already on Tasks */ },
          icon = { Icon(Icons.Default.List, contentDescription = stringResource(R.string.nav_tasks)) },
          label = { Text(stringResource(R.string.nav_tasks), style = MaterialTheme.typography.labelSmall) }
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
          icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings_title)) },
          label = { Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.labelSmall) }
        )
      }
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 12.dp).testTag("add_task_fab")
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
          text = stringResource(R.string.tasks_title),
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

  // Add Task Dialog Overlay
  if (showAddDialog) {
    AddTaskDialog(
      onDismiss = { showAddDialog = false },
      onAddTask = { newTask ->
        tasks.add(newTask)
        showAddDialog = false
      }
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
          modifier = Modifier.testTag("confirm_delete_button")
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
          modifier = Modifier.testTag("cancel_delete_button")
        ) {
          Text(text = stringResource(R.string.btn_cancel))
        }
      },
      modifier = Modifier.testTag("delete_confirmation_dialog")
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
      modifier = Modifier.testTag("task_options_bottom_sheet")
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
          modifier = Modifier.fillMaxWidth().testTag("bottom_sheet_task_title")
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
            .testTag("bottom_sheet_edit_option"),
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
            .testTag("bottom_sheet_delete_option"),
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
            .testTag("bottom_sheet_cancel_button"),
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskRow(
  task: Task,
  isPending: Boolean,
  onToggleComplete: (Task) -> Unit,
  onLongClick: (Task) -> Unit
) {
  // Subtle pulse scale animation on the card
  val scale by if (isPending) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    infiniteTransition.animateFloat(
      initialValue = 0.99f,
      targetValue = 1.01f,
      animationSpec = infiniteRepeatable(
        animation = tween(1000, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
      ),
      label = "pulse_scale"
    )
  } else {
    remember { mutableStateOf(1f) }
  }

  // Subtle background color pulsing alpha
  val pulseAlpha by if (isPending) {
    val infiniteTransition = rememberInfiniteTransition(label = "alpha_pulse")
    infiniteTransition.animateFloat(
      initialValue = 0.7f,
      targetValue = 1.0f,
      animationSpec = infiniteRepeatable(
        animation = tween(1000, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse
      ),
      label = "pulse_alpha"
    )
  } else {
    remember { mutableStateOf(1f) }
  }

  // Soft sage/mint green background for pending state compatible with themes
  val basePendingColor = if (isSystemInDarkTheme()) {
    Color(0xFF1B3A1E)
  } else {
    Color(0xFFE8F5E9)
  }

  // Choose card background based on state. If rolled over, use a beautiful, quiet warm amber/gold color tint.
  val cardBg = if (isPending) {
    basePendingColor.copy(alpha = basePendingColor.alpha * pulseAlpha)
  } else if (task.isRolledOver && !task.isCompleted) {
    if (isSystemInDarkTheme()) Color(0xFF221F17) else Color(0xFFFFFBEB)
  } else {
    MaterialTheme.colorScheme.surface
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .scale(scale)
      .combinedClickable(
        onClick = {},
        onLongClick = { onLongClick(task) }
      )
      .testTag("task_item_${task.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = cardBg
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isPending) 2.dp else 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 18.dp, vertical = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Checkbox
      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(
            if (task.isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            else Color.Transparent
          )
          .border(
            width = 1.5.dp,
            color = if (task.isCompleted) {
              MaterialTheme.colorScheme.primary
            } else if (isPending) {
              if (isSystemInDarkTheme()) Color(0xFF81C784) else Color(0xFF2E7D32)
            } else {
              MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
            },
            shape = RoundedCornerShape(6.dp)
          )
          .clickable { onToggleComplete(task) }
          .testTag("task_checkbox_${task.id}"),
        contentAlignment = Alignment.Center
      ) {
        if (task.isCompleted) {
          Text(
            text = "✅",
            fontSize = 12.sp
          )
        }
      }

      // Title & Start Time
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = task.title,
            style = MaterialTheme.typography.titleMedium.copy(
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = if (task.isCompleted) {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
              } else if (isPending) {
                if (isSystemInDarkTheme()) Color(0xFFE8F5E9) else Color(0xFF1B5E20)
              } else {
                MaterialTheme.colorScheme.onSurface
              },
              textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).testTag("task_title_${task.id}")
          )

          if (task.isRolledOver && !task.isCompleted) {
            Box(
              modifier = Modifier
                .background(
                  color = if (isSystemInDarkTheme()) Color(0xFF3E2723).copy(alpha = 0.6f) else Color(0xFFFFE0B2),
                  shape = RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 8.dp, vertical = 2.dp)
                .testTag("task_rolled_over_badge_${task.id}")
            ) {
              Text(
                text = stringResource(R.string.badge_rolled_over),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSystemInDarkTheme()) Color(0xFFFFB74D) else Color(0xFFE65100)
                )
              )
            }
          }
        }

        if (TaskSettingsManager.showCategories || (TaskSettingsManager.showTaskTime && !task.startTime.isNullOrBlank())) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(top = 4.dp)
          ) {
            // Category Badge
            if (TaskSettingsManager.showCategories) {
              val localizedCategory = when (task.category) {
                "work" -> stringResource(R.string.cat_work)
                "personal" -> stringResource(R.string.cat_personal)
                "important" -> stringResource(R.string.cat_important)
                else -> task.category
              }
              val categoryColor = when (task.category) {
                "important" -> MaterialTheme.colorScheme.error
                "personal" -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.primary
              }

              Box(
                modifier = Modifier
                  .background(
                    color = categoryColor.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(6.dp)
                  )
                  .border(
                    width = 0.5.dp,
                    color = categoryColor.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(6.dp)
                  )
                  .padding(horizontal = 8.dp, vertical = 2.dp)
                  .testTag("task_category_badge_${task.id}")
              ) {
                Text(
                  text = localizedCategory,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = categoryColor
                  )
                )
              }
            }

            // Start Time Row
            if (TaskSettingsManager.showTaskTime && !task.startTime.isNullOrBlank()) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.testTag("task_time_container_${task.id}")
              ) {
                Icon(
                  imageVector = Icons.Default.AccessTime,
                  contentDescription = null,
                  tint = if (task.isCompleted) {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                  } else if (isPending) {
                    if (isSystemInDarkTheme()) Color(0xFF81C784) else Color(0xFF2E7D32)
                  } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                  },
                  modifier = Modifier.size(14.dp)
                )
                Text(
                  text = formatStartTimeForDisplay(task.startTime, java.util.Locale.getDefault()),
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = if (task.isCompleted) {
                      MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    } else if (isPending) {
                      if (isSystemInDarkTheme()) Color(0xFFC8E6C9) else Color(0xFF2E7D32)
                    } else {
                      MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    }
                  )
                )
              }
            }
          }
        }
      }
    }
  }
}

fun formatStartTimeForDisplay(startTime: String, locale: java.util.Locale): String {
    val cleanTime = startTime.trim()
    try {
        val regex = java.util.regex.Pattern.compile("(\\d{1,2}):(\\d{2})\\s*(AM|PM|am|pm|صباحًا|مساءً)?", java.util.regex.Pattern.CASE_INSENSITIVE)
        val matcher = regex.matcher(cleanTime)
        if (matcher.find()) {
            val hourStr = matcher.group(1) ?: return startTime
            val minuteStr = matcher.group(2) ?: return startTime
            val amPmIndicator = matcher.group(3)?.uppercase(java.util.Locale.US) ?: "AM"
            
            val isPm = amPmIndicator.contains("PM") || amPmIndicator.contains("مساءً")
            val hourInt = hourStr.toIntOrNull() ?: return startTime
            val minuteInt = minuteStr.toIntOrNull() ?: return startTime
            
            if (locale.language == "ar") {
                val formattedHour = String.format(java.util.Locale.US, "%02d", hourInt)
                val formattedMinute = String.format(java.util.Locale.US, "%02d", minuteInt)
                val suffix = if (isPm) "مساءً" else "صباحًا"
                return "$formattedHour:$formattedMinute $suffix"
            } else {
                val formattedHour = hourInt.toString()
                val formattedMinute = String.format(java.util.Locale.US, "%02d", minuteInt)
                val suffix = if (isPm) "PM" else "AM"
                return "$formattedHour:$formattedMinute $suffix"
            }
        }
    } catch (e: Exception) {
        // Fallback
    }
    return startTime
}
