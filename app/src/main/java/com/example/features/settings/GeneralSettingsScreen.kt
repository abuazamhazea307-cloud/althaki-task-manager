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
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tv
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
import androidx.compose.ui.graphics.vector.ImageVector
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
fun GeneralSettingsScreen(navController: NavController) {
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
      .testTag("general_settings_screen_root"),
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
            modifier = Modifier.testTag("general_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.general_settings_back_desc),
              tint = MaterialTheme.colorScheme.onBackground
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Column {
            Text(
              text = stringResource(R.string.general_settings_title),
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp
              )
            )
            Text(
              text = stringResource(R.string.settings_section_general_subtitle),
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

        // 1. Splash Screen Section Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("general_splash_card"),
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
              text = stringResource(R.string.general_splash_screen_title),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
              ),
              modifier = Modifier.padding(vertical = 12.dp)
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            // Show Splash Screen Toggle Switch
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
                    imageVector = Icons.Default.Tv,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                  Text(
                    text = stringResource(R.string.general_show_splash_title),
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = FontWeight.SemiBold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  )
                  Text(
                    text = stringResource(R.string.general_show_splash_desc),
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      fontSize = 11.sp
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.width(12.dp))

              Switch(
                checked = GeneralSettingsManager.showSplash,
                onCheckedChange = { value ->
                  triggerHaptic()
                  GeneralSettingsManager.setShowSplash(context, value)
                },
                modifier = Modifier.testTag("general_switch_show_splash"),
                colors = SwitchDefaults.colors(
                  checkedThumbColor = MaterialTheme.colorScheme.primary,
                  checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
              )
            }

            // Duration Selector (Visible only when Splash is enabled)
            AnimatedVisibility(
              visible = GeneralSettingsManager.showSplash,
              enter = fadeIn() + expandVertically(),
              exit = fadeOut() + shrinkVertically()
            ) {
              Column {
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                Text(
                  text = stringResource(R.string.general_splash_duration_title),
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                  ),
                  modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                )
                Text(
                  text = stringResource(R.string.general_splash_duration_desc),
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                  ),
                  modifier = Modifier.padding(bottom = 12.dp)
                )

                // Radio Button list for Duration
                DurationRadioOption(
                  label = stringResource(R.string.general_splash_duration_short),
                  selected = GeneralSettingsManager.splashDuration == GeneralSettingsManager.DURATION_SHORT,
                  testTag = "general_duration_short",
                  onClick = {
                    triggerHaptic()
                    GeneralSettingsManager.setSplashDuration(context, GeneralSettingsManager.DURATION_SHORT)
                  }
                )

                DurationRadioOption(
                  label = stringResource(R.string.general_splash_duration_normal),
                  selected = GeneralSettingsManager.splashDuration == GeneralSettingsManager.DURATION_NORMAL,
                  testTag = "general_duration_normal",
                  onClick = {
                    triggerHaptic()
                    GeneralSettingsManager.setSplashDuration(context, GeneralSettingsManager.DURATION_NORMAL)
                  }
                )

                DurationRadioOption(
                  label = stringResource(R.string.general_splash_duration_long),
                  selected = GeneralSettingsManager.splashDuration == GeneralSettingsManager.DURATION_LONG,
                  testTag = "general_duration_long",
                  onClick = {
                    triggerHaptic()
                    GeneralSettingsManager.setSplashDuration(context, GeneralSettingsManager.DURATION_LONG)
                  }
                )
              }
            }
          }
        }

        // 2. Motion & Feel Section Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("general_motion_card"),
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
              text = stringResource(R.string.general_animations_title),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
              ),
              modifier = Modifier.padding(vertical = 12.dp)
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            // Enable Animations Toggle
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
                    imageVector = Icons.Default.Animation,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                  Text(
                    text = stringResource(R.string.general_enable_animations_title),
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = FontWeight.SemiBold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  )
                  Text(
                    text = stringResource(R.string.general_enable_animations_desc),
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      fontSize = 11.sp
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.width(12.dp))

              Switch(
                checked = GeneralSettingsManager.enableAnimations,
                onCheckedChange = { value ->
                  triggerHaptic()
                  GeneralSettingsManager.setEnableAnimations(context, value)
                },
                modifier = Modifier.testTag("general_switch_animations"),
                colors = SwitchDefaults.colors(
                  checkedThumbColor = MaterialTheme.colorScheme.primary,
                  checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
              )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            // Enable Haptic Feedback Toggle
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
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                  Text(
                    text = stringResource(R.string.general_enable_haptic_title),
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = FontWeight.SemiBold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  )
                  Text(
                    text = stringResource(R.string.general_enable_haptic_desc),
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      fontSize = 11.sp
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.width(12.dp))

              Switch(
                checked = GeneralSettingsManager.enableHaptic,
                onCheckedChange = { value ->
                  // Always allow light physical tap feedback on haptic setting toggle
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  GeneralSettingsManager.setEnableHaptic(context, value)
                },
                modifier = Modifier.testTag("general_switch_haptic"),
                colors = SwitchDefaults.colors(
                  checkedThumbColor = MaterialTheme.colorScheme.primary,
                  checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
              )
            }
          }
        }

        // 3. Reset Section Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("general_reset_card"),
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
              text = stringResource(R.string.general_restore_defaults_title),
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
                .testTag("general_btn_restore_defaults"),
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
                  text = stringResource(R.string.general_restore_defaults_btn),
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                  )
                )
                Text(
                  text = stringResource(R.string.general_restore_defaults_desc),
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

  // Restore Default Settings Material 3 Confirmation Dialog
  if (showRestoreDialog) {
    AlertDialog(
      modifier = Modifier.testTag("general_restore_dialog"),
      onDismissRequest = {
        triggerHaptic()
        showRestoreDialog = false
      },
      title = {
        Text(
          text = stringResource(R.string.general_restore_dialog_title),
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold
          )
        )
      },
      text = {
        Text(
          text = stringResource(R.string.general_restore_dialog_msg),
          style = MaterialTheme.typography.bodyMedium
        )
      },
      confirmButton = {
        TextButton(
          onClick = {
            triggerHaptic()
            GeneralSettingsManager.restoreDefaults(context)
            showRestoreDialog = false
            Toast.makeText(
              context,
              context.getString(R.string.general_restore_success_toast),
              Toast.LENGTH_SHORT
            ).show()
          },
          modifier = Modifier.testTag("general_restore_dialog_confirm"),
          colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.error
          )
        ) {
          Text(text = stringResource(R.string.general_restore_dialog_confirm))
        }
      },
      dismissButton = {
        TextButton(
          onClick = {
            triggerHaptic()
            showRestoreDialog = false
          },
          modifier = Modifier.testTag("general_restore_dialog_cancel")
        ) {
          Text(text = stringResource(R.string.general_restore_dialog_cancel))
        }
      }
    )
  }
}

@Composable
fun DurationRadioOption(
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
