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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.R
import com.example.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavController) {
  val showAnimations = com.example.features.settings.GeneralSettingsManager.enableAnimations
  val durationType = com.example.features.settings.GeneralSettingsManager.splashDuration

  // Adjust total duration based on Settings selection
  val totalDuration = when (durationType) {
    com.example.features.settings.GeneralSettingsManager.DURATION_SHORT -> 1800L
    com.example.features.settings.GeneralSettingsManager.DURATION_LONG -> 5500L
    else -> 3800L // 3.8s for Normal, to perfectly capture the requested timeline
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

  val welcomeAlpha = remember { Animatable(if (showAnimations) 0f else 1f) }
  val welcomeScale = remember { Animatable(if (showAnimations) 0.85f else 1f) }

  LaunchedEffect(Unit) {
    if (showAnimations) {
      val factor = totalDuration.toFloat() / 3800f

      // 0.0s to 1.0s: Sky blue background only
      delay((1000 * factor).toLong())
      launch {
        logoAlpha.animateTo(1f, animationSpec = tween(durationMillis = (500 * factor).toInt(), easing = FastOutSlowInEasing))
      }
      launch {
        logoScale.animateTo(1f, animationSpec = tween(durationMillis = (500 * factor).toInt(), easing = FastOutSlowInEasing))
      }

      // 1.5s: "الذكي" appears
      delay((500 * factor).toLong())
      launch {
        brandAlpha.animateTo(1f, animationSpec = tween(durationMillis = (500 * factor).toInt(), easing = FastOutSlowInEasing))
      }
      launch {
        brandScale.animateTo(1f, animationSpec = tween(durationMillis = (500 * factor).toInt(), easing = FastOutSlowInEasing))
      }

      // 2.1s: "مدير" appears
      delay((600 * factor).toLong())
      launch {
        word1Alpha.animateTo(1f, animationSpec = tween(durationMillis = (500 * factor).toInt(), easing = FastOutSlowInEasing))
      }
      launch {
        word1Scale.animateTo(1f, animationSpec = tween(durationMillis = (500 * factor).toInt(), easing = FastOutSlowInEasing))
      }

      // 2.7s: "المهام" appears
      delay((600 * factor).toLong())
      launch {
        word2Alpha.animateTo(1f, animationSpec = tween(durationMillis = (500 * factor).toInt(), easing = FastOutSlowInEasing))
      }
      launch {
        word2Scale.animateTo(1f, animationSpec = tween(durationMillis = (500 * factor).toInt(), easing = FastOutSlowInEasing))
      }

      // 3.2s: Welcome text fades in
      delay((500 * factor).toLong())
      launch {
        welcomeAlpha.animateTo(1f, animationSpec = tween(durationMillis = (600 * factor).toInt(), easing = FastOutSlowInEasing))
      }
      launch {
        welcomeScale.animateTo(1f, animationSpec = tween(durationMillis = (600 * factor).toInt(), easing = FastOutSlowInEasing))
      }

      // End
      delay((600 * factor).toLong())
    } else {
      delay(totalDuration)
    }

    navController.navigate(Screen.Home.route) {
      popUpTo(Screen.Splash.route) { inclusive = true }
    }
  }

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = Color(0xFF0EA5E9)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              Color(0xFF38BDF8),
              Color(0xFF0EA5E9)
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
            .size(130.dp)
            .graphicsLayer(
              scaleX = logoScale.value,
              scaleY = logoScale.value,
              alpha = logoAlpha.value
            ),
          contentAlignment = Alignment.Center
        ) {
          GeometricLogo(
            modifier = Modifier.size(110.dp),
            color = Color.White
          )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // "الذكي" Main Title
        Text(
          text = stringResource(R.string.splash_title),
          style = MaterialTheme.typography.displayMedium.copy(
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
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
              fontSize = 22.sp,
              fontWeight = FontWeight.Medium,
              color = Color.White.copy(alpha = 0.9f)
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
              fontSize = 22.sp,
              fontWeight = FontWeight.Medium,
              color = Color.White.copy(alpha = 0.9f)
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

        Spacer(modifier = Modifier.height(40.dp))

        // Welcome text fading in at 3.2s
        Text(
          text = stringResource(R.string.splash_welcome),
          style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.85f),
            letterSpacing = 0.5.sp
          ),
          modifier = Modifier
            .graphicsLayer(
              scaleX = welcomeScale.value,
              scaleY = welcomeScale.value,
              alpha = welcomeAlpha.value
            )
            .testTag("splash_welcome_text")
        )
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

    // Scale factors to map 108dp design coordinates to local canvas pixels
    val scaleX = width / 108f
    val scaleY = height / 108f

    // 1. Outer Large White Circle (Radius 30)
    drawCircle(
      color = color,
      radius = 30f * scaleX,
      center = androidx.compose.ui.geometry.Offset(54f * scaleX, 54f * scaleY),
      style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.0f * scaleX)
    )

    // 2. Thin Inner Circular Ring (Radius 26)
    drawCircle(
      color = color.copy(alpha = 0.7f),
      radius = 26f * scaleX,
      center = androidx.compose.ui.geometry.Offset(54f * scaleX, 54f * scaleY),
      style = androidx.compose.ui.graphics.drawscope.Stroke(width = 0.8f * scaleX)
    )

    // 3. Centered Geometric Althaki Diamond Logo (Scaled and shifted slightly upwards at centerY = 46)
    val outerDiamond = androidx.compose.ui.graphics.Path().apply {
      moveTo(54f * scaleX, 35f * scaleY)
      lineTo(65f * scaleX, 46f * scaleY)
      lineTo(54f * scaleX, 57f * scaleY)
      lineTo(43f * scaleX, 46f * scaleY)
      close()
    }
    drawPath(
      path = outerDiamond,
      color = color,
      style = androidx.compose.ui.graphics.drawscope.Stroke(
        width = 2.0f * scaleX,
        cap = androidx.compose.ui.graphics.StrokeCap.Round,
        join = androidx.compose.ui.graphics.StrokeJoin.Round
      )
    )

    val innerDiamond = androidx.compose.ui.graphics.Path().apply {
      moveTo(54f * scaleX, 40.5f * scaleY)
      lineTo(59.5f * scaleX, 46f * scaleY)
      lineTo(54f * scaleX, 51.5f * scaleY)
      lineTo(48.5f * scaleX, 46f * scaleY)
      close()
    }
    drawPath(
      path = innerDiamond,
      color = color.copy(alpha = 0.7f),
      style = androidx.compose.ui.graphics.drawscope.Stroke(
        width = 1.2f * scaleX,
        cap = androidx.compose.ui.graphics.StrokeCap.Round,
        join = androidx.compose.ui.graphics.StrokeJoin.Round
      )
    )

    val coreDiamond = androidx.compose.ui.graphics.Path().apply {
      moveTo(54f * scaleX, 44f * scaleY)
      lineTo(56f * scaleX, 46f * scaleY)
      lineTo(54f * scaleX, 48f * scaleY)
      lineTo(52f * scaleX, 46f * scaleY)
      close()
    }
    drawPath(
      path = coreDiamond,
      color = color,
      style = androidx.compose.ui.graphics.drawscope.Fill
    )

    // 4. Task Manager Checklist Symbol (Centered below the diamond around Y = 66)
    // Row 1: Checkmark & Line
    val check1 = androidx.compose.ui.graphics.Path().apply {
      moveTo(45f * scaleX, 62.5f * scaleY)
      lineTo(47f * scaleX, 64.5f * scaleY)
      lineTo(51f * scaleX, 60.5f * scaleY)
    }
    drawPath(
      path = check1,
      color = color,
      style = androidx.compose.ui.graphics.drawscope.Stroke(
        width = 1.6f * scaleX,
        cap = androidx.compose.ui.graphics.StrokeCap.Round,
        join = androidx.compose.ui.graphics.StrokeJoin.Round
      )
    )

    drawLine(
      color = color,
      start = androidx.compose.ui.geometry.Offset(55f * scaleX, 62.5f * scaleY),
      end = androidx.compose.ui.geometry.Offset(63f * scaleX, 62.5f * scaleY),
      strokeWidth = 1.6f * scaleX,
      cap = androidx.compose.ui.graphics.StrokeCap.Round
    )

    // Row 2: Checkmark & Line
    val check2 = androidx.compose.ui.graphics.Path().apply {
      moveTo(45f * scaleX, 69.5f * scaleY)
      lineTo(47f * scaleX, 71.5f * scaleY)
      lineTo(51f * scaleX, 67.5f * scaleY)
    }
    drawPath(
      path = check2,
      color = color,
      style = androidx.compose.ui.graphics.drawscope.Stroke(
        width = 1.6f * scaleX,
        cap = androidx.compose.ui.graphics.StrokeCap.Round,
        join = androidx.compose.ui.graphics.StrokeJoin.Round
      )
    )

    drawLine(
      color = color,
      start = androidx.compose.ui.geometry.Offset(55f * scaleX, 69.5f * scaleY),
      end = androidx.compose.ui.geometry.Offset(63f * scaleX, 69.5f * scaleY),
      strokeWidth = 1.6f * scaleX,
      cap = androidx.compose.ui.graphics.StrokeCap.Round
    )
  }
}
