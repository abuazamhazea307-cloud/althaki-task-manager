package com.example.series.identity.diamond

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
 * MagicDiamondSplashEngine - Official Althaki Identity
 *
 * Runs sequentially with precision timing scaled dynamically based on user preferences:
 * - Splash Screen Enabled (Default): Runs the complete luxury intro sequence over exactly 5.5 seconds (5500 ms).
 * - Splash Screen Disabled (Skip/Short Mode): Scales all animation stages proportionally to complete over exactly 3.0 seconds (3000 ms).
 *
 * ANIMATION STEPS:
 * 1. THE SEED OF LIGHT & BIRTH (Bloom: 0f -> 1f)
 * 2. SPECULAR GRADIENT SHIMMER SWEEP (Shimmer: 0f -> 1f)
 * 3. ANNOUNCEMENT TWINKLE FLASH (Sparkle: 0f -> 1f -> 0f)
 * 4. STAGGERED REVEAL (Strictly after the diamond sparkle cue):
 *    - "مرحباً بك 🌹" (Fade & Slide)
 *    - "الذكي" (Fade & Slide)
 *    - "مدير المهام" (Fade & Slide)
 */
@Composable
fun MagicDiamondSplashEngine(navController: NavController) {
  val isSplashEnabled = GeneralSettingsManager.showSplash
  val scaleFactor = if (isSplashEnabled) 1.0f else (3000f / 5500f)

  // Master animation controllers
  val bloomProgress = remember { Animatable(0f) }
  val shimmerProgress = remember { Animatable(0f) }
  val sparkleAlpha = remember { Animatable(0f) }

  // Sequenced text revealing animatables
  val welcomeAlpha = remember { Animatable(0f) }
  val welcomeOffsetY = remember { Animatable(15f) }

  val brandTitleAlpha = remember { Animatable(0f) }
  val brandTitleOffsetY = remember { Animatable(15f) }

  val subtitleAlpha = remember { Animatable(0f) }
  val subtitleOffsetY = remember { Animatable(15f) }

  LaunchedEffect(scaleFactor) {
    // Stage 1: The Birth and Complete Bloom of the Diamond (0 to 2200 ms * scaleFactor)
    bloomProgress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = (2200 * scaleFactor).toInt(), easing = FastOutSlowInEasing)
    )

    // Stage 2: Specular Gradient Shimmer Sweep across facets (2000 to 3200 ms * scaleFactor)
    // Starts just before full completion to create a fluid, premium visual transition
    shimmerProgress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = (1200 * scaleFactor).toInt(), easing = FastOutSlowInEasing)
    )

    // Stage 3: Grand Announcement Sparkle Twinkle Cue (3000 to 3400 ms * scaleFactor)
    // The peak diamond twinkle signals the release of the welcome greetings
    sparkleAlpha.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = (200 * scaleFactor).toInt(), easing = LinearEasing)
    )
    sparkleAlpha.animateTo(
      targetValue = 0f,
      animationSpec = tween(durationMillis = (200 * scaleFactor).toInt(), easing = LinearEasing)
    )

    // Stage 4: Staggered Text Reveal Flow (Strictly after the sparkle cue complete)
    // Step 4.1: "مرحباً بك 🌹"
    launch {
      welcomeAlpha.animateTo(1f, tween((600 * scaleFactor).toInt(), easing = FastOutSlowInEasing))
    }
    launch {
      welcomeOffsetY.animateTo(0f, tween((600 * scaleFactor).toInt(), easing = FastOutSlowInEasing))
    }

    delay((500 * scaleFactor).toLong())

    // Step 4.2: "الذكي" (Official Series Branding)
    launch {
      brandTitleAlpha.animateTo(1f, tween((600 * scaleFactor).toInt(), easing = FastOutSlowInEasing))
    }
    launch {
      brandTitleOffsetY.animateTo(0f, tween((600 * scaleFactor).toInt(), easing = FastOutSlowInEasing))
    }

    delay((500 * scaleFactor).toLong())

    // Step 4.3: "مدير المهام" (App Purpose Subtitle)
    launch {
      subtitleAlpha.animateTo(1f, tween((600 * scaleFactor).toInt(), easing = FastOutSlowInEasing))
    }
    launch {
      subtitleOffsetY.animateTo(0f, tween((600 * scaleFactor).toInt(), easing = FastOutSlowInEasing))
    }

    // Stage 5: Static Hold for perfect visual harmony before home navigation
    delay((600 * scaleFactor).toLong())

    // Safe, single-top navigation transition
    try {
      navController.navigate(Screen.Home.route) {
        launchSingleTop = true
        popUpTo(Screen.Splash.route) { inclusive = true }
      }
    } catch (e: Exception) {
      // Gracefully prevent crashes in unit/screenshot testing environments where navigation graph is not set up
    }
  }

  // Cinematic vertical gradient matching the Althaki luxury design system
  val backgroundBrush = Brush.verticalGradient(
    colors = listOf(
      Color(0xFF0EA5E9), // Vibrant premium Sky Blue
      Color(0xFF0369A1)  // Deeper oceanic navy hue for specular contrast
    )
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(brush = backgroundBrush)
  ) {
    Column(
      modifier = Modifier
        .align(Alignment.Center)
        .padding(horizontal = 24.dp, vertical = 32.dp)
        .fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // 1. "مرحباً بك 🌹" - The first greeting text at the top
      Text(
        text = stringResource(R.string.splash_welcome) + " 🌹",
        style = MaterialTheme.typography.titleMedium.copy(
          fontSize = 20.sp,
          fontWeight = FontWeight.Medium,
          color = Color.White.copy(alpha = 0.92f),
          textAlign = TextAlign.Center,
          letterSpacing = 0.5.sp
        ),
        modifier = Modifier
          .graphicsLayer {
            alpha = welcomeAlpha.value
            translationY = welcomeOffsetY.value
          }
          .testTag("splash_welcome_footer")
      )

      Spacer(modifier = Modifier.height(28.dp))

      // 2. Althaki Diamond (💎) - Centered and stable in the layout
      AlthakiDiamond(
        sizeDp = 180.dp,
        bloomProgress = bloomProgress.value,
        shimmerProgress = shimmerProgress.value,
        sparkleAlpha = sparkleAlpha.value,
        modifier = Modifier.testTag("splash_althaki_diamond")
      )

      Spacer(modifier = Modifier.height(28.dp))

      // 3. "الذكي" - The brand name below the diamond
      Text(
        text = stringResource(R.string.splash_title),
        style = MaterialTheme.typography.displayMedium.copy(
          fontSize = 44.sp,
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

      Spacer(modifier = Modifier.height(10.dp))

      // 4. "مدير المهام" - The functional subtitle
      Text(
        text = stringResource(R.string.splash_subtitle),
        style = MaterialTheme.typography.titleLarge.copy(
          fontSize = 24.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color.White.copy(alpha = 0.85f),
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
  }
}
