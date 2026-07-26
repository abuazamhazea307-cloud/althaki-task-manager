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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
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
import com.example.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavController) {
  val showAnimations = com.example.features.settings.GeneralSettingsManager.enableAnimations
  val durationType = com.example.features.settings.GeneralSettingsManager.splashDuration

  // Adjust welcome branding duration based on Settings selection
  val welcomeDuration = when (durationType) {
    com.example.features.settings.GeneralSettingsManager.DURATION_SHORT -> 1500L
    com.example.features.settings.GeneralSettingsManager.DURATION_NORMAL -> 3000L
    com.example.features.settings.GeneralSettingsManager.DURATION_LONG -> 5000L
    else -> 4000L // Default (Recommended)
  }

  // Animatables start at 0f (except scale) so elements are hidden initially and then animate in
  val diamondAlpha = remember { Animatable(0f) }
  val diamondScale = remember { Animatable(0.8f) }

  val titleAlpha = remember { Animatable(0f) }
  val taskLogoAlpha = remember { Animatable(0f) }
  val subtitleAlpha = remember { Animatable(0f) }
  val welcomeAlpha = remember { Animatable(0f) }

  LaunchedEffect(Unit) {
    if (showAnimations) {
      // Calculate scaled animation duration and step delay (each is 10% of welcome duration)
      val animDuration = (welcomeDuration * 0.10f).toInt()
      val stepDelay = (welcomeDuration * 0.10f).toLong()

      // Animation 1: Official Althaki Diamond (💎)
      launch {
        diamondAlpha.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = animDuration, easing = FastOutSlowInEasing)
        )
      }
      launch {
        diamondScale.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = animDuration, easing = FastOutSlowInEasing)
        )
      }

      // Animation 2: Below the Diamond - "الذكي" Fade In
      delay(stepDelay)
      launch {
        titleAlpha.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = animDuration, easing = FastOutSlowInEasing)
        )
      }

      // Animation 3: Below "الذكي" - Custom Task Manager Logo (📝) Fade In
      delay(stepDelay)
      launch {
        taskLogoAlpha.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = animDuration, easing = FastOutSlowInEasing)
        )
      }

      // Animation 4: Below the Task Logo - "مدير المهام" Subtitle Fade In
      delay(stepDelay)
      launch {
        subtitleAlpha.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = animDuration, easing = FastOutSlowInEasing)
        )
      }

      // Animation 5: Bottom area - "مرحبًا بك 🌹" Fade In gently
      delay(stepDelay)
      launch {
        welcomeAlpha.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = animDuration, easing = FastOutSlowInEasing)
        )
      }

      // Complete the remaining welcome duration (rest time)
      val totalSpent = stepDelay * 4 + animDuration
      val remaining = (welcomeDuration - totalSpent).coerceAtLeast(0L)
      delay(remaining)
    } else {
      // If animations are disabled, immediately snap all elements to visible, and wait welcomeDuration
      diamondAlpha.snapTo(1f)
      diamondScale.snapTo(1f)
      titleAlpha.snapTo(1f)
      taskLogoAlpha.snapTo(1f)
      subtitleAlpha.snapTo(1f)
      welcomeAlpha.snapTo(1f)

      delay(welcomeDuration)
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
        .background(Color(0xFF0EA5E9))
        .testTag("splash_screen_root"),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxSize()
          .padding(top = 80.dp, bottom = 60.dp, start = 24.dp, end = 24.dp)
      ) {
        // Top spacer for visual balance
        Spacer(modifier = Modifier.height(1.dp))

        // Central visual identity stack
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          // Animation 1: Official Althaki Diamond
          Box(
            modifier = Modifier
              .size(100.dp)
              .graphicsLayer(
                scaleX = diamondScale.value,
                scaleY = diamondScale.value,
                alpha = diamondAlpha.value
              ),
            contentAlignment = Alignment.Center
          ) {
            AlthakiDiamondLogo(
              modifier = Modifier.size(80.dp),
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(24.dp))

          // Animation 2: Brand text "الذكي"
          Text(
            text = stringResource(R.string.splash_title),
            style = MaterialTheme.typography.displayMedium.copy(
              fontSize = 38.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              letterSpacing = 0.5.sp
            ),
            modifier = Modifier
              .graphicsLayer(alpha = titleAlpha.value)
              .testTag("splash_brand_title")
          )

          Spacer(modifier = Modifier.height(44.dp))

          // Animation 3: Custom Task Manager Logo
          Box(
            modifier = Modifier
              .size(72.dp)
              .graphicsLayer(alpha = taskLogoAlpha.value),
            contentAlignment = Alignment.Center
          ) {
            TaskManagerCustomLogo(
              modifier = Modifier.size(54.dp),
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Animation 4: "مدير المهام" Subtitle
          Text(
            text = stringResource(R.string.splash_subtitle),
            style = MaterialTheme.typography.titleLarge.copy(
              fontSize = 22.sp,
              fontWeight = FontWeight.Medium,
              color = Color.White.copy(alpha = 0.9f),
              letterSpacing = 0.5.sp
            ),
            modifier = Modifier
              .graphicsLayer(alpha = subtitleAlpha.value)
              .testTag("splash_subtitle")
          )
        }

        // Animation 5: Welcome text "مرحبًا بك 🌹"
        Text(
          text = stringResource(R.string.splash_welcome) + " 🌹",
          style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.85f),
            letterSpacing = 0.5.sp
          ),
          modifier = Modifier
            .graphicsLayer(alpha = welcomeAlpha.value)
            .testTag("splash_welcome_text")
            .padding(bottom = 24.dp)
        )
      }
    }
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

    // 1. Large White Outer Circle (Radius 30)
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
    // Outer Crystal Outline
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
    // Paper Outline
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
