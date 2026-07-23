package com.example.features.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavController) {
  val scale = remember { Animatable(0.85f) }
  val alpha = remember { Animatable(0f) }

  LaunchedEffect(Unit) {
    // Elegant simultaneous Fade + Scale
    launch {
      scale.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000)
      )
    }
    launch {
      alpha.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000)
      )
    }
    delay(2000) // Beautiful automatic navigation after splash
    navController.navigate(Screen.Home.route) {
      popUpTo(Screen.Splash.route) { inclusive = true }
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
          )
        )
      )
      .testTag("splash_screen_root"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.graphicsLayer(
        scaleX = scale.value,
        scaleY = scale.value,
        alpha = alpha.value
      )
    ) {
      // Elegant Series Logo Container
      Box(
        modifier = Modifier
          .size(110.dp)
          .background(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            shape = RoundedCornerShape(24.dp)
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "◈",
          style = MaterialTheme.typography.displayMedium.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 54.sp
          )
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      Text(
        text = stringResource(R.string.splash_title),
        style = MaterialTheme.typography.titleLarge.copy(
          fontSize = 32.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = stringResource(R.string.splash_subtitle),
        style = MaterialTheme.typography.bodyLarge.copy(
          fontSize = 15.sp,
          color = MaterialTheme.colorScheme.secondary
        )
      )

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = stringResource(R.string.splash_welcome),
        style = MaterialTheme.typography.bodyMedium.copy(
          fontSize = 14.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
      )

      Spacer(modifier = Modifier.height(48.dp))

      CircularProgressIndicator(
        modifier = Modifier.size(28.dp),
        color = MaterialTheme.colorScheme.primary,
        strokeWidth = 2.5.dp
      )
    }
  }
}
