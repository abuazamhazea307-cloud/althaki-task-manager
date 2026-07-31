package com.example.features.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
  onDismiss: () -> Unit,
  onAddTask: (Task) -> Unit,
  taskToEdit: Task? = null,
  defaultTaskDay: String = "today"
) {
  var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
  var description by remember { mutableStateOf(taskToEdit?.description ?: "") }
  var startTime by remember { mutableStateOf(taskToEdit?.startTime ?: "") }
  var enableReminder by remember { mutableStateOf(taskToEdit?.reminderEnabled ?: com.example.features.settings.ReminderSettingsManager.reminderByDefault) }
  var selectedCategory by remember { mutableStateOf(taskToEdit?.category ?: "work") } // Default to 'work'
  var selectedTaskDay by remember { mutableStateOf(taskToEdit?.taskDay ?: defaultTaskDay) }

  var selectedRingtoneUri by remember { mutableStateOf(taskToEdit?.ringtoneUri) }
  var selectedRingtoneName by remember { mutableStateOf("") }
  var reminderNotification by remember { mutableStateOf(com.example.features.settings.ReminderSettingsManager.reminderNotification) }
  val context = androidx.compose.ui.platform.LocalContext.current

  // Reactive constraint: Reminder cannot be enabled without start time
  if (startTime.isBlank() && enableReminder) {
    enableReminder = false
  }

  LaunchedEffect(selectedRingtoneUri) {
    selectedRingtoneName = getRingtoneName(context, selectedRingtoneUri)
  }

  val ringtonePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
    contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
  ) { result ->
    if (result.resultCode == android.app.Activity.RESULT_OK) {
      val uri = result.data?.getParcelableExtra<android.net.Uri>(android.media.RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
      selectedRingtoneUri = uri?.toString()
    }
  }
  
  var showTimePickerDialog by remember { mutableStateOf(false) }

  val categories = listOf("work", "personal", "important")
  val scrollState = rememberScrollState()
  val focusRequester = remember { FocusRequester() }

  // Automatically request focus on opening
  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(24.dp))
        .background(MaterialTheme.colorScheme.surface)
        .testTag("add_task_dialog_root"),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(scrollState)
          .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (taskToEdit != null) stringResource(R.string.dialog_edit_title) else stringResource(R.string.dialog_add_title),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
          )

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(36.dp)
              .background(
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                CircleShape
              )
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = stringResource(R.string.dialog_close_desc),
              modifier = Modifier.size(18.dp),
              tint = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Title Input field (Auto-focusing)
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text(stringResource(R.string.input_title_label), style = MaterialTheme.typography.bodyLarge) },
          placeholder = { Text(stringResource(R.string.input_title_placeholder), style = MaterialTheme.typography.bodyLarge) },
          modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .testTag("task_title_input"),
          singleLine = false,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
          )
        )

        // Task Day Selection
        Text(
          text = stringResource(R.string.label_task_day),
          style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
          )
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val dayOptions = if (defaultTaskDay == "today") listOf("today") else listOf("tomorrow")
          dayOptions.forEach { dayOption ->
            val isSelected = selectedTaskDay == dayOption
            val chipBg = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            val chipTextColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            val localizedDay = when (dayOption) {
              "today" -> stringResource(R.string.today_tasks)
              "tomorrow" -> stringResource(R.string.tomorrow_tasks)
              else -> dayOption
            }

            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(chipBg)
                .clickable { selectedTaskDay = dayOption }
                .padding(vertical = 10.dp)
                .testTag("task_day_chip_${dayOption}"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = localizedDay,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 13.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = chipTextColor
                )
              )
            }
          }
        }

        // Task Start Time Input
        Box(
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = startTime,
            onValueChange = {},
            readOnly = true,
            enabled = true,
            label = { Text(stringResource(R.string.label_start_time), style = MaterialTheme.typography.bodyLarge) },
            placeholder = { Text(stringResource(R.string.placeholder_start_time), style = MaterialTheme.typography.bodyLarge) },
            trailingIcon = {
              if (startTime.isNotBlank()) {
                IconButton(
                  onClick = {
                    startTime = ""
                    enableReminder = false
                    // Cancel any active reminder for this task directly if editing
                    taskToEdit?.let {
                      com.example.features.tasks.ReminderScheduler.cancelReminder(context, it.id)
                    }
                  },
                  modifier = Modifier.testTag("clear_start_time_button")
                ) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear start time",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              } else {
                IconButton(
                  onClick = { showTimePickerDialog = true },
                  modifier = Modifier.testTag("open_time_picker_button")
                ) {
                  Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = stringResource(R.string.label_start_time),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("task_start_time_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = MaterialTheme.colorScheme.onSurface,
              unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
          )

          // Clickable overlay covering the field except the trailing icon (on the right)
          Box(
            modifier = Modifier
              .matchParentSize()
              .padding(end = 56.dp)
              .clickable { showTimePickerDialog = true }
          )
        }

        if (startTime.isNotBlank()) {
          // Notification Reminder (Optional Switch toggle)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f))
              .clickable { enableReminder = !enableReminder }
              .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Icon(
                imageVector = if (enableReminder) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                contentDescription = stringResource(R.string.label_reminder),
                tint = if (enableReminder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
              )
              Column {
                Text(
                  text = stringResource(R.string.label_reminder),
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                )
                Text(
                  text = stringResource(R.string.desc_reminder),
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                  )
                )
              }
            }

            Switch(
              checked = enableReminder,
              onCheckedChange = { enableReminder = it },
              modifier = Modifier.testTag("task_reminder_switch")
            )
          }


        }

        // Category Tag Selection
        Text(
          text = stringResource(R.string.label_task_category),
          style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
          )
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          categories.forEach { category ->
            val isSelected = selectedCategory == category
            val chipBg = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            val chipTextColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            val localizedCategory = when (category) {
              "work" -> stringResource(R.string.cat_work)
              "personal" -> stringResource(R.string.cat_personal)
              "important" -> stringResource(R.string.cat_important)
              else -> category
            }

            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(chipBg)
                .clickable { selectedCategory = category }
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = localizedCategory,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 13.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = chipTextColor
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dialog Actions (Cancel & Save Buttons)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("cancel_task_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = MaterialTheme.colorScheme.primary
            )
          ) {
            Text(
              text = stringResource(R.string.btn_cancel),
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
          }

          Button(
            onClick = {
              if (title.isNotBlank()) {
                val calculatedTargetDate = if (selectedTaskDay == "tomorrow") getTomorrowDateString() else getCurrentDateString()
                val updatedTask = if (taskToEdit != null) {
                  taskToEdit.copy(
                    title = title,
                    description = description,
                    category = selectedCategory,
                    startTime = startTime.ifBlank { null },
                    reminderEnabled = enableReminder,
                    ringtoneUri = selectedRingtoneUri,
                    taskDay = selectedTaskDay,
                    targetDate = calculatedTargetDate
                  )
                } else {
                  Task(
                    title = title,
                    description = description,
                    category = selectedCategory,
                    startTime = startTime.ifBlank { null },
                    reminderEnabled = enableReminder,
                    isCompleted = false,
                    ringtoneUri = selectedRingtoneUri,
                    taskDay = selectedTaskDay,
                    targetDate = calculatedTargetDate
                  )
                }
                onAddTask(updatedTask)
              }
            },
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("save_task_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary
            ),
            enabled = title.isNotBlank()
          ) {
            Text(
              text = stringResource(R.string.btn_save),
              style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = if (title.isNotBlank()) Color.White else Color.White.copy(alpha = 0.5f)
              )
            )
          }
        }
      }
    }
  }

  // Time Picker Dialog
  if (showTimePickerDialog) {
    Dialog(
      onDismissRequest = { showTimePickerDialog = false },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      Surface(
        modifier = Modifier
          .fillMaxWidth(0.9f)
          .clip(RoundedCornerShape(24.dp))
          .background(MaterialTheme.colorScheme.surface),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
          Text(
            text = stringResource(R.string.time_picker_title),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          )

          // Parse existing startTime if any to initialize state
          val parsedTime = remember(startTime) {
            if (startTime.isNotBlank()) {
              val parts = startTime.trim().split(" ")
              if (parts.size >= 2) {
                val timeParts = parts[0].split(":")
                val h = timeParts.getOrNull(0) ?: ""
                val m = timeParts.getOrNull(1) ?: ""
                val ampm = parts[1].uppercase()
                Triple(h, m, ampm)
              } else {
                Triple("", "", null)
              }
            } else {
              Triple("", "", null)
            }
          }

          var enteredHour by remember { mutableStateOf(parsedTime.first) }
          var enteredMinute by remember { mutableStateOf(parsedTime.second) }
          var selectedAmPm by remember { mutableStateOf<String?>(parsedTime.third) }

          CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // 1. Hour Box
              TimePartInput(
                value = enteredHour,
                onValueChange = { newValue ->
                  val clean = newValue.filter { it.isDigit() }
                  if (clean.length <= 2) {
                    val num = clean.toIntOrNull()
                    if (num == null || num in 1..12) {
                      enteredHour = clean
                    }
                  }
                },
                placeholder = "12",
                modifier = Modifier.testTag("time_picker_hour_input")
              )

              // 2. Colon separator
              Text(
                text = ":",
                style = TextStyle(
                  fontSize = 36.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
              )

              // 3. Minute Box
              TimePartInput(
                value = enteredMinute,
                onValueChange = { newValue ->
                  val clean = newValue.filter { it.isDigit() }
                  if (clean.length <= 2) {
                    val num = clean.toIntOrNull()
                    if (num == null || num in 0..59) {
                      enteredMinute = clean
                    }
                  }
                },
                placeholder = "00",
                modifier = Modifier.testTag("time_picker_minute_input")
              )

              // 4. Spacer gap before buttons
              Spacer(modifier = Modifier.width(20.dp))

              // 5. AM/PM vertical selector (صباحاً / مساءً)
              Column(
                modifier = Modifier.width(100.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                val amSelected = selectedAmPm == "AM"
                val pmSelected = selectedAmPm == "PM"

                // Unselected background: Dark slate. Selected: Blue.
                val unselectedBg = Color(0xFF374151)
                val selectedBg = Color(0xFF2196F3)

                // "صباحاً" Button
                Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (amSelected) selectedBg else unselectedBg)
                    .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                    .clickable {
                      selectedAmPm = "AM"
                    }
                    .testTag("time_picker_am_btn")
                ) {
                  Text(
                    text = "صباحاً",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                  )
                }

                // "مساءً" Button
                Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (pmSelected) selectedBg else unselectedBg)
                    .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                    .clickable {
                      selectedAmPm = "PM"
                    }
                    .testTag("time_picker_pm_btn")
                ) {
                  Text(
                    text = "مساءً",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                  )
                }
              }
            }
          }

          // Validation to ensure manual selection is complete
          val isValidHour = enteredHour.toIntOrNull()?.let { it in 1..12 } ?: false
          val isValidMinute = enteredMinute.toIntOrNull()?.let { it in 0..59 } ?: false
          val isSelectionComplete = isValidHour && isValidMinute && (selectedAmPm != null)

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
          ) {
            TextButton(
              onClick = { showTimePickerDialog = false },
              modifier = Modifier.testTag("time_picker_cancel_btn")
            ) {
              Text(
                text = stringResource(R.string.btn_cancel),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                if (isSelectionComplete) {
                  val h = enteredHour.toIntOrNull() ?: 12
                  val m = enteredMinute.toIntOrNull() ?: 0
                  val formattedMinute = String.format(Locale.US, "%02d", m)
                  val amPm = selectedAmPm ?: "AM"
                  startTime = "$h:$formattedMinute $amPm"
                  showTimePickerDialog = false
                }
              },
              enabled = isSelectionComplete,
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
              ),
              modifier = Modifier.testTag("time_picker_confirm_btn")
            ) {
              Text(
                text = stringResource(R.string.btn_save),
                style = MaterialTheme.typography.bodyLarge.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (isSelectionComplete) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
              )
            }
          }
        }
      }
    }
  }
}

