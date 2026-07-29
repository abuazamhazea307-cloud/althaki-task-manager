package com.example.features.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag

@Composable
fun MagicDiamondSplashEngine() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("magic_diamond_splash_surface"),
        color = Color(0xFF030406) // Background black #030406
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("diamond_geometric_canvas")
        ) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            
            // Width and Height of the 2D diamond shape
            val halfWidth = size.width * 0.25f
            val halfHeight = size.height * 0.20f

            val path = Path().apply {
                moveTo(centerX, centerY - halfHeight) // Top vertex
                lineTo(centerX + halfWidth, centerY)  // Right vertex
                lineTo(centerX, centerY + halfHeight) // Bottom vertex
                lineTo(centerX - halfWidth, centerY)  // Left vertex
                close()
            }

            // Draw solid pure white diamond shape
            drawPath(
                path = path,
                color = Color.White
            )
        }
    }
}
