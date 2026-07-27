package com.example.features.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag

@Composable
fun MagicDiamondSplashEngine() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("magic_diamond_splash_surface"),
        color = Color(0xFF030406) // Luxury deep black
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            DiamondPlaceholder()
        }
    }
}

@Composable
private fun DiamondPlaceholder() {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .testTag("diamond_placeholder_canvas")
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        drawCircle(
            color = Color.White,
            radius = 6f,
            center = Offset(centerX, centerY)
        )
    }
}
