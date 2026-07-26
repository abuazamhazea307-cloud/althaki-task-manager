package com.example.series.identity.diamond

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.R
import com.example.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * MagicDiamondSplashEngine - Official Althaki Identity
 * The single master intro splash & materialization engine.
 * Avoids multiple screen transitions and completely eliminates intermediate blank states.
 *
 * Runs sequentially:
 * SCENE 1 (THE BIRTH OF THE DIAMOND) - 1200ms: Staggered wireframe and facet bloom.
 * SCENE 2 (THE LIVING DIAMOND) - 2500ms: Soft breathing glow and single slow shimmer diagonal sweep.
 * SCENE 3 (WELCOME MATERIALIZATION) - Seamless fade/slide-in of brand title, logo (📝), and subtitle, while the living gem stays centered.
 */
@Composable
fun MagicDiamondSplashEngine(navController: NavController) {
  // Birth & specular sweep progress
  val bloomProgress = remember { Animatable(0f) }
  val shimmerProgress = remember { Animatable(0f) }

  // Welcome element materialization animatables
  val brandTitleAlpha = remember { Animatable(0f) }
  val brandTitleOffsetY = remember { Animatable(25f) }

  val taskLogoAlpha = remember { Animatable(0f) }
  val taskLogoOffsetY = remember { Animatable(25f) }

  val subtitleAlpha = remember { Animatable(0f) }
  val subtitleOffsetY = remember { Animatable(25f) }

  val footerAlpha = remember { Animatable(0f) }
  val footerOffsetY = remember { Animatable(25f) }

  LaunchedEffect(Unit) {
    // ----------------------------------------------------
    // SCENE 1: THE BIRTH OF THE DIAMOND (1200ms)
    // ----------------------------------------------------
    bloomProgress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 1200, easing = LinearEasing)
    )

    // ----------------------------------------------------
    // SCENE 2: THE LIVING DIAMOND (2500ms)
    // Runs the diagonal shimmer sweep once across the crystal facets.
    // ----------------------------------------------------
    shimmerProgress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 2500, easing = FastOutSlowInEasing)
    )

    // ----------------------------------------------------
    // SCENE 3: WELCOME MATERIALIZATION
    // Staggered reveal: "الذكي" -> 💎 (stays in place) -> 📝 -> "مدير المهام" -> "مرحباً بك 🌹"
    // ----------------------------------------------------
    // 1. Brand Title: "الذكي"
    launch {
      brandTitleAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
    }
    launch {
      brandTitleOffsetY.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
    }

    delay(350L)

    // 2. Custom Task Symbol: 📝
    launch {
      taskLogoAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
    }
    launch {
      taskLogoOffsetY.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
    }

    delay(350L)

    // 3. Subtitle: "مدير المهام"
    launch {
      subtitleAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
    }
    launch {
      subtitleOffsetY.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
    }

    delay(350L)

    // 4. Welcome Footer text: "مرحبًا بك 🌹"
    launch {
      footerAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
    }
    launch {
      footerOffsetY.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
    }

    // Hold everything beautifully visible on screen before transitioning
    delay(1500L)

    // One-time smooth transition to Home Screen
    navController.navigate(Screen.Home.route) {
      launchSingleTop = true
      popUpTo(Screen.Splash.route) { inclusive = true }
    }
  }

  // Brand main background color (Pure Sky Blue)
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0EA5E9))
  ) {
    Column(
      modifier = Modifier
        .align(Alignment.Center)
        .padding(24.dp)
        .fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // 1. "الذكي" Brand Title
      Text(
        text = stringResource(R.string.splash_title),
        style = MaterialTheme.typography.displayMedium.copy(
          fontSize = 42.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          textAlign = TextAlign.Center,
          letterSpacing = 0.5.sp
        ),
        modifier = Modifier
          .graphicsLayer {
            alpha = brandTitleAlpha.value
            translationY = brandTitleOffsetY.value
          }
          .testTag("splash_brand_title")
      )

      Spacer(modifier = Modifier.height(24.dp))

      // 2. Althaki Diamond (💎) - Centered and stays alive continuously in place
      AlthakiDiamond(
        sizeDp = 180.dp,
        bloomProgress = bloomProgress.value,
        shimmerProgress = shimmerProgress.value,
        modifier = Modifier.testTag("splash_althaki_diamond")
      )

      Spacer(modifier = Modifier.height(24.dp))

      // 3. Custom Procedural Task Manager Welcome Logo (📝)
      Box(
        modifier = Modifier
          .size(70.dp)
          .graphicsLayer {
            alpha = taskLogoAlpha.value
            translationY = taskLogoOffsetY.value
          },
        contentAlignment = Alignment.Center
      ) {
        TaskManagerWelcomeLogo(
          modifier = Modifier.size(60.dp),
          color = Color.White
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 4. "مدير المهام" Subtitle
      Text(
        text = stringResource(R.string.splash_subtitle),
        style = MaterialTheme.typography.titleLarge.copy(
          fontSize = 24.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color.White.copy(alpha = 0.95f),
          textAlign = TextAlign.Center,
          letterSpacing = 0.5.sp
        ),
        modifier = Modifier
          .graphicsLayer {
            alpha = subtitleAlpha.value
            translationY = subtitleOffsetY.value
          }
          .testTag("splash_subtitle")
      )
    }

    // 5. Welcome Text at the bottom ("مرحبًا بك 🌹")
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
        .padding(bottom = 54.dp)
        .graphicsLayer {
          alpha = footerAlpha.value
          translationY = footerOffsetY.value
        }
        .testTag("splash_welcome_footer")
    )
  }
}

