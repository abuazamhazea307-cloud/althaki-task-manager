package com.example.series.identity.diamond

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.cos
import kotlin.math.sin

/**
 * MagicDiamond - The Unified Official 3D Procedural Diamond for "Althaki" Series.
 * Drawn entirely using vector Paths, Brushes, and dynamic animated shaders/gradients.
 * Features: Bloom, Breathing, Shimmer, Sparkles, Glow, and ground Reflection.
 */
@Composable
fun MagicDiamond(
  modifier: Modifier = Modifier,
  bloomProgress: Float = 1f,
  shimmerProgress: Float = 0f,
  alpha: Float = 1f
) {
  val infiniteTransition = rememberInfiniteTransition(label = "DiamondEffects")

  // 1. Breathing Scale Animation: 0.98f -> 1.02f -> 0.98f
  val breathingScale by infiniteTransition.animateFloat(
    initialValue = 0.98f,
    targetValue = 1.02f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "Breathing"
  )

  // 2. Glow Opacity Animation: Changes slowly to make the diamond look alive
  val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 0.8f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 3000, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "Glow"
  )

  // 3. Sparkles alpha animations - staggered twinkling
  val sparkle1Alpha by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = keyframes {
        durationMillis = 1800
        0f at 0
        1f at 400
        0f at 900
        0f at 1800
      },
      repeatMode = RepeatMode.Restart
    ),
    label = "Sparkle1"
  )

  val sparkle2Alpha by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = keyframes {
        durationMillis = 2200
        0f at 0
        0f at 600
        1f at 1100
        0f at 1600
        0f at 2200
      },
      repeatMode = RepeatMode.Restart
    ),
    label = "Sparkle2"
  )

  val sparkle3Alpha by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = keyframes {
        durationMillis = 2500
        0f at 0
        0f at 1200
        1f at 1700
        0f at 2200
        0f at 2500
      },
      repeatMode = RepeatMode.Restart
    ),
    label = "Sparkle3"
  )

  val finalScale = breathingScale * (0.8f + 0.2f * bloomProgress)

  Canvas(modifier = modifier) {
    val width = size.width
    val height = size.height

    // Scale factors to map the geometric 100x100 design grid to local pixel space
    val scaleX = width / 100f
    val scaleY = height / 100f

    // Center coordinates
    val cx = 50f * scaleX
    val cy = 48f * scaleY

    // Define 10 high-precision 3D diamond vertices
    val v1 = Offset(35f * scaleX, 25f * scaleY)  // Top-Left Table
    val v2 = Offset(65f * scaleX, 25f * scaleY)  // Top-Right Table
    val v3 = Offset(15f * scaleX, 48f * scaleY)  // Girdle Far-Left
    val v4 = Offset(32f * scaleX, 48f * scaleY)  // Girdle Inner-Left
    val v5 = Offset(50f * scaleX, 48f * scaleY)  // Girdle Center
    val v6 = Offset(68f * scaleX, 48f * scaleY)  // Girdle Inner-Right
    val v7 = Offset(85f * scaleX, 48f * scaleY)  // Girdle Far-Right
    val v8 = Offset(50f * scaleX, 85f * scaleY)  // Culet (Bottom Center)
    val v9 = Offset(43f * scaleX, 35f * scaleY)  // Crown Upper-Mid-Left
    val v10 = Offset(57f * scaleX, 35f * scaleY) // Crown Upper-Mid-Right

    // Diamond color scheme
    val cWhite = Color(0xFFFFFFFF)
    val cDiamondWhite = Color(0xFFEAF8FF)
    val cCrystalBlue = Color(0xFFD8F4FF)
    val cSky = Color(0xFF7DD3FC)
    val cBlue = Color(0xFF38BDF8)
    val cPrimary = Color(0xFF0EA5E9)
    val cDeepBlue = Color(0xFF0284C7)
    val cDarkBlue = Color(0xFF0369A1)

    // Outer Silhouette Path (used for clipping & ground reflection)
    val silhouettePath = Path().apply {
      moveTo(v1.x, v1.y)
      lineTo(v2.x, v2.y)
      lineTo(v7.x, v7.y)
      lineTo(v8.x, v8.y)
      lineTo(v3.x, v3.y)
      close()
    }

    // 1. Draw Bloom Light Rays (if bloom is running)
    if (bloomProgress < 0.95f) {
      val rayAlpha = (1f - bloomProgress) * 0.75f * alpha
      val rayCount = 8
      val maxRayLength = (1f - bloomProgress) * 90f * scaleX
      for (i in 0 until rayCount) {
        val angle = (i * (2 * Math.PI / rayCount)) + (bloomProgress * 0.5)
        val rx = cx + (cos(angle) * maxRayLength).toFloat()
        val ry = cy + (sin(angle) * maxRayLength).toFloat()
        drawLine(
          color = cDiamondWhite.copy(alpha = rayAlpha),
          start = Offset(cx, cy),
          end = Offset(rx, ry),
          strokeWidth = 2.5f * scaleX,
          cap = StrokeCap.Round
        )
      }
    }

    // 2. Glow Effect (Ambient Soft Lighting Behind the Diamond)
    val glowRadius = 80f * scaleX * finalScale
    val glowBrush = Brush.radialGradient(
      colors = listOf(
        cWhite.copy(alpha = 0.35f * glowAlpha * alpha * bloomProgress),
        cCrystalBlue.copy(alpha = 0.15f * glowAlpha * alpha * bloomProgress),
        Color.Transparent
      ),
      center = Offset(cx, cy),
      radius = glowRadius
    )
    drawCircle(
      brush = glowBrush,
      center = Offset(cx, cy),
      radius = glowRadius
    )

    // 3. Ground Reflection Effect (Flipped vertically, lowered opacity and vertical scale)
    withTransform({
      translate(top = height * 0.45f)
      scale(scaleX = finalScale, scaleY = -0.22f * finalScale)
    }) {
      // Draw mirrored facets at very low opacity
      val refAlpha = 0.12f * alpha * bloomProgress
      drawFacet(this, v1, v2, v10, v9, Brush.verticalGradient(listOf(cWhite.copy(alpha = refAlpha), cCrystalBlue.copy(alpha = refAlpha))))
      drawFacet(this, v1, v9, v4, v3, Brush.verticalGradient(listOf(cDiamondWhite.copy(alpha = refAlpha), cBlue.copy(alpha = refAlpha))))
      drawFacet(this, v2, v10, v6, v7, Brush.verticalGradient(listOf(cCrystalBlue.copy(alpha = refAlpha), cDeepBlue.copy(alpha = refAlpha))))
      drawFacet(this, v9, v5, v4, null, Brush.verticalGradient(listOf(cDiamondWhite.copy(alpha = refAlpha), cPrimary.copy(alpha = refAlpha))))
      drawFacet(this, v10, v5, v6, null, Brush.verticalGradient(listOf(cCrystalBlue.copy(alpha = refAlpha), cDarkBlue.copy(alpha = refAlpha))))
      drawFacet(this, v9, v10, v5, null, Brush.verticalGradient(listOf(cWhite.copy(alpha = refAlpha), cSky.copy(alpha = refAlpha))))
      drawFacet(this, v3, v4, v8, null, Brush.verticalGradient(listOf(cBlue.copy(alpha = refAlpha), cDarkBlue.copy(alpha = refAlpha))))
      drawFacet(this, v4, v5, v8, null, Brush.verticalGradient(listOf(cSky.copy(alpha = refAlpha), cDeepBlue.copy(alpha = refAlpha))))
      drawFacet(this, v5, v6, v8, null, Brush.verticalGradient(listOf(cCrystalBlue.copy(alpha = refAlpha), cPrimary.copy(alpha = refAlpha))))
      drawFacet(this, v6, v7, v8, null, Brush.verticalGradient(listOf(cDiamondWhite.copy(alpha = refAlpha), cDarkBlue.copy(alpha = refAlpha))))
    }

    // Apply main scale transforms (breathing + bloom)
    withTransform({
      scale(scaleX = finalScale, scaleY = finalScale)
    }) {
      // 4. Fill Each 3D Facet with Custom Gradients for Real Optical Depth
      val fAlpha = alpha * bloomProgress

      // Crown Facets
      drawFacet(this, v1, v2, v10, v9, Brush.linearGradient(listOf(cWhite.copy(alpha = fAlpha), cCrystalBlue.copy(alpha = fAlpha)), start = v1, end = v10))
      drawFacet(this, v1, v9, v4, v3, Brush.linearGradient(listOf(cDiamondWhite.copy(alpha = fAlpha), cBlue.copy(alpha = fAlpha)), start = v1, end = v4))
      drawFacet(this, v2, v10, v6, v7, Brush.linearGradient(listOf(cCrystalBlue.copy(alpha = fAlpha), cDeepBlue.copy(alpha = fAlpha)), start = v2, end = v6))
      drawFacet(this, v9, v5, v4, null, Brush.linearGradient(listOf(cDiamondWhite.copy(alpha = fAlpha), cPrimary.copy(alpha = fAlpha)), start = v9, end = v5))
      drawFacet(this, v10, v5, v6, null, Brush.linearGradient(listOf(cCrystalBlue.copy(alpha = fAlpha), cDarkBlue.copy(alpha = fAlpha)), start = v10, end = v5))
      drawFacet(this, v9, v10, v5, null, Brush.linearGradient(listOf(cWhite.copy(alpha = fAlpha), cSky.copy(alpha = fAlpha)), start = v9, end = v5))

      // Pavilion Facets
      drawFacet(this, v3, v4, v8, null, Brush.linearGradient(listOf(cBlue.copy(alpha = fAlpha), cDarkBlue.copy(alpha = fAlpha)), start = v3, end = v8))
      drawFacet(this, v4, v5, v8, null, Brush.linearGradient(listOf(cSky.copy(alpha = fAlpha), cDeepBlue.copy(alpha = fAlpha)), start = v4, end = v8))
      drawFacet(this, v5, v6, v8, null, Brush.linearGradient(listOf(cCrystalBlue.copy(alpha = fAlpha), cPrimary.copy(alpha = fAlpha)), start = v5, end = v8))
      drawFacet(this, v6, v7, v8, null, Brush.linearGradient(listOf(cDiamondWhite.copy(alpha = fAlpha), cDarkBlue.copy(alpha = fAlpha)), start = v6, end = v8))

      // 5. Draw Fine White Razor-Cut Bevel Lines (Facet Outline Mesh)
      val edgeColor = cDiamondWhite.copy(alpha = 0.55f * fAlpha)
      val strokeW = 1.2f * scaleX
      drawFacetEdges(this, v1, v2, v10, v9, edgeColor, strokeW)
      drawFacetEdges(this, v1, v9, v4, v3, edgeColor, strokeW)
      drawFacetEdges(this, v2, v10, v6, v7, edgeColor, strokeW)
      drawFacetEdges(this, v9, v5, v4, null, edgeColor, strokeW)
      drawFacetEdges(this, v10, v5, v6, null, edgeColor, strokeW)
      drawFacetEdges(this, v9, v10, v5, null, edgeColor, strokeW)
      drawFacetEdges(this, v3, v4, v8, null, edgeColor, strokeW)
      drawFacetEdges(this, v4, v5, v8, null, edgeColor, strokeW)
      drawFacetEdges(this, v5, v6, v8, null, edgeColor, strokeW)
      drawFacetEdges(this, v6, v7, v8, null, edgeColor, strokeW)

      // 6. Shimmer Light Sweep (Linear gradient blend)
      val sweepOffset = (shimmerProgress * (width + height))
      val shimmerBrush = Brush.linearGradient(
        colors = listOf(
          Color.White.copy(alpha = 0f),
          Color.White.copy(alpha = 0.15f),
          Color.White.copy(alpha = 0.75f), // Mesh Highlight
          Color.White.copy(alpha = 0.15f),
          Color.White.copy(alpha = 0f)
        ),
        start = Offset(sweepOffset - 80f, sweepOffset - 80f),
        end = Offset(sweepOffset + 80f, sweepOffset + 80f)
      )

      // Clip sweep highlight strictly to the diamond's outer boundary
      drawContext.canvas.save()
      drawContext.canvas.clipPath(silhouettePath)
      drawRect(
        brush = shimmerBrush,
        blendMode = BlendMode.SrcOver
      )
      drawContext.canvas.restore()
    }

    // 7. Sparkles particles overlay
    drawSparkleParticle(this, 18f * scaleX, 32f * scaleY, 4f * scaleX, sparkle1Alpha * alpha * bloomProgress)
    drawSparkleParticle(this, 82f * scaleX, 34f * scaleY, 5f * scaleX, sparkle2Alpha * alpha * bloomProgress)
    drawSparkleParticle(this, 26f * scaleX, 74f * scaleY, 4.5f * scaleX, sparkle3Alpha * alpha * bloomProgress)
    drawSparkleParticle(this, 74f * scaleX, 70f * scaleY, 4f * scaleX, sparkle1Alpha * alpha * bloomProgress) // Re-use alpha staggered
  }
}