fun getRingtoneName(context: android.content.Context, uriString: String?): String {
  val targetUri = if (uriString.isNullOrBlank()) {
    com.example.features.settings.ReminderSettingsManager.defaultAlarmSound
  } else {
    uriString
  }
  if (targetUri.isNullOrBlank()) return context.getString(R.string.ringtone_default)
  return try {
    val uri = android.net.Uri.parse(targetUri)
    val ringtone = android.media.RingtoneManager.getRingtone(context, uri)
    ringtone?.getTitle(context) ?: context.getString(R.string.ringtone_default)
  } catch (e: Exception) {
    context.getString(R.string.ringtone_default)
  }
}

@Composable
fun TimePartInput(
  value: String,
  onValueChange: (String) -> Unit,
  placeholder: String,
  modifier: Modifier = Modifier
) {
  var isFocused by remember { mutableStateOf(false) }

  BasicTextField(
    value = value,
    onValueChange = { newValue ->
      val clean = newValue.filter { it.isDigit() }
      if (clean.length <= 2) {
        onValueChange(clean)
      }
    },
    textStyle = TextStyle(
      textAlign = TextAlign.Center,
      fontWeight = FontWeight.Bold,
      fontSize = 32.sp,
      color = MaterialTheme.colorScheme.onSurface
    ),
    keyboardOptions = KeyboardOptions(
      keyboardType = KeyboardType.Number,
      imeAction = ImeAction.Done
    ),
    singleLine = true,
    decorationBox = { innerTextField ->
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .fillMaxSize()
          .background(
            color = if (isFocused) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
            shape = RoundedCornerShape(12.dp)
          )
          .border(
            width = if (isFocused) 2.dp else 1.dp,
            color = if (isFocused) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            shape = RoundedCornerShape(12.dp)
          )
      ) {
        if (value.isEmpty()) {
          Text(
            text = placeholder,
            style = TextStyle(
              textAlign = TextAlign.Center,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
              fontWeight = FontWeight.Bold,
              fontSize = 32.sp
            )
          )
        }
        innerTextField()
      }
    },
    modifier = modifier
      .width(75.dp)
      .height(72.dp)
      .onFocusChanged { isFocused = it.isFocused }
  )
}
