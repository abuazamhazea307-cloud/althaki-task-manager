package com.example.features.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.R
import com.example.features.settings.GeneralSettingsManager
import com.example.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavController) {
  val showAnimations = GeneralSettingsManager.enableAnimations
  val durationType = GeneralSettingsManager.splashDuration

  // Resolve the total display duration purely from the settings selection
  val welcomeDuration = when (durationType) {
    GeneralSettingsManager.DURATION_SHORT -> 1500L
    GeneralSettingsManager.DURATION_NORMAL -> 3000L
    GeneralSettingsManager.DURATION_LONG -> 5000L
    else -> 4000L // Default (Recommended)
  }

  // All 5 identity elements are present in the composition from the very first frame.
  // They start with Alpha = 0f and Scale = 0.92f to ensure smooth transition without blank flashes.
  val diamondAlpha = remember { Animatable(0f) }
  val diamondScale = remember { Animatable(0.92f) }

  val titleAlpha = remember { Animatable(0f) }
  val titleScale = remember { Animatable(0.92f) }

  val taskLogoAlpha = remember { Animatable(0f) }
  val taskLogoScale = remember { Animatable(0.92f) }

  val subtitleAlpha = remember { Animatable(0f) }
  val subtitleScale = remember { Animatable(0.92f) }

  val welcomeAlpha = remember { Animatable(0f) }
  val welcomeScale = remember { Animatable(0.92f) }

  LaunchedEffect(Unit) {
    if (showAnimations) {
      // Calculate smooth dynamic transition timing based on welcomeDuration
      val stepDuration = (welcomeDuration * 0.20f).coerceIn(350f, 900f).toInt()
      val stepDelay = (welcomeDuration * 0.08f).coerceIn(100f, 350f).toLong()

      // 1. 💎 (Althaki Diamond Logo) starts instantly
      launch {
        diamondAlpha.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = stepDuration, easing = FastOutSlowInEasing)
        )
      }
      launch {
        diamondScale.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = stepDuration, easing = FastOutSlowInEasing)
        )
      }

      // 2. "الذكي" Brand Title
      delay(stepDelay)
      launch {
        titleAlpha.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = stepDuration, easing = FastOutSlowInEasing)
        )
      }
      launch {
        titleScale.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = stepDuration, easing = FastOutSlowInEasing)
        )
      }

      // 3. 📝 (Custom Task Manager Logo)
      delay(stepDelay)
      launch {
        taskLogoAlpha.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = stepDuration, easing = FastOutSlowInEasing)
        )
      }
      launch {
        taskLogoScale.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = stepDuration, easing = FastOutSlowInEasing)
        )
      }

      // 4. "مدير المهام" Subtitle
      delay(stepDelay)
      launch {
        subtitleAlpha.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = stepDuration, easing = FastOutSlowInEasing)
        )
      }
      launch {
        subtitleScale.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = stepDuration, easing = FastOutSlowInEasing)
        )
      }

      // 5. "مرحبًا بك 🌹" Welcome Message at the bottom
      delay(stepDelay)
      launch {
        welcomeAlpha.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = stepDuration, easing = FastOutSlowInEasing)
        )
      }
      launch {
        welcomeScale.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = stepDuration, easing = FastOutSlowInEasing)
        )
      }

      // Wait out the remainder of the total splash screen duration
      val totalSpent = (stepDelay * 4) + stepDuration
      val remaining = (welcomeDuration - totalSpent).coerceAtLeast(0L)
      delay(remaining)
    } else {
      // Direct snap for immediate display if animations are disabled
      diamondAlpha.snapTo(1f)
      diamondScale.snapTo(1f)
      titleAlpha.snapTo(1f)
      titleScale.snapTo(1f)
      taskLogoAlpha.snapTo(1f)
      taskLogoScale.snapTo(1f)
      subtitleAlpha.snapTo(1f)
      subtitleScale.snapTo(1f)
      welcomeAlpha.snapTo(1f)
      welcomeScale.snapTo(1f)

      delay(welcomeDuration)
    }

    // Direct single-top navigation to the Home screen
    navController.navigate(Screen.Home.route) {
      launchSingleTop = true
      popUpTo(Screen.Splash.route) { inclusive = true }
    }
  }

  // Pure sky blue canvas filling the entire screen perfectly from the very first frame
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0EA5E9))
  ) {
    // Center Column hosting the core brand identity stack
    Column(
      modifier = Modifier
        .align(Alignment.Center)
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // 1. 💎 (Althaki Diamond Logo)
      AlthakiDiamondLogo(
        modifier = Modifier
          .size(100.dp)
          .graphicsLayer(
            alpha = diamondAlpha.value,
            scaleX = diamondScale.value,
            scaleY = diamondScale.value
          )
          .testTag("splash_diamond_logo")
      )

      Spacer(modifier = Modifier.height(24.dp))

      // 2. "الذكي" Brand Title
      Text(
        text = stringResource(R.string.splash_title),
        style = MaterialTheme.typography.displayMedium.copy(
          fontSize = 38.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          letterSpacing = 0.5.sp
        ),
        modifier = Modifier
          .graphicsLayer(
            alpha = titleAlpha.value,
            scaleX = titleScale.value,
            scaleY = titleScale.value
          )
          .testTag("splash_brand_title")
      )

      Spacer(modifier = Modifier.height(44.dp))

      // 3. 📝 (Custom Task Manager Logo)
      Box(
        modifier = Modifier
          .size(72.dp)
          .graphicsLayer(
            alpha = taskLogoAlpha.value,
            scaleX = taskLogoScale.value,
            scaleY = taskLogoScale.value
          ),
        contentAlignment = Alignment.Center
      ) {
        TaskManagerCustomLogo(
          modifier = Modifier.size(54.dp),
          color = Color.White
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 4. "مدير المهام" Subtitle
      Text(
        text = stringResource(R.string.splash_subtitle),
        style = MaterialTheme.typography.titleLarge.copy(
          fontSize = 22.sp,
          fontWeight = FontWeight.Medium,
          color = Color.White.copy(alpha = 0.9f),
          letterSpacing = 0.5.sp
        ),
        modifier = Modifier
          .graphicsLayer(
            alpha = subtitleAlpha.value,
            scaleX = subtitleScale.value,
            scaleY = subtitleScale.value
          )
          .testTag("splash_subtitle")
      )
    }

    // 5. "مرحبًا بك 🌹" Welcome message aligned at the bottom
    Text(
      text = stringResource(R.string.splash_welcome) + " 🌹",
      style = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        color = Color.White.copy(alpha = 0.85f),
        letterSpacing = 0.5.sp
      ),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 60.dp)
        .graphicsLayer(
          alpha = welcomeAlpha.value,
          scaleX = welcomeScale.value,
          scaleY = welcomeScale.value
        )
        .testTag("splash_welcome_text")
    )
  }
}

