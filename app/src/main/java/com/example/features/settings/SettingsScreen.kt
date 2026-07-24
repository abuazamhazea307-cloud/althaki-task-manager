package com.example.features.settings

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.R
import com.example.navigation.Screen
import com.example.ui.theme.ThemeManager

@Composable
fun SettingsScreen(navController: NavController) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  var showThemeDialog by remember { mutableStateOf(false) }

  val activeThemeLabel = when (ThemeManager.currentThemeMode) {
    ThemeManager.MODE_LIGHT -> stringResource(R.string.theme_option_light)
    ThemeManager.MODE_DARK -> stringResource(R.string.theme_option_dark)
    else -> stringResource(R.string.theme_option_system)
  }

  if (showThemeDialog) {
    AlertDialog(
      onDismissRequest = { showThemeDialog = false },
      title = {
        Text(
          text = stringResource(R.string.theme_dialog_title),
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        )
      },
      text = {
        Column(
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          ThemeOptionRow(
            text = stringResource(R.string.theme_option_system),
            isSelected = ThemeManager.currentThemeMode == ThemeManager.MODE_SYSTEM,
            onClick = {
              ThemeManager.setThemeMode(context, ThemeManager.MODE_SYSTEM)
              showThemeDialog = false
            }
          )
          ThemeOptionRow(
            text = stringResource(R.string.theme_option_light),
            isSelected = ThemeManager.currentThemeMode == ThemeManager.MODE_LIGHT,
            onClick = {
              ThemeManager.setThemeMode(context, ThemeManager.MODE_LIGHT)
              showThemeDialog = false
            }
          )
          ThemeOptionRow(
            text = stringResource(R.string.theme_option_dark),
            isSelected = ThemeManager.currentThemeMode == ThemeManager.MODE_DARK,
            onClick = {
              ThemeManager.setThemeMode(context, ThemeManager.MODE_DARK)
              showThemeDialog = false
            }
          )
        }
      },
      confirmButton = {},
      dismissButton = {
        TextButton(onClick = { showThemeDialog = false }) {
          Text(text = stringResource(R.string.bottom_sheet_cancel))
        }
      },
      shape = RoundedCornerShape(24.dp),
      containerColor = MaterialTheme.colorScheme.surface
    )
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .testTag("settings_screen_root"),
    topBar = {
      // Elegant Custom M3 Header Row (Notch & Safe Area Aware)
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
            onClick = { navController.popBackStack() },
            modifier = Modifier.testTag("settings_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.settings_back_desc),
              tint = MaterialTheme.colorScheme.onBackground
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Column {
            Text(
              text = stringResource(R.string.settings_title),
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp
              ),
              modifier = Modifier.testTag("settings_screen_title")
            )
            Text(
              text = stringResource(R.string.settings_desc),
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
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(paddingValues)
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Decorative top spacer
      Spacer(modifier = Modifier.height(4.dp))

      // Reusable Settings List Section Cards
      SettingsItemCard(
        title = stringResource(R.string.settings_section_general_title),
        subtitle = stringResource(R.string.settings_section_general_subtitle),
        icon = Icons.Default.Settings,
        testTag = "settings_item_general",
        onClick = {
          Toast.makeText(context, "${context.getString(R.string.settings_section_general_title)}: ${context.getString(R.string.welcome_subtitle)}", Toast.LENGTH_SHORT).show()
        }
      )

      SettingsItemCard(
        title = stringResource(R.string.settings_section_tasks_title),
        subtitle = stringResource(R.string.settings_section_tasks_subtitle),
        icon = Icons.Default.CheckCircle,
        testTag = "settings_item_tasks",
        onClick = {
          Toast.makeText(context, "${context.getString(R.string.settings_section_tasks_title)}: ${context.getString(R.string.welcome_subtitle)}", Toast.LENGTH_SHORT).show()
        }
      )

      SettingsItemCard(
        title = stringResource(R.string.settings_section_reminders_title),
        subtitle = stringResource(R.string.settings_section_reminders_subtitle),
        icon = Icons.Default.Notifications,
        testTag = "settings_item_reminders",
        onClick = {
          Toast.makeText(context, "${context.getString(R.string.settings_section_reminders_title)}: ${context.getString(R.string.welcome_subtitle)}", Toast.LENGTH_SHORT).show()
        }
      )

      SettingsItemCard(
        title = stringResource(R.string.settings_section_appearance_title),
        subtitle = "${stringResource(R.string.settings_section_appearance_subtitle)} ($activeThemeLabel)",
        icon = Icons.Default.Palette,
        testTag = "settings_item_appearance",
        onClick = {
          showThemeDialog = true
        }
      )

      SettingsItemCard(
        title = stringResource(R.string.settings_section_language_title),
        subtitle = stringResource(R.string.settings_section_language_subtitle),
        icon = Icons.Default.Language,
        testTag = "settings_item_language",
        onClick = {
          Toast.makeText(context, "${context.getString(R.string.settings_section_language_title)}: ${context.getString(R.string.welcome_subtitle)}", Toast.LENGTH_SHORT).show()
        }
      )

      SettingsItemCard(
        title = stringResource(R.string.settings_section_about_title),
        subtitle = stringResource(R.string.settings_section_about_subtitle),
        icon = Icons.Default.Info,
        testTag = "settings_item_about",
        onClick = {
          navController.navigate(Screen.About.route)
        }
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Footer brand version info
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "◈ " + stringResource(R.string.series_title).replace("◈", "").trim(),
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            fontSize = 16.sp
          )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Beta 0.2",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            fontSize = 12.sp
          )
        )
      }
    }
  }
}

@Composable
fun SettingsItemCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  testTag: String,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .testTag(testTag),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        // Icon circular frame
        Box(
          modifier = Modifier
            .size(42.dp)
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
            modifier = Modifier.size(22.dp)
          )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
              fontSize = 16.sp
            )
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 12.sp,
              lineHeight = 16.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Trailing elegant chevron
      Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.size(24.dp)
      )
    }
  }
}

@Composable
fun ThemeOptionRow(
  text: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(vertical = 12.dp, horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.bodyLarge.copy(
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
      )
    )
    RadioButton(
      selected = isSelected,
      onClick = onClick
    )
  }
}