private fun drawFacet(
  drawScope: DrawScope,
  p1: Offset,
  p2: Offset,
  p3: Offset,
  p4: Offset?,
  brush: Brush
) {
  val path = Path().apply {
    moveTo(p1.x, p1.y)
    lineTo(p2.x, p2.y)
    lineTo(p3.x, p3.y)
    if (p4 != null) {
      lineTo(p4.x, p4.y)
    }
    close()
  }
  drawScope.drawPath(path = path, brush = brush)
}

private fun drawFacetEdges(
  drawScope: DrawScope,
  p1: Offset,
  p2: Offset,
  p3: Offset,
  p4: Offset?,
  color: Color,
  strokeWidth: Float
) {
  val path = Path().apply {
    moveTo(p1.x, p1.y)
    lineTo(p2.x, p2.y)
    lineTo(p3.x, p3.y)
    if (p4 != null) {
      lineTo(p4.x, p4.y)
    }
    close()
  }
  drawScope.drawPath(
    path = path,
    color = color,
    style = Stroke(
      width = strokeWidth,
      cap = StrokeCap.Round,
      join = StrokeJoin.Round
    )
  )
}

private fun drawSparkleParticle(
  drawScope: DrawScope,
  x: Float,
  y: Float,
  size: Float,
  alpha: Float
) {
  if (alpha <= 0.05f) return
  val path = Path().apply {
    moveTo(x, y - size)
    quadraticTo(x, y, x + size, y)
    quadraticTo(x, y, x, y + size)
    quadraticTo(x, y, x - size, y)
    quadraticTo(x, y, x, y - size)
    close()
  }
  drawScope.drawPath(
    path = path,
    color = Color.White.copy(alpha = alpha)
  )
}