@Composable
fun AlthakiDiamondLogo(
  modifier: Modifier = Modifier,
  color: Color = Color.White
) {
  androidx.compose.foundation.Canvas(modifier = modifier) {
    val width = size.width
    val height = size.height
    val scaleX = width / 108f
    val scaleY = height / 108f

    // 1. Outer Crystal Outline
    val outerPath = androidx.compose.ui.graphics.Path().apply {
      moveTo(45f * scaleX, 44f * scaleY)
      lineTo(63f * scaleX, 44f * scaleY)
      lineTo(68f * scaleX, 54f * scaleY)
      lineTo(54f * scaleX, 68f * scaleY)
      lineTo(40f * scaleX, 54f * scaleY)
      close()
    }
    drawPath(
      path = outerPath,
      color = color,
      style = androidx.compose.ui.graphics.drawscope.Stroke(
        width = 2.0f * scaleX,
        cap = androidx.compose.ui.graphics.StrokeCap.Round,
        join = androidx.compose.ui.graphics.StrokeJoin.Round
      )
    )

    // 2. Inner Crystal Outline
    val innerPath = androidx.compose.ui.graphics.Path().apply {
      moveTo(48f * scaleX, 48f * scaleY)
      lineTo(60f * scaleX, 48f * scaleY)
      lineTo(63f * scaleX, 54f * scaleY)
      lineTo(54f * scaleX, 63f * scaleY)
      lineTo(45f * scaleX, 54f * scaleY)
      close()
    }
    drawPath(
      path = innerPath,
      color = color,
      style = androidx.compose.ui.graphics.drawscope.Stroke(
        width = 1.2f * scaleX,
        cap = androidx.compose.ui.graphics.StrokeCap.Round,
        join = androidx.compose.ui.graphics.StrokeJoin.Round
      )
    )

    // 3. Tiny Center Crystal
    val centerPath = androidx.compose.ui.graphics.Path().apply {
      moveTo(51f * scaleX, 51.5f * scaleY)
      lineTo(57f * scaleX, 51.5f * scaleY)
      lineTo(58.5f * scaleX, 54f * scaleY)
      lineTo(54f * scaleX, 58f * scaleY)
      lineTo(49.5f * scaleX, 54f * scaleY)
      close()
    }
    drawPath(
      path = centerPath,
      color = color,
      style = androidx.compose.ui.graphics.drawscope.Stroke(
        width = 0.8f * scaleX,
        cap = androidx.compose.ui.graphics.StrokeCap.Round,
        join = androidx.compose.ui.graphics.StrokeJoin.Round
      )
    )

    // 4. Facet Lines Outer to Inner
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(45f * scaleX, 44f * scaleY), end = androidx.compose.ui.geometry.Offset(48f * scaleX, 48f * scaleY), strokeWidth = 1.5f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(63f * scaleX, 44f * scaleY), end = androidx.compose.ui.geometry.Offset(60f * scaleX, 48f * scaleY), strokeWidth = 1.5f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(68f * scaleX, 54f * scaleY), end = androidx.compose.ui.geometry.Offset(63f * scaleX, 54f * scaleY), strokeWidth = 1.5f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(54f * scaleX, 68f * scaleY), end = androidx.compose.ui.geometry.Offset(54f * scaleX, 63f * scaleY), strokeWidth = 1.5f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(40f * scaleX, 54f * scaleY), end = androidx.compose.ui.geometry.Offset(45f * scaleX, 54f * scaleY), strokeWidth = 1.5f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)

    // 5. Facet Lines Inner to Center
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(48f * scaleX, 48f * scaleY), end = androidx.compose.ui.geometry.Offset(51f * scaleX, 51.5f * scaleY), strokeWidth = 0.8f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(60f * scaleX, 48f * scaleY), end = androidx.compose.ui.geometry.Offset(57f * scaleX, 51.5f * scaleY), strokeWidth = 0.8f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(63f * scaleX, 54f * scaleY), end = androidx.compose.ui.geometry.Offset(58.5f * scaleX, 54f * scaleY), strokeWidth = 0.8f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(54f * scaleX, 63f * scaleY), end = androidx.compose.ui.geometry.Offset(54f * scaleX, 58f * scaleY), strokeWidth = 0.8f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(45f * scaleX, 54f * scaleY), end = androidx.compose.ui.geometry.Offset(49.5f * scaleX, 54f * scaleY), strokeWidth = 0.8f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
  }
}