/**
 * Beautiful custom procedurally drawn TaskManager Logo.
 */
@Composable
fun TaskManagerWelcomeLogo(
  modifier: Modifier = Modifier,
  color: Color = Color.White
) {
  Canvas(modifier = modifier) {
    val width = size.width
    val height = size.height
    val scaleX = width / 100f
    val scaleY = height / 100f

    // Draw the clean check list paper sheet
    val paperPath = Path().apply {
      moveTo(45f * scaleX, 42f * scaleY)
      lineTo(55f * scaleX, 42f * scaleY)
      lineTo(60f * scaleX, 47f * scaleY)
      lineTo(60f * scaleX, 58f * scaleY)
      quadraticTo(60f * scaleX, 60f * scaleY, 58f * scaleX, 60f * scaleY)
      lineTo(45f * scaleX, 60f * scaleY)
      quadraticTo(43f * scaleX, 60f * scaleY, 43f * scaleX, 58f * scaleY)
      lineTo(43f * scaleX, 44f * scaleY)
      quadraticTo(43f * scaleX, 42f * scaleY, 45f * scaleX, 42f * scaleY)
      close()
    }
    drawPath(
      path = paperPath,
      color = color,
      style = Stroke(
        width = 2.0f * scaleX,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round
      )
    )

    // Corner folded flap
    val flapPath = Path().apply {
      moveTo(55f * scaleX, 42f * scaleY)
      lineTo(55f * scaleX, 47f * scaleY)
      lineTo(60f * scaleX, 47f * scaleY)
    }
    drawPath(
      path = flapPath,
      color = color,
      style = Stroke(
        width = 2.0f * scaleX,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round
      )
    )

    // Line Row 1
    drawLine(
      color = color,
      start = androidx.compose.ui.geometry.Offset(46.5f * scaleX, 49.5f * scaleY),
      end = androidx.compose.ui.geometry.Offset(53.5f * scaleX, 49.5f * scaleY),
      strokeWidth = 1.8f * scaleX,
      cap = StrokeCap.Round
    )

    // Row 2: Checkmark symbol
    val checkPath = Path().apply {
      moveTo(45.5f * scaleX, 54.5f * scaleY)
      lineTo(47.5f * scaleX, 56.5f * scaleY)
      lineTo(50.5f * scaleX, 52.5f * scaleY)
    }
    drawPath(
      path = checkPath,
      color = color,
      style = Stroke(
        width = 1.8f * scaleX,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round
      )
    )

    // Row 2: Line next to checkmark
    drawLine(
      color = color,
      start = androidx.compose.ui.geometry.Offset(52.5f * scaleX, 55f * scaleY),
      end = androidx.compose.ui.geometry.Offset(57.5f * scaleX, 55f * scaleY),
      strokeWidth = 1.8f * scaleX,
      cap = StrokeCap.Round
    )
  }
}
