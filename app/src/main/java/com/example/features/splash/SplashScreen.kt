package com.example.features.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.series.identity.diamond.MagicDiamond
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Clean Rebuilt SplashScreen utilizing the unified MagicDiamondSplashEngine.
 * Implements a flawless, fluid double-phase flow with absolutely no blank screens or lag.
 * Phase 1: 0ms - 1000ms: Standalone 3D procedural MagicDiamond.
 * Phase 2: 1000ms onwards: Morphological shift into the grand "Althaki" series welcoming stack.
 */
@Composable
fun SplashScreen(navController: NavController) {
  val showAnimations = GeneralSettingsManager.enableAnimations
  val durationType = GeneralSettingsManager.splashDuration

  // Resolve the total display duration purely from the settings selection
  val welcomeDuration = when (durationType) {
    GeneralSettingsManager.DURATION_SHORT -> 1500L
    GeneralSettingsManager.DURATION_NORMAL -> 3000L
    GeneralSettingsManager.DURATION_LONG -> 5000L
    else -> 4000L // Default (Recommended): 4000L
  }

  // 1. Core Magic Diamond Animation states (Procedural Bloom & Shimmer)
  val bloomProgress = remember { Animatable(0f) }
  val shimmerProgress = remember { Animatable(-0.5f) }

  // 2. Welcome Stack elements (Fade-in + Scale-up animations)
  val brandTitleAlpha = remember { Animatable(0f) }
  val brandTitleScale = remember { Animatable(0.92f) }

  val taskLogoAlpha = remember { Animatable(0f) }
  val taskLogoScale = remember { Animatable(0.92f) }

  val subtitleAlpha = remember { Animatable(0f) }
  val subtitleScale = remember { Animatable(0.92f) }

  val welcomeTextAlpha = remember { Animatable(0f) }
  val welcomeTextScale = remember { Animatable(0.92f) }

  // 3. Layout Morphological Transition progress
  val layoutShiftProgress = remember { Animatable(0f) }

  LaunchedEffect(Unit) {
    if (showAnimations) {
      // PHASE 1: The Magic Diamond materializes (Bloom) and shines (Shimmer)
      launch {
        bloomProgress.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
      }
      launch {
        shimmerProgress.animateTo(
          targetValue = 1.5f,
          animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
      }

      // Wait exactly 1000ms for Phase 1 to finish completely
      delay(1000L)

      // PHASE 2: Morph/Shift layout dynamically and fade in the welcoming identity stack
      launch {
        layoutShiftProgress.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
      }

      // Brand Title "الذكي"
      launch {
        brandTitleAlpha.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
      }
      launch {
        brandTitleScale.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
      }

      // Task Logo (📝)
      delay(150L)
      launch {
        taskLogoAlpha.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
      }
      launch {
        taskLogoScale.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
      }

      // Subtitle "مدير المهام"
      delay(150L)
      launch {
        subtitleAlpha.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
      }
      launch {
        subtitleScale.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
      }

      // Welcome Message at the bottom "مرحباً بك 🌹"
      delay(150L)
      launch {
        welcomeTextAlpha.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
      }
      launch {
        welcomeTextScale.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
      }

      // Remain visible until the chosen welcome duration has completed
      delay(welcomeDuration)
    } else {
      // Direct instant snapping of all components if animations are disabled by user settings
      bloomProgress.snapTo(1f)
      shimmerProgress.snapTo(1.5f)
      layoutShiftProgress.snapTo(1f)
      brandTitleAlpha.snapTo(1f)
      brandTitleScale.snapTo(1f)
      taskLogoAlpha.snapTo(1f)
      taskLogoScale.snapTo(1f)
      subtitleAlpha.snapTo(1f)
      subtitleScale.snapTo(1f)
      welcomeTextAlpha.snapTo(1f)
      welcomeTextScale.snapTo(1f)

      delay(welcomeDuration)
    }

    // Secure navigation to home screen
    navController.navigate(Screen.Home.route) {
      launchSingleTop = true
      popUpTo(Screen.Splash.route) { inclusive = true }
    }
  }

  // Pure static sky blue background filling the screen from frame 1
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0EA5E9))
  ) {
    // 1. Unified 3D MagicDiamond Composable
    // Shifts dynamically vertically as part of the morphological welcome transition
    Box(
      modifier = Modifier
        .align(Alignment.Center)
        .graphicsLayer {
          val shiftPx = layoutShiftProgress.value * 160.dp.toPx()
          translationY = -shiftPx
        }
    ) {
      MagicDiamond(
        modifier = Modifier.size(170.dp),
        bloomProgress = bloomProgress.value,
        shimmerProgress = shimmerProgress.value,
        alpha = 1f
      )
    }

    // 2. Core welcoming elements layout (Grown from first frame, animated in Phase 2)
    Column(
      modifier = Modifier
        .align(Alignment.Center)
        .padding(top = 110.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // A. "الذكي" Brand Title
      Text(
        text = stringResource(R.string.splash_title),
        style = MaterialTheme.typography.displayMedium.copy(
          fontSize = 38.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          letterSpacing = 0.5.sp
        ),
        modifier = Modifier
          .graphicsLayer {
            alpha = brandTitleAlpha.value
            scaleX = brandTitleScale.value
            scaleY = brandTitleScale.value
          }
          .testTag("splash_brand_title")
      )

      Spacer(modifier = Modifier.height(30.dp))

      // B. 📝 Custom Task Manager Logo
      Box(
        modifier = Modifier
          .size(72.dp)
          .graphicsLayer {
            alpha = taskLogoAlpha.value
            scaleX = taskLogoScale.value
            scaleY = taskLogoScale.value
          },
        contentAlignment = Alignment.Center
      ) {
        TaskManagerCustomLogo(
          modifier = Modifier.size(54.dp),
          color = Color.White
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // C. "مدير المهام" Subtitle
      Text(
        text = stringResource(R.string.splash_subtitle),
        style = MaterialTheme.typography.titleLarge.copy(
          fontSize = 22.sp,
          fontWeight = FontWeight.Medium,
          color = Color.White.copy(alpha = 0.9f),
          letterSpacing = 0.5.sp
        ),
        modifier = Modifier
          .graphicsLayer {
            alpha = subtitleAlpha.value
            scaleX = subtitleScale.value
            scaleY = subtitleScale.value
          }
          .testTag("splash_subtitle")
      )
    }

    // 3. Welcome Text ("مرحبًا بك 🌹") at the bottom of the screen
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
        .graphicsLayer {
          alpha = welcomeTextAlpha.value
          scaleX = welcomeTextScale.value
          scaleY = welcomeTextScale.value
        }
        .testTag("splash_welcome_text")
    )
  }
}

/**
 * Custom procedurally drawn TaskManager Logo.
 */
@Composable
fun TaskManagerCustomLogo(
  modifier: Modifier = Modifier,
  color: Color = Color.White
) {
  Canvas(modifier = modifier) {
    val width = size.width
    val height = size.height
    val scaleX = width / 108f
    val scaleY = height / 108f

    // Paper outline
    val paperPath = Path().apply {
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
      style = Stroke(
        width = 1.6f * scaleX,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round
      )
    )

    // Folded Corner Flap
    val flapPath = Path().apply {
      moveTo(57f * scaleX, 47f * scaleY)
      lineTo(57f * scaleX, 51f * scaleY)
      lineTo(61f * scaleX, 51f * scaleY)
    }
    drawPath(
      path = flapPath,
      color = color,
      style = Stroke(
        width = 1.6f * scaleX,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round
      )
    )

    // Row 1: Line
    drawLine(
      color = color,
      start = androidx.compose.ui.geometry.Offset(50f * scaleX, 53f * scaleY),
      end = androidx.compose.ui.geometry.Offset(58f * scaleX, 53f * scaleY),
      strokeWidth = 1.4f * scaleX,
      cap = StrokeCap.Round
    )

    // Row 2: Checkmark
    val checkPath = Path().apply {
      moveTo(49.5f * scaleX, 56.5f * scaleY)
      lineTo(51f * scaleX, 58f * scaleY)
      lineTo(53f * scaleX, 55f * scaleY)
    }
    drawPath(
      path = checkPath,
      color = color,
      style = Stroke(
        width = 1.4f * scaleX,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round
      )
    )

    // Row 2: Line next to Checkmark
    drawLine(
      color = color,
      start = androidx.compose.ui.geometry.Offset(54.5f * scaleX, 57f * scaleY),
      end = androidx.compose.ui.geometry.Offset(58.5f * scaleX, 57f * scaleY),
      strokeWidth = 1.4f * scaleX,
      cap = StrokeCap.Round
    )
  }
}

/**
 * Geometric Diamond Logo for Backward Compatibility (e.g. Settings, About).
 */
@Composable
fun GeometricLogo(
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.primary
) {
  Canvas(modifier = modifier) {
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
      style = Stroke(width = 2.5f * scaleX)
    )

    // 2. Thin Inner Circular Ring (Radius 26)
    drawCircle(
      color = color,
      radius = 26f * scaleX,
      center = androidx.compose.ui.geometry.Offset(54f * scaleX, 54f * scaleY),
      style = Stroke(width = 1.0f * scaleX)
    )

    // 3. Official Althaki Diamond Logo (centered at Y=44)
    val outerDiamond = Path().apply {
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
      style = Stroke(
        width = 2.0f * scaleX,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round
      )
    )

    // Inner Crystal Outline
    val innerDiamond = Path().apply {
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
      style = Stroke(
        width = 1.2f * scaleX,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round
      )
    )

    // Tiny Center Crystal
    val centerDiamond = Path().apply {
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
      style = Stroke(
        width = 0.8f * scaleX,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round
      )
    )

    // Facet Lines Outer to Inner
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(45f * scaleX, 34f * scaleY), end = androidx.compose.ui.geometry.Offset(48f * scaleX, 38f * scaleY), strokeWidth = 1.5f * scaleX, cap = StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(63f * scaleX, 34f * scaleY), end = androidx.compose.ui.geometry.Offset(60f * scaleX, 38f * scaleY), strokeWidth = 1.5f * scaleX, cap = StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(68f * scaleX, 44f * scaleY), end = androidx.compose.ui.geometry.Offset(63f * scaleX, 44f * scaleY), strokeWidth = 1.5f * scaleX, cap = StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(54f * scaleX, 58f * scaleY), end = androidx.compose.ui.geometry.Offset(54f * scaleX, 53f * scaleY), strokeWidth = 1.5f * scaleX, cap = StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(40f * scaleX, 44f * scaleY), end = androidx.compose.ui.geometry.Offset(45f * scaleX, 44f * scaleY), strokeWidth = 1.5f * scaleX, cap = StrokeCap.Round)

    // Facet Lines Inner to Center
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(48f * scaleX, 38f * scaleY), end = androidx.compose.ui.geometry.Offset(51f * scaleX, 41.5f * scaleY), strokeWidth = 0.8f * scaleX, cap = StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(60f * scaleX, 38f * scaleY), end = androidx.compose.ui.geometry.Offset(57f * scaleX, 41.5f * scaleY), strokeWidth = 0.8f * scaleX, cap = StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(63f * scaleX, 44f * scaleY), end = androidx.compose.ui.geometry.Offset(58.5f * scaleX, 44f * scaleY), strokeWidth = 0.8f * scaleX, cap = StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(54f * scaleX, 53f * scaleY), end = androidx.compose.ui.geometry.Offset(54f * scaleX, 48f * scaleY), strokeWidth = 0.8f * scaleX, cap = StrokeCap.Round)
    drawLine(color = color, start = androidx.compose.ui.geometry.Offset(45f * scaleX, 44f * scaleY), end = androidx.compose.ui.geometry.Offset(49.5f * scaleX, 44f * scaleY), strokeWidth = 0.8f * scaleX, cap = StrokeCap.Round)

    // 4. Custom Task Manager Symbol (centered at Y=71)
    val paperPath = Path().apply {
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
      style = Stroke(
        width = 1.6f * scaleX,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round
      )
    )

    // Folded Corner Flap
    val flapPath = Path().apply {
      moveTo(57f * scaleX, 64f * scaleY)
      lineTo(57f * scaleX, 68f * scaleY)
      lineTo(61f * scaleX, 68f * scaleY)
    }
    drawPath(
      path = flapPath,
      color = color,
      style = Stroke(
        width = 1.6f * scaleX,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round
      )
    )

    // Row 1: Line
    drawLine(
      color = color,
      start = androidx.compose.ui.geometry.Offset(50f * scaleX, 70f * scaleY),
      end = androidx.compose.ui.geometry.Offset(58f * scaleX, 70f * scaleY),
      strokeWidth = 1.4f * scaleX,
      cap = StrokeCap.Round
    )

    // Row 2: Checkmark
    val checkPath = Path().apply {
      moveTo(49.5f * scaleX, 73.5f * scaleY)
      lineTo(51f * scaleX, 75f * scaleY)
      lineTo(53f * scaleX, 72f * scaleY)
    }
    drawPath(
      path = checkPath,
      color = color,
      style = Stroke(
        width = 1.4f * scaleX,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round
      )
    )

    // Row 2: Line next to Checkmark
    drawLine(
      color = color,
      start = androidx.compose.ui.geometry.Offset(54.5f * scaleX, 74f * scaleY),
      end = androidx.compose.ui.geometry.Offset(58.5f * scaleX, 74f * scaleY),
      strokeWidth = 1.4f * scaleX,
      cap = StrokeCap.Round
    )
  }
}
