package com.example.series.identity.diamond

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.features.settings.GeneralSettingsManager
import com.example.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Stage 1: Diamond Splash Screen.
 * Fully complies with the Althaki Series design mandates:
 * - FIRST PIXEL RULE: Renders the AlthakiDiamond immediately from the very first frame.
 * - Solid Sky Blue (#0EA5E9) background filling the screen.
 * - NO TEXT, NO LOGOS, NO APPS NAMES. Just the Diamond itself in all its physical beauty.
 * - 3.5-second total duration.
 * - Flawless, smooth 60FPS animations.
 */
@Composable
fun AlthakiDiamondSplashEngine(navController: NavController) {
  val showAnimations = GeneralSettingsManager.enableAnimations

  // Resolve the total display duration: exactly 3.5 seconds (3500ms)
  val splashDuration = 3500L

  // 1. Core Gem materialization animation states
  val bloomProgress = remember { Animatable(0f) }
  val scaleProgress = remember { Animatable(0.75f) }
  val alphaProgress = remember { Animatable(0f) }

  // 2. Continuous Shimmer Sweep loop state (shines diagonally every 2.5 seconds)
  val shimmerProgress = remember { Animatable(-0.2f) }

  LaunchedEffect(Unit) {
    if (showAnimations) {
      // Phase 1: Materialize the diamond (Bloom, Scale, and Fade-in)
      launch {
        bloomProgress.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
      }
      launch {
        scaleProgress.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
      }
      launch {
        alphaProgress.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
      }

      // Shimmer sweep loop: sweeps light diagonally across the diamond facets
      launch {
        while (true) {
          shimmerProgress.animateTo(
            targetValue = 1.3f,
            animationSpec = tween(durationMillis = 1600, easing = FastOutSlowInEasing)
          )
          shimmerProgress.snapTo(-0.3f)
          delay(1200L) // Wait before starting the next diagonal sweep
        }
      }
    } else {
      // Instantly snap all visual transitions to finished state if animations are disabled
      bloomProgress.snapTo(1f)
      scaleProgress.snapTo(1f)
      alphaProgress.snapTo(1f)
      shimmerProgress.snapTo(1f)
    }

    // Wait for the exact required duration (3.5 seconds)
    delay(splashDuration)

    // Stage 1 -> Stage 2: Clean, secure navigation to the separate Welcome Screen
    navController.navigate(Screen.Welcome.route) {
      launchSingleTop = true
      popUpTo(Screen.Splash.route) { inclusive = true }
    }
  }

  // Base background is purely solid Sky Blue (#0EA5E9), meeting design requirements
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0EA5E9))
      .testTag("althaki_diamond_splash_screen"),
    contentAlignment = Alignment.Center
  ) {
    // AlthakiDiamond rendered directly from frame 1
    AlthakiDiamond(
      modifier = Modifier
        .size(200.dp)
        .graphicsLayer {
          scaleX = scaleProgress.value
          scaleY = scaleProgress.value
        }
        .testTag("althaki_diamond_gem"),
      bloomProgress = bloomProgress.value,
      shimmerProgress = shimmerProgress.value,
      alpha = alphaProgress.value
    )
  }
}
