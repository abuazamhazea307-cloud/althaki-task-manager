package com.example.features.settings

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.BuildConfig
import com.example.R
import com.example.features.splash.GeometricLogo

@Composable
fun AboutScreen(navController: NavController) {
  val scrollState = rememberScrollState()

  // Dynamically load versionName and versionCode from the generated BuildConfig
  val appVersionName = BuildConfig.VERSION_NAME
  val appVersionCode = BuildConfig.VERSION_CODE.toString()

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .testTag("about_screen_root"),
    topBar = {
      // Elegant top header row
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
            modifier = Modifier.testTag("about_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.about_back_desc),
              tint = MaterialTheme.colorScheme.onBackground
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Column {
            Text(
              text = stringResource(R.string.about_title),
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp
              ),
              modifier = Modifier.testTag("about_screen_title")
            )
            Text(
              text = stringResource(R.string.settings_section_about_subtitle),
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
        // App Logo & Identity section
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
              .testTag("about_logo_container"),
            contentAlignment = Alignment.Center
          ) {
            GeometricLogo(
              modifier = Modifier.size(54.dp),
              color = MaterialTheme.colorScheme.primary
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Brand Name: "الذكي"
          Text(
            text = stringResource(R.string.about_brand_name),
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              letterSpacing = 0.5.sp
            ),
            modifier = Modifier.testTag("about_brand_text")
          )

          Spacer(modifier = Modifier.height(4.dp))

          // App Name Row: "مدير المهام / Task Manager"
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = stringResource(R.string.about_app_name),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
              )
            )
            Text(
              text = " • ",
              style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
              )
            )
            Text(
              text = stringResource(R.string.about_english_name),
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }

        // Description Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("about_desc_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
        ) {
          Text(
            text = stringResource(R.string.about_desc),
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

        // Info List Section
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("about_info_card"),
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
            // Version Row
            InfoRow(
              icon = Icons.Default.Info,
              label = stringResource(R.string.about_version_label),
              value = appVersionName,
              testTag = "about_info_version"
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            // Build Code Row
            InfoRow(
              icon = Icons.Default.Code,
              label = stringResource(R.string.about_build_label),
              value = appVersionCode,
              testTag = "about_info_build"
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            // Developer Row
            InfoRow(
              icon = Icons.Default.Person,
              label = stringResource(R.string.about_developer_title),
              value = stringResource(R.string.about_developer_val),
              testTag = "about_info_developer"
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            // License Row
            InfoRow(
              icon = Icons.Default.VerifiedUser,
              label = stringResource(R.string.about_license_title),
              value = stringResource(R.string.about_license_val),
              testTag = "about_info_license"
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Footer Brand Accent
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "◈ الذكي ◈",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
              letterSpacing = 2.sp
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Althaki Application Series",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
              fontSize = 11.sp
            )
          )
        }
      }
    }
  }
}

@Composable
fun InfoRow(
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
