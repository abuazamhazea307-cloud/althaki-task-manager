package com.example.features.welcome

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.features.settings.GeneralSettingsManager
import com.example.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Stage 2: Welcome Screen.
 * Displays the Althaki Series brand identity with high-end, premium animations.
 * Features:
 * - Brand Title: "الذكي" (R.string.splash_title)
 * - Custom Task Manager Logo (📝) procedurally drawn on Canvas
 * - Subtitle: "مدير المهام" (R.string.splash_subtitle)
 * - Welcome Text: "مرحبًا بك 🌹" (R.string.splash_welcome)
 * - Premium Material 3 CTA Button ("ابدأ الآن") for instant access
 * - Smart auto-transition to Home Screen after 3 seconds if button is not pressed
 */
@Composable
fun WelcomeScreen(navController: NavController) {
  val showAnimations = GeneralSettingsManager.enableAnimations

  // Core visual elements animations (Fade-in and subtle translation)
  val brandTitleAlpha = remember { Animatable(0f) }
  val brandTitleScale = remember { Animatable(0.9f) }

  val logoAlpha = remember { Animatable(0f) }
  val logoScale = remember { Animatable(0.9f) }

  val subtitleAlpha = remember { Animatable(0f) }
  val subtitleScale = remember { Animatable(0.9f) }

  val ctaAlpha = remember { Animatable(0f) }
  val ctaScale = remember { Animatable(0.9f) }

  val welcomeAlpha = remember { Animatable(0f) }

  fun navigateToHome() {
    navController.navigate(Screen.Home.route) {
      launchSingleTop = true
      popUpTo(Screen.Welcome.route) { inclusive = true }
    }
  }

  LaunchedEffect(Unit) {
    if (showAnimations) {
      // Elegant staggered animation reveal sequence
      launch {
        brandTitleAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
      }
      launch {
        brandTitleScale.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
      }

      delay(150L)
      launch {
        logoAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
      }
      launch {
        logoScale.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
      }

      delay(150L)
      launch {
        subtitleAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
      }
      launch {
        subtitleScale.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
      }

      delay(200L)
      launch {
        ctaAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
      }
      launch {
        ctaScale.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
      }

      delay(150L)
      launch {
        welcomeAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
      }

      // 3-second auto-transition to the home screen if user doesn't press CTA button
      delay(3000L)
      navigateToHome()
    } else {
      brandTitleAlpha.snapTo(1f)
      brandTitleScale.snapTo(1f)
      logoAlpha.snapTo(1f)
      logoScale.snapTo(1f)
      subtitleAlpha.snapTo(1f)
      subtitleScale.snapTo(1f)
      ctaAlpha.snapTo(1f)
      ctaScale.snapTo(1f)
      welcomeAlpha.snapTo(1f)

      delay(1500L)
      navigateToHome()
    }
  }

  // Pure premium sky blue background matching Stage 1 but with identity elements
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
          fontSize = 40.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          textAlign = TextAlign.Center,
          letterSpacing = 0.5.sp
        ),
        modifier = Modifier
          .graphicsLayer {
            alpha = brandTitleAlpha.value
            scaleX = brandTitleScale.value
            scaleY = brandTitleScale.value
          }
          .testTag("welcome_brand_title")
      )

      Spacer(modifier = Modifier.height(28.dp))

      // 2. Custom Task Manager Procedural Canvas Logo (📝)
      Box(
        modifier = Modifier
          .size(90.dp)
          .graphicsLayer {
            alpha = logoAlpha.value
            scaleX = logoScale.value
            scaleY = logoScale.value
          },
        contentAlignment = Alignment.Center
      ) {
        TaskManagerWelcomeLogo(
          modifier = Modifier.size(70.dp),
          color = Color.White
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // 3. "مدير المهام" Subtitle
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
            scaleX = subtitleScale.value
            scaleY = subtitleScale.value
          }
          .testTag("welcome_subtitle")
      )

      Spacer(modifier = Modifier.height(48.dp))

      // 4. Premium CTA Button "ابدأ الآن"
      Button(
        onClick = { navigateToHome() },
        colors = ButtonDefaults.buttonColors(
          containerColor = Color.White,
          contentColor = Color(0xFF0EA5E9)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
          defaultElevation = 6.dp,
          pressedElevation = 2.dp
        ),
        modifier = Modifier
          .widthIn(min = 200.dp, max = 320.dp)
          .height(54.dp)
          .graphicsLayer {
            alpha = ctaAlpha.value
            scaleX = ctaScale.value
            scaleY = ctaScale.value
          }
          .testTag("welcome_cta_button")
      ) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "ابدأ الآن",
            style = MaterialTheme.typography.titleMedium.copy(
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )
          )
        }
      }
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
          alpha = welcomeAlpha.value
        }
        .testTag("welcome_footer")
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
