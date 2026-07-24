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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Repeat
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
import com.example.features.tasks.getRingtoneName

@Composable
fun ReminderSettingsScreen(navController: NavController) {
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

    // Ringtone Picker Activity Launcher
    val ringtonePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val uri = result.data?.getParcelableExtra<android.net.Uri>(android.media.RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            uri?.let {
                triggerHaptic()
                ReminderSettingsManager.setDefaultAlarmSound(context, it.toString())
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("reminder_settings_screen_root"),
        topBar = {
            // Safe Area Aware M3 Header
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
                        modifier = Modifier.testTag("reminder_settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.reminder_settings_back_desc),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.reminder_settings_title),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 22.sp
                            )
                        )
                        Text(
                            text = stringResource(R.string.settings_section_reminders_subtitle),
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
                // 1. General Reminder Options Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        // Default Reminder Switch
                        ReminderSwitchSettingItem(
                            title = stringResource(R.string.reminder_default_title),
                            subtitle = stringResource(R.string.reminder_default_desc),
                            icon = Icons.Default.NotificationsActive,
                            checked = ReminderSettingsManager.reminderByDefault,
                            testTag = "reminder_by_default_switch",
                            onCheckedChange = {
                                triggerHaptic()
                                ReminderSettingsManager.setReminderByDefault(context, it)
                            }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                        // Default Alarm Ringtone Picker Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    triggerHaptic()
                                    val intent = android.content.Intent(android.media.RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                                        putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_TYPE, android.media.RingtoneManager.TYPE_ALARM)
                                        putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_TITLE, context.getString(R.string.default_alarm_sound_title))
                                        putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ReminderSettingsManager.defaultAlarmSound.let { if (it.isBlank()) null else android.net.Uri.parse(it) })
                                        putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                                        putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                                    }
                                    ringtonePickerLauncher.launch(intent)
                                }
                                .padding(vertical = 16.dp)
                                .testTag("default_ringtone_selector_row"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
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
                                        imageVector = Icons.Default.Audiotrack,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.default_alarm_sound_title),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = getRingtoneName(context, ReminderSettingsManager.defaultAlarmSound),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                // 2. Default Snooze Duration Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("snooze_duration_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
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
                                    imageVector = Icons.Default.Snooze,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.default_snooze_duration_title),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 16.sp
                                    )
                                )
                                Text(
                                    text = stringResource(R.string.default_snooze_duration_desc),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Durations (5, 10, 15, 30 minutes)
                        ReminderRadioOption(
                            label = stringResource(R.string.snooze_5_min),
                            selected = ReminderSettingsManager.defaultSnoozeDuration == 5,
                            testTag = "snooze_duration_5",
                            onClick = {
                                triggerHaptic()
                                ReminderSettingsManager.setDefaultSnoozeDuration(context, 5)
                            }
                        )
                        ReminderRadioOption(
                            label = stringResource(R.string.snooze_10_min),
                            selected = ReminderSettingsManager.defaultSnoozeDuration == 10,
                            testTag = "snooze_duration_10",
                            onClick = {
                                triggerHaptic()
                                ReminderSettingsManager.setDefaultSnoozeDuration(context, 10)
                            }
                        )
                        ReminderRadioOption(
                            label = stringResource(R.string.snooze_15_min),
                            selected = ReminderSettingsManager.defaultSnoozeDuration == 15,
                            testTag = "snooze_duration_15",
                            onClick = {
                                triggerHaptic()
                                ReminderSettingsManager.setDefaultSnoozeDuration(context, 15)
                            }
                        )
                        ReminderRadioOption(
                            label = stringResource(R.string.snooze_30_min),
                            selected = ReminderSettingsManager.defaultSnoozeDuration == 30,
                            testTag = "snooze_duration_30",
                            onClick = {
                                triggerHaptic()
                                ReminderSettingsManager.setDefaultSnoozeDuration(context, 30)
                            }
                        )
                    }
                }

                // 3. Maximum Snooze Count Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("max_snooze_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
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
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.max_snooze_count_title),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 16.sp
                                    )
                                )
                                Text(
                                    text = stringResource(R.string.max_snooze_count_desc),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Snooze Counts (-1: Unlimited, 1, 3, 5)
                        ReminderRadioOption(
                            label = stringResource(R.string.snooze_unlimited),
                            selected = ReminderSettingsManager.maxSnoozeCount == -1,
                            testTag = "snooze_count_unlimited",
                            onClick = {
                                triggerHaptic()
                                ReminderSettingsManager.setMaxSnoozeCount(context, -1)
                            }
                        )
                        ReminderRadioOption(
                            label = stringResource(R.string.snooze_count_1),
                            selected = ReminderSettingsManager.maxSnoozeCount == 1,
                            testTag = "snooze_count_1",
                            onClick = {
                                triggerHaptic()
                                ReminderSettingsManager.setMaxSnoozeCount(context, 1)
                            }
                        )
                        ReminderRadioOption(
                            label = stringResource(R.string.snooze_count_3),
                            selected = ReminderSettingsManager.maxSnoozeCount == 3,
                            testTag = "snooze_count_3",
                            onClick = {
                                triggerHaptic()
                                ReminderSettingsManager.setMaxSnoozeCount(context, 3)
                            }
                        )
                        ReminderRadioOption(
                            label = stringResource(R.string.snooze_count_5),
                            selected = ReminderSettingsManager.maxSnoozeCount == 5,
                            testTag = "snooze_count_5",
                            onClick = {
                                triggerHaptic()
                                ReminderSettingsManager.setMaxSnoozeCount(context, 5)
                            }
                        )
                    }
                }

                // 4. Alarm Ring Behavior Options (Vibration, Continuous, Timeout) Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        // Alarm Vibration Switch
                        ReminderSwitchSettingItem(
                            title = stringResource(R.string.alarm_vibration_title),
                            subtitle = stringResource(R.string.alarm_vibration_desc),
                            icon = Icons.Default.Vibration,
                            checked = ReminderSettingsManager.alarmVibration,
                            testTag = "alarm_vibration_switch",
                            onCheckedChange = {
                                triggerHaptic()
                                ReminderSettingsManager.setAlarmVibration(context, it)
                            }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                        // Continuous Alarm Switch
                        ReminderSwitchSettingItem(
                            title = stringResource(R.string.continuous_alarm_title),
                            subtitle = stringResource(R.string.continuous_alarm_desc),
                            icon = Icons.Default.VolumeUp,
                            checked = ReminderSettingsManager.continuousAlarm,
                            testTag = "continuous_alarm_switch",
                            onCheckedChange = {
                                triggerHaptic()
                                ReminderSettingsManager.setContinuousAlarm(context, it)
                            }
                        )

                        // Alarm Timeout Options (Visible only if Continuous Alarm = OFF)
                        AnimatedVisibility(
                            visible = !ReminderSettingsManager.continuousAlarm,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("alarm_timeout_card")
                                    .padding(top = 12.dp)
                            ) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
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
                                            imageVector = Icons.Default.Timer,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = stringResource(R.string.alarm_timeout_title),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 16.sp
                                            )
                                        )
                                        Text(
                                            text = stringResource(R.string.alarm_timeout_desc),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Options (30 seconds, 1 minute, 3 minutes, 5 minutes)
                                ReminderRadioOption(
                                    label = stringResource(R.string.timeout_30_sec),
                                    selected = ReminderSettingsManager.alarmTimeout == 30,
                                    testTag = "timeout_30_sec",
                                    onClick = {
                                        triggerHaptic()
                                        ReminderSettingsManager.setAlarmTimeout(context, 30)
                                    }
                                )
                                ReminderRadioOption(
                                    label = stringResource(R.string.timeout_1_min),
                                    selected = ReminderSettingsManager.alarmTimeout == 60,
                                    testTag = "timeout_1_min",
                                    onClick = {
                                        triggerHaptic()
                                        ReminderSettingsManager.setAlarmTimeout(context, 60)
                                    }
                                )
                                ReminderRadioOption(
                                    label = stringResource(R.string.timeout_3_min),
                                    selected = ReminderSettingsManager.alarmTimeout == 180,
                                    testTag = "timeout_3_min",
                                    onClick = {
                                        triggerHaptic()
                                        ReminderSettingsManager.setAlarmTimeout(context, 180)
                                    }
                                )
                                ReminderRadioOption(
                                    label = stringResource(R.string.timeout_5_min),
                                    selected = ReminderSettingsManager.alarmTimeout == 300,
                                    testTag = "timeout_5_min",
                                    onClick = {
                                        triggerHaptic()
                                        ReminderSettingsManager.setAlarmTimeout(context, 300)
                                    }
                                )
                            }
                        }
                    }
                }

                // 5. Additional Safety & Notification Toggles Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        // Ignore Completed Tasks Switch
                        ReminderSwitchSettingItem(
                            title = stringResource(R.string.ignore_completed_tasks_title),
                            subtitle = stringResource(R.string.ignore_completed_tasks_desc),
                            icon = Icons.Default.CheckCircle,
                            checked = ReminderSettingsManager.ignoreCompletedTasks,
                            testTag = "ignore_completed_switch",
                            onCheckedChange = {
                                triggerHaptic()
                                ReminderSettingsManager.setIgnoreCompletedTasks(context, it)
                            }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                        // Suppress Notification Switch
                        ReminderSwitchSettingItem(
                            title = stringResource(R.string.reminder_notification_title),
                            subtitle = stringResource(R.string.reminder_notification_desc),
                            icon = Icons.Default.Notifications,
                            checked = ReminderSettingsManager.reminderNotification,
                            testTag = "reminder_notification_switch",
                            onCheckedChange = {
                                triggerHaptic()
                                ReminderSettingsManager.setReminderNotification(context, it)
                            }
                        )
                    }
                }

                // 6. Restore Reminder Settings Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reminder_reset_card"),
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
                            text = stringResource(R.string.reminder_restore_defaults_title),
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
                                .testTag("reminder_btn_restore_defaults"),
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
                                    text = stringResource(R.string.reminder_restore_defaults_btn),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                )
                                Text(
                                    text = stringResource(R.string.reminder_restore_defaults_desc),
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

    // Material 3 Restore Confirmation Dialog
    if (showRestoreDialog) {
        AlertDialog(
            modifier = Modifier.testTag("reminder_restore_dialog"),
            onDismissRequest = {
                triggerHaptic()
                showRestoreDialog = false
            },
            title = {
                Text(
                    text = stringResource(R.string.reminder_restore_dialog_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.reminder_restore_dialog_msg),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        triggerHaptic()
                        ReminderSettingsManager.restoreDefaults(context)
                        showRestoreDialog = false
                        Toast.makeText(
                            context,
                            context.getString(R.string.reminder_restore_success_toast),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier.testTag("reminder_restore_dialog_confirm"),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(text = stringResource(R.string.reminder_restore_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        triggerHaptic()
                        showRestoreDialog = false
                    },
                    modifier = Modifier.testTag("reminder_restore_dialog_cancel")
                ) {
                    Text(text = stringResource(R.string.reminder_restore_dialog_cancel))
                }
            }
        )
    }
}

@Composable
fun ReminderRadioOption(
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

@Composable
fun ReminderSwitchSettingItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    testTag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
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
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