@Composable
fun TaskManagerCustomLogo(
  modifier: Modifier = Modifier,
  color: Color = Color.White
) {
  androidx.compose.foundation.Canvas(modifier = modifier) {
    val width = size.width
    val height = size.height
    val scaleX = width / 108f
    val scaleY = height / 108f

    // Paper Outline
    val paperPath = androidx.compose.ui.graphics.Path().apply {
      moveTo(49f * scaleX, 47f * scaleY)
      lineTo(57f * scaleX, 47f * scaleY)
      lineTo(61f * scaleX, 51f * scaleY)
      lineTo(61f * scaleX, 59f * scaleY)
      quadraticTo(61f * scaleX, 61f * scaleY, 59f * scaleX, 61f * scaleY)
      lineTo(49f * scaleX, 61f * scaleY)
      quadraticTo(47f * scaleX, 61f * scaleY, 47f * scaleX, 59f * scaleY)
      lineTo(47f * scaleX, 49f * scaleY)
      quadraticTo(47f * scaleX, 47f * scaleY, 49f * scaleX, 47f * scaleY)
      close()
    }
    drawPath(
      path = paperPath,
      color = color,
      style = androidx.compose.ui.graphics.drawscope.Stroke(
        width = 1.6f * scaleX,
        cap = androidx.compose.ui.graphics.StrokeCap.Round,
        join = androidx.compose.ui.graphics.StrokeJoin.Round
      )
    )

    // Folded Corner Flap
    val flapPath = androidx.compose.ui.graphics.Path().apply {
      moveTo(57f * scaleX, 47f * scaleY)
      lineTo(57f * scaleX, 51f * scaleY)
      lineTo(61f * scaleX, 51f * scaleY)
    }
    drawPath(
      path = flapPath,
      color = color,
      style = androidx.compose.ui.graphics.drawscope.Stroke(
        width = 1.6f * scaleX,
        cap = androidx.compose.ui.graphics.StrokeCap.Round,
        join = androidx.compose.ui.graphics.StrokeJoin.Round
      )
    )

    // Row 1: Line
    drawLine(
      color = color,
      start = androidx.compose.ui.geometry.Offset(50f * scaleX, 53f * scaleY),
      end = androidx.compose.ui.geometry.Offset(58f * scaleX, 53f * scaleY),
      strokeWidth = 1.4f * scaleX,
      cap = androidx.compose.ui.graphics.StrokeCap.Round
    )

    // Row 2: Checkmark
    val checkPath = androidx.compose.ui.graphics.Path().apply {
      moveTo(49.5f * scaleX, 56.5f * scaleY)
      lineTo(51f * scaleX, 58f * scaleY)
      lineTo(53f * scaleX, 55f * scaleY)
    }
    drawPath(
      path = checkPath,
      color = color,
      style = androidx.compose.ui.graphics.drawscope.Stroke(
        width = 1.4f * scaleX,
        cap = androidx.compose.ui.graphics.StrokeCap.Round,
        join = androidx.compose.ui.graphics.StrokeJoin.Round
      )
    )

    // Row 2: Line next to Checkmark
    drawLine(
      color = color,
      start = androidx.compose.ui.geometry.Offset(54.5f * scaleX, 57f * scaleY),
      end = androidx.compose.ui.geometry.Offset(58.5f * scaleX, 57f * scaleY),
      strokeWidth = 1.4f * scaleX,
      cap = androidx.compose.ui.graphics.StrokeCap.Round
    )
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

    // 1. Large Outer Circle (Radius 30)
    drawCircle(
      color = color,
      radius = 30f * scaleX,
      center = androidx.compose.ui.geometry.Offset(54f * scaleX, 54f * scaleY),
      style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f * scaleX)
    )

    // 2. Thin Inner Circular Ring (Radius 26)
    drawCircle(
      color = color,
      radius = 26f * scaleX,
      center = androidx.compose.ui.geometry.Offset(54f * scaleX, 54f * scaleY),
      style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.0f * scaleX)
    )

    // 3. Official Althaki Diamond Logo (centered at Y=44)
    val outerDiamond = androidx.compose.ui.graphics.Path().apply {
      moveTo(45f * scaleX, 34f * scaleY)
      lineTo(63f * scaleX, 34f * scaleY)
      lineTo(68f * scaleX, 44f * scaleY)
      lineTo(54f * scaleX, 58f * scaleY)
      lineTo(40f * scaleX, 44f * scaleY)
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

    // Inner Crystal Outline
    val innerDiamond = androidx.compose.ui.graphics.Path().apply {
      moveTo(48f * scaleX, 38f * scaleY)
      lineTo(60f * scaleX, 38f * scaleY)
      lineTo(63f * scaleX, 44f * scaleY)
      lineTo(54f * scaleX, 53f * scaleY)
      lineTo(45f * scaleX, 44f * scaleY)
      close()
    }
    drawPath(
      path = innerDiamond,
      color = color,
      style = androidx.compose.ui.graphics.drawscope.Stroke(
        width = 1.2f * scaleX,
        cap = androidx.compose.ui.graphics.StrokeCap.Round,
        join = androidx.compose.ui.graphics.StrokeJoin.Round
      )
    )

    // Tiny Center Crystal
    val centerDiamond = androidx.compose.ui.graphics.Path().apply {
      moveTo(51f * scaleX, 41.5f * scaleY)
      lineTo(57f * scaleX, 41.5f * scaleY)
      lineTo(58.5f * scaleX, 44f * scaleY)
      lineTo(54f * scaleX, 48f * scaleY)
      lineTo(49.5f * scaleX, 44f * scaleY)
      close()
    }
    drawPath(
      path = centerDiamond,
      color = color,
      style = androidx.compose.ui.graphics.drawscope.Stroke(
        width = 0.8f * scaleX,
        cap = androidx.compose.ui.graphics.StrokeCap.Round,
        join = androidx.compose.ui.graphics.StrokeJoin.Round
      )
    )

    // Facet Lines Outer to Inner
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(45f * scaleX, 34f * scaleY), end = androidx.compose.ui.geometry.Offset(48f * scaleX, 38f * scaleY), strokeWidth = 1.5f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(63f * scaleX, 34f * scaleY), end = androidx.compose.ui.geometry.Offset(60f * scaleX, 38f * scaleY), strokeWidth = 1.5f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(68f * scaleX, 44f * scaleY), end = androidx.compose.ui.geometry.Offset(63f * scaleX, 44f * scaleY), strokeWidth = 1.5f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(54f * scaleX, 58f * scaleY), end = androidx.compose.ui.geometry.Offset(54f * scaleX, 53f * scaleY), strokeWidth = 1.5f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(40f * scaleX, 44f * scaleY), end = androidx.compose.ui.geometry.Offset(45f * scaleX, 44f * scaleY), strokeWidth = 1.5f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)

    // Facet Lines Inner to Center
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(48f * scaleX, 38f * scaleY), end = androidx.compose.ui.geometry.Offset(51f * scaleX, 41.5f * scaleY), strokeWidth = 0.8f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(60f * scaleX, 38f * scaleY), end = androidx.compose.ui.geometry.Offset(57f * scaleX, 41.5f * scaleY), strokeWidth = 0.8f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(63f * scaleX, 44f * scaleY), end = androidx.compose.ui.geometry.Offset(58.5f * scaleX, 44f * scaleY), strokeWidth = 0.8f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(54f * scaleX, 53f * scaleY), end = androidx.compose.ui.geometry.Offset(54f * scaleX, 48f * scaleY), strokeWidth = 0.8f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(45f * scaleX, 44f * scaleY), end = androidx.compose.ui.geometry.Offset(49.5f * scaleX, 44f * scaleY), strokeWidth = 0.8f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)

    // 4. Custom Task Manager Symbol (centered at Y=71)
    val paperPath = androidx.compose.ui.graphics.Path().apply {
      moveTo(49f * scaleX, 64f * scaleY)
      lineTo(57f * scaleX, 64f * scaleY)
      lineTo(61f * scaleX, 68f * scaleY)
      lineTo(61f * scaleX, 76f * scaleY)
      quadraticTo(61f * scaleX, 78f * scaleY, 59f * scaleX, 78f * scaleY)
      lineTo(49f * scaleX, 78f * scaleY)
      quadraticTo(47f * scaleX, 78f * scaleY, 47f * scaleX, 76f * scaleY)
      lineTo(47f * scaleX, 66f * scaleY)
      quadraticTo(47f * scaleX, 64f * scaleY, 49f * scaleX, 64f * scaleY)
      close()
    }
    drawPath(
      path = paperPath,
      color = color,
      style = androidx.compose.ui.graphics.drawscope.Stroke(
        width = 1.6f * scaleX,
        cap = androidx.compose.ui.graphics.StrokeCap.Round,
        join = androidx.compose.ui.graphics.StrokeJoin.Round
      )
    )

    // Folded Corner Flap
    val flapPath = androidx.compose.ui.graphics.Path().apply {
      moveTo(57f * scaleX, 64f * scaleY)
      lineTo(57f * scaleX, 68f * scaleY)
      lineTo(61f * scaleX, 68f * scaleY)
    }
    drawPath(
      path = flapPath,
      color = color,
      style = androidx.compose.ui.graphics.drawscope.Stroke(
        width = 1.6f * scaleX,
        cap = androidx.compose.ui.graphics.StrokeCap.Round,
        join = androidx.compose.ui.graphics.StrokeJoin.Round
      )
    )

    // Row 1: Line
    drawLine(
      color = color,
      start = androidx.compose.ui.geometry.Offset(50f * scaleX, 70f * scaleY),
      end = androidx.compose.ui.geometry.Offset(58f * scaleX, 70f * scaleY),
      strokeWidth = 1.4f * scaleX,
      cap = androidx.compose.ui.graphics.StrokeCap.Round
    )

    // Row 2: Checkmark
    val checkPath = androidx.compose.ui.graphics.Path().apply {
      moveTo(49.5f * scaleX, 73.5f * scaleY)
      lineTo(51f * scaleX, 75f * scaleY)
      lineTo(53f * scaleX, 72f * scaleY)
    }
    drawPath(
      path = checkPath,
      color = color,
      style = androidx.compose.ui.graphics.drawscope.Stroke(
        width = 1.4f * scaleX,
        cap = androidx.compose.ui.graphics.StrokeCap.Round,
        join = androidx.compose.ui.graphics.StrokeJoin.Round
      )
    )

    // Row 2: Line next to Checkmark
    drawLine(
      color = color,
      start = androidx.compose.ui.geometry.Offset(54.5f * scaleX, 74f * scaleY),
      end = androidx.compose.ui.geometry.Offset(58.5f * scaleX, 74f * scaleY),
      strokeWidth = 1.4f * scaleX,
      cap = androidx.compose.ui.graphics.StrokeCap.Round
    )
  }
}
