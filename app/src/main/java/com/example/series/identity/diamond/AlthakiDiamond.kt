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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sin

/**
 * AlthakiDiamond - The Premium 3D Crystal Gem of the "Althaki" Series.
 * Rendered entirely via procedural Compose Canvas APIs, Paths, and Gradients.
 * No PNGs, GIFs, Lottie, or Vector Assets. Perfect 60FPS luxury diamond.
 *
 * It features:
 * - 12-faceted 3D geometry with realistic glass refractions and specular highlights
 * - Soft breathing glow in the background
 * - Real-time animated diagonal Shimmer sweep (specular highlight)
 * - Ground reflection with reduced vertical scale and high opacity fading
 * - Twin staggered sparkles twinkling around the jewel
 * - Gentle sinusoidal floating motion on the Y-axis
 */
@Composable
fun AlthakiDiamond(
  modifier: Modifier = Modifier,
  sizeDp: Dp = 180.dp,
  bloomProgress: Float = 1f, // 0f to 1f for the materialization effect
  shimmerProgress: Float = 0f, // 0f to 1f for the manual diagonal shine sweep
  sparkleAlpha: Float = 0f, // 0f to 1f for the final announcement flash
  alpha: Float = 1f
) {
  val infiniteTransition = rememberInfiniteTransition(label = "AlthakiDiamondTransition")

  // 1. Glow Breathing animation (modulates the backing glow's opacity and radius)
  val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.40f,
    targetValue = 0.70f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 4500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "GlowBreathing"
  )

  // 2. Float translation is completely disabled for luxury stability (no floating offset)
  val floatY = 0f

  // 3. Staggered Sparkle Particles alphas
  val sparkleA by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = keyframes {
        durationMillis = 2000
        0f at 0
        1f at 500
        0f at 1000
        0f at 2000
      },
      repeatMode = RepeatMode.Restart
    ),
    label = "SparkleA"
  )

  val sparkleB by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = keyframes {
        durationMillis = 2400
        0f at 0
        0f at 800
        1f at 1400
        0f at 2000
        0f at 2400
      },
      repeatMode = RepeatMode.Restart
    ),
    label = "SparkleB"
  )

  val sparkleC by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = keyframes {
        durationMillis = 2800
        0f at 0
        0f at 1200
        1f at 1900
        0f at 2500
        0f at 2800
      },
      repeatMode = RepeatMode.Restart
    ),
    label = "SparkleC"
  )

  // Compose Canvas rendering
  Canvas(
    modifier = modifier.size(sizeDp)
  ) {
    val width = size.width
    val height = size.height

    // Scale factors mapping virtual 100x100 design grid to actual local pixels
    val scaleX = width / 100f
    val scaleY = height / 100f

    // Center coordinates
    val cx = 50f * scaleX
    val cy = 46f * scaleY

    // Apply floating offset to everything except the backing glow and reflection
    val finalYOffset = floatY * scaleY

    // Luxury Color Palette Definitions
    val cWhite = Color(0xFFFFFFFF)
    val cDiamondWhite = Color(0xFFEAF8FF)
    val cCrystalBlue = Color(0xFFD8F4FF)
    val cSky = Color(0xFF7DD3FC)
    val cBlue = Color(0xFF38BDF8)
    val cPrimary = Color(0xFF0EA5E9)
    val cDeepBlue = Color(0xFF0284C7)
    val cDarkBlue = Color(0xFF0369A1)

    // ----------------------------------------------------
    // BACKGROUND GLOW (Dynamic Soft Radial Gradient)
    // ----------------------------------------------------
    val glowRadius = 75f * scaleX * (0.85f + 0.15f * bloomProgress)
    val glowBrush = Brush.radialGradient(
      colors = listOf(
        cDiamondWhite.copy(alpha = 0.45f * glowAlpha * alpha * bloomProgress),
        cCrystalBlue.copy(alpha = 0.20f * glowAlpha * alpha * bloomProgress),
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

    // ----------------------------------------------------
    // GEOMETRIC VERTICES OF THE DIAMOND (12 facets total)
    // ----------------------------------------------------
    // Girdle points (middle edge)
    val gLeft = Offset(12f * scaleX, 48f * scaleY + finalYOffset)
    val gMidLeft = Offset(31f * scaleX, 50f * scaleY + finalYOffset)
    val gCenter = Offset(50f * scaleX, 51f * scaleY + finalYOffset)
    val gMidRight = Offset(69f * scaleX, 50f * scaleY + finalYOffset)
    val gRight = Offset(88f * scaleX, 48f * scaleY + finalYOffset)

    // Upper Crown points (Table flat top)
    val tLeft = Offset(34f * scaleX, 26f * scaleY + finalYOffset)
    val tRight = Offset(66f * scaleX, 26f * scaleY + finalYOffset)
    val tMidLeft = Offset(41f * scaleX, 36f * scaleY + finalYOffset)
    val tMidRight = Offset(59f * scaleX, 36f * scaleY + finalYOffset)

    // Pavilion Culet (Bottom tip point)
    val pCulet = Offset(50f * scaleX, 86f * scaleY + finalYOffset)

    // Outer silhouette path for clipping / outline
    val silhouettePath = Path().apply {
      moveTo(tLeft.x, tLeft.y)
      lineTo(tRight.x, tRight.y)
      lineTo(gRight.x, gRight.y)
      lineTo(pCulet.x, pCulet.y)
      lineTo(gLeft.x, gLeft.y)
      close()
    }

    // ----------------------------------------------------
    // GROUND REFLECTION EFFECT (Vertically flipped & low opacity)
    // ----------------------------------------------------
    withTransform({
      translate(top = height * 0.40f)
      scale(scaleX = 0.95f, scaleY = -0.22f)
    }) {
      val refAlpha = 0.14f * alpha * bloomProgress

      // Mirrors the top table & crown facets in reflection
      drawFacet(this, tLeft, tRight, tMidRight, tMidLeft, Brush.verticalGradient(listOf(cWhite.copy(alpha = refAlpha), cCrystalBlue.copy(alpha = refAlpha))))
      drawFacet(this, tLeft, tMidLeft, gMidLeft, null, Brush.verticalGradient(listOf(cDiamondWhite.copy(alpha = refAlpha), cBlue.copy(alpha = refAlpha))))
      drawFacet(this, tLeft, gLeft, gMidLeft, null, Brush.verticalGradient(listOf(cSky.copy(alpha = refAlpha), cPrimary.copy(alpha = refAlpha))))
      drawFacet(this, tRight, tMidRight, gMidRight, null, Brush.verticalGradient(listOf(cCrystalBlue.copy(alpha = refAlpha), cDeepBlue.copy(alpha = refAlpha))))
      drawFacet(this, tRight, gRight, gMidRight, null, Brush.verticalGradient(listOf(cBlue.copy(alpha = refAlpha), cDarkBlue.copy(alpha = refAlpha))))
      drawFacet(this, tMidLeft, tMidRight, gCenter, null, Brush.verticalGradient(listOf(cWhite.copy(alpha = refAlpha), cSky.copy(alpha = refAlpha))))
      drawFacet(this, tMidLeft, gMidLeft, gCenter, null, Brush.verticalGradient(listOf(cCrystalBlue.copy(alpha = refAlpha), cBlue.copy(alpha = refAlpha))))
      drawFacet(this, tMidRight, gMidRight, gCenter, null, Brush.verticalGradient(listOf(cDiamondWhite.copy(alpha = refAlpha), cPrimary.copy(alpha = refAlpha))))

      // Mirrors bottom pavilion facets in reflection
      drawFacet(this, gLeft, gMidLeft, pCulet, null, Brush.verticalGradient(listOf(cPrimary.copy(alpha = refAlpha), cDarkBlue.copy(alpha = refAlpha))))
      drawFacet(this, gMidLeft, gCenter, pCulet, null, Brush.verticalGradient(listOf(cSky.copy(alpha = refAlpha), cDeepBlue.copy(alpha = refAlpha))))
      drawFacet(this, gCenter, gMidRight, pCulet, null, Brush.verticalGradient(listOf(cCrystalBlue.copy(alpha = refAlpha), cPrimary.copy(alpha = refAlpha))))
      drawFacet(this, gMidRight, gRight, pCulet, null, Brush.verticalGradient(listOf(cBlue.copy(alpha = refAlpha), cDarkBlue.copy(alpha = refAlpha))))
    }

    // ----------------------------------------------------
    // PROGRESSIVE MATERIALIZATION CALCULATOR & STAGGER ENGINE
    // ----------------------------------------------------
    fun getLocalAlpha(start: Float, end: Float): Float {
      if (bloomProgress < start) return 0f
      if (bloomProgress >= end) return alpha
      return ((bloomProgress - start) / (end - start)) * alpha
    }

    // 0% - 10%: Draw point of light in the center
    if (bloomProgress < 0.35f) {
      val lightAlpha = if (bloomProgress < 0.10f) {
        // 0% to 10%: intensity rises from 0.3f to 1.0f
        0.3f + (bloomProgress / 0.10f) * 0.7f
      } else {
        // 10% to 35%: slowly fade out and integrate into the crystal structures
        1.0f - ((bloomProgress - 0.10f) / 0.25f)
      }
      val lightRadius = if (bloomProgress < 0.10f) {
        (2.5f + (bloomProgress / 0.10f) * 6.5f) * scaleX
      } else {
        (9.0f + ((bloomProgress - 0.10f) / 0.25f) * 11.0f) * scaleX
      }

      drawCircle(
        color = Color.White.copy(alpha = lightAlpha * alpha),
        radius = lightRadius,
        center = Offset(cx, cy)
      )
    }

    // 20% - 55%: Delicate crystal wireframe lines (subtle lines build up first)
    // 55% - 90%: Crisp solid crystal borders/edges
    val wireframeAlpha = getLocalAlpha(0.20f, 0.55f) * 0.25f
    val solidBorderAlpha = getLocalAlpha(0.55f, 0.90f) * 0.70f
    val edgeColor = cWhite.copy(alpha = (wireframeAlpha + solidBorderAlpha).coerceIn(0f, 1f))
    val edgeW = 1.2f * scaleX

    // 35% - 75%: Facet Color Fills Materializing Facet-by-Facet (Staggered progression)
    // Table (Top Flat Face): 35% - 45%
    val f1Alpha = getLocalAlpha(0.35f, 0.45f)
    // Crown Left & Right: 40% - 50%
    val f2Alpha = getLocalAlpha(0.40f, 0.50f)
    val f4Alpha = getLocalAlpha(0.40f, 0.50f)
    // Crown Far Left & Far Right: 45% - 55%
    val f3Alpha = getLocalAlpha(0.45f, 0.55f)
    val f5Alpha = getLocalAlpha(0.45f, 0.55f)
    // Crown Center-Top & Mid Left / Right: 50% - 65%
    val f6Alpha = getLocalAlpha(0.50f, 0.65f)
    val f7Alpha = getLocalAlpha(0.50f, 0.65f)
    val f8Alpha = getLocalAlpha(0.50f, 0.65f)
    // Pavilion Bottom Facets: 55% - 75%
    val f9Alpha = getLocalAlpha(0.55f, 0.70f)
    val f10Alpha = getLocalAlpha(0.57f, 0.72f)
    val f11Alpha = getLocalAlpha(0.59f, 0.74f)
    val f12Alpha = getLocalAlpha(0.61f, 0.75f)

    // 1. Table Face (Top flat Octagon/Quad)
    drawFacet(this, tLeft, tRight, tMidRight, tMidLeft, Brush.linearGradient(
      colors = listOf(cWhite.copy(alpha = f1Alpha), cDiamondWhite.copy(alpha = f1Alpha)),
      start = tLeft, end = tMidRight
    ))

    // 2. Crown Left facet
    drawFacet(this, tLeft, tMidLeft, gMidLeft, null, Brush.linearGradient(
      colors = listOf(cWhite.copy(alpha = f2Alpha), cBlue.copy(alpha = f2Alpha)),
      start = tLeft, end = gMidLeft
    ))

    // 3. Crown Far Left facet
    drawFacet(this, tLeft, gLeft, gMidLeft, null, Brush.linearGradient(
      colors = listOf(cSky.copy(alpha = f3Alpha), cPrimary.copy(alpha = f3Alpha)),
      start = tLeft, end = gLeft
    ))

    // 4. Crown Right facet
    drawFacet(this, tRight, tMidRight, gMidRight, null, Brush.linearGradient(
      colors = listOf(cDiamondWhite.copy(alpha = f4Alpha), cSky.copy(alpha = f4Alpha)),
      start = tRight, end = gMidRight
    ))

    // 5. Crown Far Right facet
    drawFacet(this, tRight, gRight, gMidRight, null, Brush.linearGradient(
      colors = listOf(cPrimary.copy(alpha = f5Alpha), cDeepBlue.copy(alpha = f5Alpha)),
      start = tRight, end = gRight
    ))

    // 6. Crown Center-Top facet
    drawFacet(this, tMidLeft, tMidRight, gCenter, null, Brush.linearGradient(
      colors = listOf(cWhite.copy(alpha = f6Alpha), cSky.copy(alpha = f6Alpha)),
      start = tMidLeft, end = gCenter
    ))

    // 7. Crown Mid-Left facet
    drawFacet(this, tMidLeft, gMidLeft, gCenter, null, Brush.linearGradient(
      colors = listOf(cCrystalBlue.copy(alpha = f7Alpha), cBlue.copy(alpha = f7Alpha)),
      start = tMidLeft, end = gCenter
    ))

    // 8. Crown Mid-Right facet
    drawFacet(this, tMidRight, gMidRight, gCenter, null, Brush.linearGradient(
      colors = listOf(cDiamondWhite.copy(alpha = f8Alpha), cPrimary.copy(alpha = f8Alpha)),
      start = tMidRight, end = gCenter
    ))

    // 9. Pavilion Far Left facet
    drawFacet(this, gLeft, gMidLeft, pCulet, null, Brush.linearGradient(
      colors = listOf(cPrimary.copy(alpha = f9Alpha), cDarkBlue.copy(alpha = f9Alpha)),
      start = gLeft, end = pCulet
    ))

    // 10. Pavilion Mid Left facet
    drawFacet(this, gMidLeft, gCenter, pCulet, null, Brush.linearGradient(
      colors = listOf(cSky.copy(alpha = f10Alpha), cDeepBlue.copy(alpha = f10Alpha)),
      start = gMidLeft, end = pCulet
    ))

    // 11. Pavilion Mid Right facet
    drawFacet(this, gCenter, gMidRight, pCulet, null, Brush.linearGradient(
      colors = listOf(cCrystalBlue.copy(alpha = f11Alpha), cPrimary.copy(alpha = f11Alpha)),
      start = gCenter, end = pCulet
    ))

    // 12. Pavilion Far Right facet
    drawFacet(this, gMidRight, gRight, pCulet, null, Brush.linearGradient(
      colors = listOf(cBlue.copy(alpha = f12Alpha), cDarkBlue.copy(alpha = f12Alpha)),
      start = gMidRight, end = pCulet
    ))

    // Draw Bevel Edge outlines (only if visible progress has reached active line stages)
    if (bloomProgress >= 0.20f) {
      drawFacetEdges(this, tLeft, tRight, tMidRight, tMidLeft, edgeColor, edgeW)
      drawFacetEdges(this, tLeft, tMidLeft, gMidLeft, null, edgeColor, edgeW)
      drawFacetEdges(this, tLeft, gLeft, gMidLeft, null, edgeColor, edgeW)
      drawFacetEdges(this, tRight, tMidRight, gMidRight, null, edgeColor, edgeW)
      drawFacetEdges(this, tRight, gRight, gMidRight, null, edgeColor, edgeW)
      drawFacetEdges(this, tMidLeft, tMidRight, gCenter, null, edgeColor, edgeW)
      drawFacetEdges(this, tMidLeft, gMidLeft, gCenter, null, edgeColor, edgeW)
      drawFacetEdges(this, tMidRight, gMidRight, gCenter, null, edgeColor, edgeW)
      drawFacetEdges(this, gLeft, gMidLeft, pCulet, null, edgeColor, edgeW)
      drawFacetEdges(this, gMidLeft, gCenter, pCulet, null, edgeColor, edgeW)
      drawFacetEdges(this, gCenter, gMidRight, pCulet, null, edgeColor, edgeW)
      drawFacetEdges(this, gMidRight, gRight, pCulet, null, edgeColor, edgeW)
    }

    // 90% - 100%: Internal crystal reflections and sparkles appear
    val reflectionAndSparkleAlpha = getLocalAlpha(0.90f, 1.0f)

    if (reflectionAndSparkleAlpha > 0f) {
      // ----------------------------------------------------
      // SHIMMER SPECULAR REFLECTION SWEEP ( Diagonal light sliding )
      // ----------------------------------------------------
      val shimmerOffset = shimmerProgress * (width + height * 1.5f) - (width * 0.4f)
      val shimmerBrush = Brush.linearGradient(
        colors = listOf(
          Color.White.copy(alpha = 0f),
          Color.White.copy(alpha = 0.15f * reflectionAndSparkleAlpha),
          Color.White.copy(alpha = 0.82f * reflectionAndSparkleAlpha), // Core gleam highlight
          Color.White.copy(alpha = 0.15f * reflectionAndSparkleAlpha),
          Color.White.copy(alpha = 0f)
        ),
        start = Offset(shimmerOffset, shimmerOffset - 40f * scaleY),
        end = Offset(shimmerOffset + 50f * scaleX, shimmerOffset + 40f * scaleY)
      )

      // Clip shimmer strictly to the diamond's outer borders
      drawContext.canvas.save()
      drawContext.canvas.clipPath(silhouettePath)
      drawRect(
        brush = shimmerBrush,
        blendMode = BlendMode.SrcOver
      )
      drawContext.canvas.restore()

      // ----------------------------------------------------
      // TWINKLING SPARKLES (Magic glass highlights)
      // ----------------------------------------------------
      drawSparkle(this, 16f * scaleX, 28f * scaleY + finalYOffset, 4.5f * scaleX, sparkleA * reflectionAndSparkleAlpha)
      drawSparkle(this, 84f * scaleX, 32f * scaleY + finalYOffset, 5f * scaleX, sparkleB * reflectionAndSparkleAlpha)
      drawSparkle(this, 22f * scaleX, 70f * scaleY + finalYOffset, 4f * scaleX, sparkleC * reflectionAndSparkleAlpha)
      drawSparkle(this, 78f * scaleX, 64f * scaleY + finalYOffset, 4.5f * scaleX, sparkleA * reflectionAndSparkleAlpha)
    }

    // ----------------------------------------------------
    // GRAND FINAL ANNOUNCEMENT SPARKLE (The cue to reveal text)
    // ----------------------------------------------------
    if (sparkleAlpha > 0f) {
      drawSparkle(this, 41f * scaleX, 36f * scaleY + finalYOffset, 12f * scaleX, sparkleAlpha * alpha)
    }
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

private fun drawSparkle(
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
      center = Offset(54f * scaleX, 54f * scaleY),
      style = Stroke(width = 2.5f * scaleX)
    )

    // 2. Thin Inner Circular Ring (Radius 26)
    drawCircle(
      color = color,
      radius = 26f * scaleX,
      center = Offset(54f * scaleX, 54f * scaleY),
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
    drawLine(color = color, start = Offset(45f * scaleX, 34f * scaleY), end = Offset(48f * scaleX, 38f * scaleY), strokeWidth = 1.5f * scaleX, cap = StrokeCap.Round)
    drawLine(color = color, start = Offset(63f * scaleX, 34f * scaleY), end = Offset(60f * scaleX, 38f * scaleY), strokeWidth = 1.5f * scaleX, cap = StrokeCap.Round)
    drawLine(color = color, start = Offset(68f * scaleX, 44f * scaleY), end = Offset(63f * scaleX, 44f * scaleY), strokeWidth = 1.5f * scaleX, cap = StrokeCap.Round)
    drawLine(color = color, start = Offset(54f * scaleX, 58f * scaleY), end = Offset(54f * scaleX, 53f * scaleY), strokeWidth = 1.5f * scaleX, cap = StrokeCap.Round)
    drawLine(color = color, start = Offset(40f * scaleX, 44f * scaleY), end = Offset(45f * scaleX, 44f * scaleY), strokeWidth = 1.5f * scaleX, cap = StrokeCap.Round)

    // Facet Lines Inner to Center
    drawLine(color = color, start = Offset(48f * scaleX, 38f * scaleY), end = Offset(51f * scaleX, 41.5f * scaleY), strokeWidth = 0.8f * scaleX, cap = StrokeCap.Round)
    drawLine(color = color, start = Offset(60f * scaleX, 38f * scaleY), end = Offset(57f * scaleX, 41.5f * scaleY), strokeWidth = 0.8f * scaleX, cap = StrokeCap.Round)
    drawLine(color = color, start = Offset(63f * scaleX, 44f * scaleY), end = Offset(58.5f * scaleX, 44f * scaleY), strokeWidth = 0.8f * scaleX, cap = StrokeCap.Round)
    drawLine(color = color, start = Offset(54f * scaleX, 53f * scaleY), end = Offset(54f * scaleX, 48f * scaleY), strokeWidth = 0.8f * scaleX, cap = StrokeCap.Round)
    drawLine(color = color, start = Offset(45f * scaleX, 44f * scaleY), end = Offset(49.5f * scaleX, 44f * scaleY), strokeWidth = 0.8f * scaleX, cap = StrokeCap.Round)

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
      start = Offset(50f * scaleX, 70f * scaleY),
      end = Offset(58f * scaleX, 70f * scaleY),
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
      start = Offset(54.5f * scaleX, 74f * scaleY),
      end = Offset(58.5f * scaleX, 74f * scaleY),
      strokeWidth = 1.4f * scaleX,
      cap = StrokeCap.Round
    )
  }
}
