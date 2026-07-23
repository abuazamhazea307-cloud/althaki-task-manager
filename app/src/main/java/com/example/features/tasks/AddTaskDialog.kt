package com.example.features.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
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
  taskToEdit: Task? = null
) {
  var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
  var description by remember { mutableStateOf(taskToEdit?.description ?: "") }
  var startTime by remember { mutableStateOf(taskToEdit?.startTime ?: "") }
  var enableReminder by remember { mutableStateOf(taskToEdit?.reminderEnabled ?: false) }
  var selectedCategory by remember { mutableStateOf(taskToEdit?.category ?: "work") } // Default to 'work'

  var selectedRingtoneUri by remember { mutableStateOf(taskToEdit?.ringtoneUri) }
  var selectedRingtoneName by remember { mutableStateOf("") }
  val context = androidx.compose.ui.platform.LocalContext.current

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
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
          )
        )

        // Description Input field
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text(stringResource(R.string.input_desc_label), style = MaterialTheme.typography.bodyLarge) },
          placeholder = { Text(stringResource(R.string.input_desc_placeholder), style = MaterialTheme.typography.bodyLarge) },
          modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .testTag("task_description_input"),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
          )
        )

        // Task Start Time Input (Optional clickable box)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { showTimePickerDialog = true }
        ) {
          OutlinedTextField(
            value = startTime,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text(stringResource(R.string.label_start_time), style = MaterialTheme.typography.bodyLarge) },
            placeholder = { Text(stringResource(R.string.placeholder_start_time), style = MaterialTheme.typography.bodyLarge) },
            trailingIcon = {
              Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = stringResource(R.string.label_start_time),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("task_start_time_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              disabledTextColor = MaterialTheme.colorScheme.onSurface,
              disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
              disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
              disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
              disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }

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

        // Custom Ringtone Selector
        if (enableReminder) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f))
              .clickable {
                val intent = android.content.Intent(android.media.RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                  putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_TYPE, android.media.RingtoneManager.TYPE_ALARM)
                  putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_TITLE, context.getString(R.string.ringtone_label))
                  putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, selectedRingtoneUri?.let { android.net.Uri.parse(it) })
                  putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                  putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                }
                ringtonePickerLauncher.launch(intent)
              }
              .padding(horizontal = 16.dp, vertical = 12.dp)
              .testTag("ringtone_selector_row"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Audiotrack,
                contentDescription = stringResource(R.string.ringtone_label),
                tint = MaterialTheme.colorScheme.primary
              )
              Column {
                Text(
                  text = stringResource(R.string.ringtone_label),
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                )
                Text(
                  text = selectedRingtoneName,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                  ),
                  maxLines = 1,
                  overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                  modifier = Modifier.testTag("selected_ringtone_name")
                )
              }
            }

            Icon(
              imageVector = Icons.Default.ChevronRight,
              contentDescription = "Navigate",
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
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
                val updatedTask = if (taskToEdit != null) {
                  taskToEdit.copy(
                    title = title,
                    description = description,
                    category = selectedCategory,
                    startTime = startTime.ifBlank { null },
                    reminderEnabled = enableReminder,
                    ringtoneUri = selectedRingtoneUri
                  )
                } else {
                  Task(
                    title = title,
                    description = description,
                    category = selectedCategory,
                    startTime = startTime.ifBlank { null },
                    reminderEnabled = enableReminder,
                    isCompleted = false,
                    ringtoneUri = selectedRingtoneUri
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

          val timePickerState = rememberTimePickerState(
            initialHour = 12,
            initialMinute = 0,
            is24Hour = false
          )

          TimePicker(
            state = timePickerState,
            modifier = Modifier.testTag("time_picker")
          )

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
                val hour = timePickerState.hour
                val minute = timePickerState.minute
                val amPm = if (hour >= 12) "PM" else "AM"
                val displayHour = when {
                  hour == 0 -> 12
                  hour > 12 -> hour - 12
                  else -> hour
                }
                val formattedMinute = String.format(Locale.US, "%02d", minute)
                startTime = "$displayHour:$formattedMinute $amPm"
                showTimePickerDialog = false
              },
              modifier = Modifier.testTag("time_picker_confirm_btn")
            ) {
              Text(
                text = stringResource(R.string.btn_save),
                style = MaterialTheme.typography.bodyLarge.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White
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
  if (uriString.isNullOrBlank()) return context.getString(R.string.ringtone_default)
  return try {
    val uri = android.net.Uri.parse(uriString)
    val ringtone = android.media.RingtoneManager.getRingtone(context, uri)
    ringtone?.getTitle(context) ?: context.getString(R.string.ringtone_default)
  } catch (e: Exception) {
    context.getString(R.string.ringtone_default)
  }
}
