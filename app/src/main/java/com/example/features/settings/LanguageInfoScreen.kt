package com.example.features.settings

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.R
import java.util.Locale

@Composable
fun LanguageInfoScreen(navController: NavController) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  // Get current active device locale dynamically
  val currentLocale = Locale.getDefault()
  val localizedLanguageName = remember(currentLocale) {
    currentLocale.getDisplayName(currentLocale).replaceFirstChar {
      if (it.isLowerCase()) it.titlecase(currentLocale) else it.toString()
    }
  }

  // Check if system language settings intent can be resolved
  val settingsIntent = remember {
    Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS)
  }
  val isSettingsIntentSupported = remember {
    try {
      val resolveInfo = context.packageManager.queryIntentActivities(settingsIntent, 0)
      resolveInfo.isNotEmpty()
    } catch (e: Exception) {
      false
    }
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .testTag("language_screen_root"),
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
            modifier = Modifier.testTag("language_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.language_back_desc),
              tint = MaterialTheme.colorScheme.onBackground
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Column {
            Text(
              text = stringResource(R.string.language_title),
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp
              ),
              modifier = Modifier.testTag("language_screen_title")
            )
            Text(
              text = stringResource(R.string.settings_section_language_subtitle),
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
      ) {
        // Language Header Section
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(vertical = 16.dp)
        ) {
          Box(
            modifier = Modifier
              .size(100.dp)
              .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                shape = RoundedCornerShape(24.dp)
              )
              .testTag("language_icon_container"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Language,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(54.dp)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = stringResource(R.string.language_info_title),
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              letterSpacing = 0.5.sp
            )
          )
        }

        // Informational M3 Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("language_info_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
        ) {
          Text(
            text = stringResource(R.string.language_info_body),
            style = MaterialTheme.typography.bodyMedium.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 22.sp,
              textAlign = TextAlign.Center
            ),
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp)
          )
        }

        // Current Language Section Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("language_details_card"),
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
            // Title
            Text(
              text = stringResource(R.string.language_section_title),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
              ),
              modifier = Modifier.padding(vertical = 12.dp)
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            // Active Language Row
            LanguageDetailRow(
              icon = Icons.Default.Language,
              label = stringResource(R.string.language_current_label),
              value = localizedLanguageName,
              testTag = "language_row_current"
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            // Source Row
            LanguageDetailRow(
              icon = Icons.Default.Sync,
              label = stringResource(R.string.language_source_label),
              value = stringResource(R.string.language_source_value),
              testTag = "language_row_source"
            )
          }
        }

        // Open Device Language Settings Button
        if (isSettingsIntentSupported) {
          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = {
              try {
                val intent = Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS).apply {
                  addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
              } catch (e: Exception) {
                Toast.makeText(context, "Unable to open language settings", Toast.LENGTH_SHORT).show()
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(56.dp)
              .testTag("language_open_settings_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary
            )
          ) {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = null,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = stringResource(R.string.language_settings_btn),
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
fun LanguageDetailRow(
  icon: ImageVector,
  label: String,
  value: String,
  testTag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 16.dp)
      .testTag(testTag),
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
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp)
        )
      }
      Spacer(modifier = Modifier.width(16.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
      )
    }

    Spacer(modifier = Modifier.width(8.dp))

    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Normal
      ),
      textAlign = TextAlign.End
    )
  }
}
