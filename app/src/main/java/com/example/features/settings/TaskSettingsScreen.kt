package com.example.features.settings

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.R

@Composable
fun TaskSettingsScreen(navController: NavController) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  val scrollState = rememberScrollState()

  var showRestoreDialog by remember { mutableStateOf(false) }

  // Haptic feedback trigger helper
  val triggerHaptic = {
    if (GeneralSettingsManager.enableHaptic) {
      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .testTag("task_settings_screen_root"),
    topBar = {
      // Modern Custom M3 Header Row (Safe Area Aware)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.background)
          .padding(top = 16.dp, bottom = 8.dp, start = 8.dp, end = 16.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          IconButton(
            onClick = {
              triggerHaptic()
              navController.popBackStack()
            },
            modifier = Modifier.testTag("task_settings_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.tasks_settings_back_desc),
              tint = MaterialTheme.colorScheme.onBackground
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Column {
            Text(
              text = stringResource(R.string.tasks_settings_title),
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp
              )
            )
            Text(
              text = stringResource(R.string.settings_section_tasks_subtitle),
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
              )
            )
          }
        }
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              MaterialTheme.colorScheme.background,
              MaterialTheme.colorScheme.primary.copy(alpha = 0.02f),
              MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
            )
          )
        )
        .padding(paddingValues)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(scrollState)
          .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
      ) {

        // 1. Task Order & Sorting Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("task_order_sorting_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 8.dp)
          ) {
            Text(
              text = stringResource(R.string.tasks_order_title),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
              ),
              modifier = Modifier.padding(vertical = 12.dp)
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            Text(
              text = stringResource(R.string.tasks_order_desc),
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              ),
              modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            )

            // Task Order Options
            TaskRadioOption(
              label = stringResource(R.string.tasks_order_oldest),
              selected = TaskSettingsManager.taskOrder == TaskSettingsManager.ORDER_OLDEST_FIRST,
              testTag = "task_order_oldest",
              onClick = {
                triggerHaptic()
                TaskSettingsManager.setTaskOrder(context, TaskSettingsManager.ORDER_OLDEST_FIRST)
              }
            )

            TaskRadioOption(
              label = stringResource(R.string.tasks_order_newest),
              selected = TaskSettingsManager.taskOrder == TaskSettingsManager.ORDER_NEWEST_FIRST,
              testTag = "task_order_newest",
              onClick = {
                triggerHaptic()
                TaskSettingsManager.setTaskOrder(context, TaskSettingsManager.ORDER_NEWEST_FIRST)
              }
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            Text(
              text = stringResource(R.string.tasks_sort_by_title),
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
              ),
              modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            Text(
              text = stringResource(R.string.tasks_sort_by_desc),
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              ),
              modifier = Modifier.padding(bottom = 12.dp)
            )

            // Default Sorting Options
            TaskRadioOption(
              label = stringResource(R.string.tasks_sort_creation),
              selected = TaskSettingsManager.sortBy == TaskSettingsManager.SORT_CREATION_DATE,
              testTag = "task_sort_creation",
              onClick = {
                triggerHaptic()
                TaskSettingsManager.setSortBy(context, TaskSettingsManager.SORT_CREATION_DATE)
              }
            )

            TaskRadioOption(
              label = stringResource(R.string.tasks_sort_start_time),
              selected = TaskSettingsManager.sortBy == TaskSettingsManager.SORT_START_TIME,
              testTag = "task_sort_start_time",
              onClick = {
                triggerHaptic()
                TaskSettingsManager.setSortBy(context, TaskSettingsManager.SORT_START_TIME)
              }
            )

            TaskRadioOption(
              label = stringResource(R.string.tasks_sort_title_az),
              selected = TaskSettingsManager.sortBy == TaskSettingsManager.SORT_TITLE,
              testTag = "task_sort_title_az",
              onClick = {
                triggerHaptic()
                TaskSettingsManager.setSortBy(context, TaskSettingsManager.SORT_TITLE)
              }
            )
          }
        }

        // 2. Completed Tasks Visibility Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("task_completed_visibility_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 8.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .background(
                      color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                      shape = CircleShape
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                  Text(
                    text = stringResource(R.string.tasks_show_completed_title),
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = FontWeight.SemiBold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  )
                  Text(
                    text = stringResource(R.string.tasks_show_completed_desc),
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      fontSize = 11.sp
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.width(12.dp))

              Switch(
                checked = TaskSettingsManager.showCompleted,
                onCheckedChange = { value ->
                  triggerHaptic()
                  TaskSettingsManager.setShowCompleted(context, value)
                },
                modifier = Modifier.testTag("task_switch_show_completed"),
                colors = SwitchDefaults.colors(
                  checkedThumbColor = MaterialTheme.colorScheme.primary,
                  checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
              )
            }
          }
        }

        // 3. Display Preferences Card (Task Time & Categories)
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("task_display_preferences_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 8.dp)
          ) {
            Text(
              text = stringResource(R.string.settings_section_appearance_title),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
              ),
              modifier = Modifier.padding(vertical = 12.dp)
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            // Show Task Time Toggle
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .background(
                      color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                      shape = CircleShape
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                  Text(
                    text = stringResource(R.string.tasks_show_time_title),
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = FontWeight.SemiBold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  )
                  Text(
                    text = stringResource(R.string.tasks_show_time_desc),
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      fontSize = 11.sp
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.width(12.dp))

              Switch(
                checked = TaskSettingsManager.showTaskTime,
                onCheckedChange = { value ->
                  triggerHaptic()
                  TaskSettingsManager.setShowTaskTime(context, value)
                },
                modifier = Modifier.testTag("task_switch_show_time"),
                colors = SwitchDefaults.colors(
                  checkedThumbColor = MaterialTheme.colorScheme.primary,
                  checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
              )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            // Show Categories Toggle
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .background(
                      color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                      shape = CircleShape
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                  Text(
                    text = stringResource(R.string.tasks_show_categories_title),
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = FontWeight.SemiBold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  )
                  Text(
                    text = stringResource(R.string.tasks_show_categories_desc),
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      fontSize = 11.sp
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.width(12.dp))

              Switch(
                checked = TaskSettingsManager.showCategories,
                onCheckedChange = { value ->
                  triggerHaptic()
                  TaskSettingsManager.setShowCategories(context, value)
                },
                modifier = Modifier.testTag("task_switch_show_categories"),
                colors = SwitchDefaults.colors(
                  checkedThumbColor = MaterialTheme.colorScheme.primary,
                  checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
              )
            }
          }
        }

        // 4. Reset Task Settings Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("task_reset_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.08f)
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 8.dp)
          ) {
            Text(
              text = stringResource(R.string.tasks_restore_defaults_title),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                fontSize = 16.sp
              ),
              modifier = Modifier.padding(vertical = 12.dp)
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f))

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  triggerHaptic()
                  showRestoreDialog = true
                }
                .padding(vertical = 16.dp)
                .testTag("task_btn_restore_defaults"),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .background(
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                    shape = CircleShape
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.error,
                  modifier = Modifier.size(18.dp)
                )
              }
              Spacer(modifier = Modifier.width(16.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = stringResource(R.string.tasks_restore_defaults_btn),
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                  )
                )
                Text(
                  text = stringResource(R.string.tasks_restore_defaults_desc),
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                    fontSize = 11.sp
                  )
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  // Restore Default Task Settings Material 3 Confirmation Dialog
  if (showRestoreDialog) {
    AlertDialog(
      modifier = Modifier.testTag("task_restore_dialog"),
      onDismissRequest = {
        triggerHaptic()
        showRestoreDialog = false
      },
      title = {
        Text(
          text = stringResource(R.string.tasks_restore_dialog_title),
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold
          )
        )
      },
      text = {
        Text(
          text = stringResource(R.string.tasks_restore_dialog_msg),
          style = MaterialTheme.typography.bodyMedium
        )
      },
      confirmButton = {
        TextButton(
          onClick = {
            triggerHaptic()
            TaskSettingsManager.restoreDefaults(context)
            showRestoreDialog = false
            Toast.makeText(
              context,
              context.getString(R.string.tasks_restore_success_toast),
              Toast.LENGTH_SHORT
            ).show()
          },
          modifier = Modifier.testTag("task_restore_dialog_confirm"),
          colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.error
          )
        ) {
          Text(text = stringResource(R.string.tasks_restore_dialog_confirm))
        }
      },
      dismissButton = {
        TextButton(
          onClick = {
            triggerHaptic()
            showRestoreDialog = false
          },
          modifier = Modifier.testTag("task_restore_dialog_cancel")
        ) {
          Text(text = stringResource(R.string.tasks_restore_dialog_cancel))
        }
      }
    )
  }
}

@Composable
fun TaskRadioOption(
  label: String,
  selected: Boolean,
  testTag: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(vertical = 8.dp)
      .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically
  ) {
    RadioButton(
      selected = selected,
      onClick = onClick
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium.copy(
        color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
      )
    )
  }
}
