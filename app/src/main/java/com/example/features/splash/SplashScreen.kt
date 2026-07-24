package com.example.features.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavController) {
  val showAnimations = com.example.features.settings.GeneralSettingsManager.enableAnimations
  val durationType = com.example.features.settings.GeneralSettingsManager.splashDuration

  val totalDuration = when (durationType) {
    com.example.features.settings.GeneralSettingsManager.DURATION_SHORT -> 1500L
    com.example.features.settings.GeneralSettingsManager.DURATION_LONG -> 5000L
    else -> 3000L
  }

  // Animatables for precise timeline triggers
  val logoAlpha = remember { Animatable(if (showAnimations) 0f else 1f) }
  val logoScale = remember { Animatable(if (showAnimations) 0.8f else 1f) }

  val brandAlpha = remember { Animatable(if (showAnimations) 0f else 1f) }
  val brandScale = remember { Animatable(if (showAnimations) 0.85f else 1f) }

  val word1Alpha = remember { Animatable(if (showAnimations) 0f else 1f) }
  val word1Scale = remember { Animatable(if (showAnimations) 0.85f else 1f) }

  val word2Alpha = remember { Animatable(if (showAnimations) 0f else 1f) }
  val word2Scale = remember { Animatable(if (showAnimations) 0.85f else 1f) }

  LaunchedEffect(Unit) {
    if (showAnimations) {
      val factor = totalDuration.toFloat() / 3000f

      // 0.4s * factor
      delay((400 * factor).toLong())
      launch {
        logoAlpha.animateTo(1f, animationSpec = tween(durationMillis = (600 * factor).toInt(), easing = FastOutSlowInEasing))
      }
      launch {
        logoScale.animateTo(1f, animationSpec = tween(durationMillis = (600 * factor).toInt(), easing = FastOutSlowInEasing))
      }

      // 0.9s * factor
      delay((500 * factor).toLong())
      launch {
        brandAlpha.animateTo(1f, animationSpec = tween(durationMillis = (500 * factor).toInt(), easing = FastOutSlowInEasing))
      }
      launch {
        brandScale.animateTo(1f, animationSpec = tween(durationMillis = (500 * factor).toInt(), easing = FastOutSlowInEasing))
      }

      // 1.6s * factor
      delay((700 * factor).toLong())
      launch {
        word1Alpha.animateTo(1f, animationSpec = tween(durationMillis = (500 * factor).toInt(), easing = FastOutSlowInEasing))
      }
      launch {
        word1Scale.animateTo(1f, animationSpec = tween(durationMillis = (500 * factor).toInt(), easing = FastOutSlowInEasing))
      }

      // 2.2s * factor
      delay((600 * factor).toLong())
      launch {
        word2Alpha.animateTo(1f, animationSpec = tween(durationMillis = (500 * factor).toInt(), easing = FastOutSlowInEasing))
      }
      launch {
        word2Scale.animateTo(1f, animationSpec = tween(durationMillis = (500 * factor).toInt(), easing = FastOutSlowInEasing))
      }

      // 3.0s * factor
      delay((800 * factor).toLong())
    } else {
      delay(totalDuration)
    }

    navController.navigate(Screen.Home.route) {
      popUpTo(Screen.Splash.route) { inclusive = true }
    }
  }

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              MaterialTheme.colorScheme.background,
              MaterialTheme.colorScheme.primary.copy(alpha = 0.04f),
              MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            )
          )
        )
        .testTag("splash_screen_root"),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
      ) {
        // Geometric Logo Container with premium styling
        Box(
          modifier = Modifier
            .size(110.dp)
            .graphicsLayer(
              scaleX = logoScale.value,
              scaleY = logoScale.value,
              alpha = logoAlpha.value
            )
            .background(
              color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
              shape = RoundedCornerShape(28.dp)
            ),
          contentAlignment = Alignment.Center
        ) {
          GeometricLogo(
            modifier = Modifier.size(60.dp),
            color = MaterialTheme.colorScheme.primary
          )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // "الذكي" Main Title
        Text(
          text = "الذكي",
          style = MaterialTheme.typography.displayMedium.copy(
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          ),
          modifier = Modifier
            .graphicsLayer(
              scaleX = brandScale.value,
              scaleY = brandScale.value,
              alpha = brandAlpha.value
            )
            .testTag("splash_brand_title")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // "مدير المهام" Subtitle words fading in sequentially
        Row(
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "مدير",
            style = MaterialTheme.typography.titleLarge.copy(
              fontSize = 20.sp,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier
              .graphicsLayer(
                scaleX = word1Scale.value,
                scaleY = word1Scale.value,
                alpha = word1Alpha.value
              )
              .testTag("splash_word_1")
          )

          Spacer(modifier = Modifier.width(8.dp))

          Text(
            text = "المهام",
            style = MaterialTheme.typography.titleLarge.copy(
              fontSize = 20.sp,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier
              .graphicsLayer(
                scaleX = word2Scale.value,
                scaleY = word2Scale.value,
                alpha = word2Alpha.value
              )
              .testTag("splash_word_2")
          )
        }
      }
    }
  }
}

@Composable
fun GeometricLogo(
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.primary
) {
  androidx.compose.foundation.Canvas(modifier = modifier) {
    val width = size.width
    val height = size.height
    val centerX = width / 2f
    val centerY = height / 2f

    // Outer diamond size (relative to width)
    val outerRadius = width * 0.44f
    val outerPath = androidx.compose.ui.graphics.Path().apply {
      moveTo(centerX, centerY - outerRadius)
      lineTo(centerX + outerRadius, centerY)
      lineTo(centerX, centerY + outerRadius)
      lineTo(centerX - outerRadius, centerY)
      close()
    }

    // Inner diamond size
    val innerRadius = width * 0.22f
    val innerPath = androidx.compose.ui.graphics.Path().apply {
      moveTo(centerX, centerY - innerRadius)
      lineTo(centerX + innerRadius, centerY)
      lineTo(centerX, centerY + innerRadius)
      lineTo(centerX - innerRadius, centerY)
      close()
    }

    // Center dot size
    val coreRadius = width * 0.08f
    val corePath = androidx.compose.ui.graphics.Path().apply {
      moveTo(centerX, centerY - coreRadius)
      lineTo(centerX + coreRadius, centerY)
      lineTo(centerX, centerY + coreRadius)
      lineTo(centerX - coreRadius, centerY)
      close()
    }

    // Draw outer diamond outline
    val strokeWidth = width * 0.07f
    drawPath(
      path = outerPath,
      color = color,
      style = androidx.compose.ui.graphics.drawscope.Stroke(
        width = strokeWidth,
        miter = 4f,
        cap = androidx.compose.ui.graphics.StrokeCap.Round,
        join = androidx.compose.ui.graphics.StrokeJoin.Round
      )
    )

    // Draw inner diamond outline
    drawPath(
      path = innerPath,
      color = color.copy(alpha = 0.85f),
      style = androidx.compose.ui.graphics.drawscope.Stroke(
        width = strokeWidth * 0.7f,
        miter = 4f,
        cap = androidx.compose.ui.graphics.StrokeCap.Round,
        join = androidx.compose.ui.graphics.StrokeJoin.Round
      )
    )

    // Draw core filled diamond
    drawPath(
      path = corePath,
      color = color,
      style = androidx.compose.ui.graphics.drawscope.Fill
    )
  }
}
