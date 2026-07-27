package com.example.features.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import kotlin.math.*

// 3D Point representation
data class Vertex3D(val x: Float, val y: Float, val z: Float)

// 3D Face representation
data class Face(val v1: Int, val v2: Int, val v3: Int, val colorType: Int)

@Composable
fun MagicDiamondSplashEngine() {
    // 1. Time state for the initial 5-second entry animation (progress: 0f to 1f)
    val entryProgress = remember { Animatable(0f) }
    
    // 2. Infinite transition for continuous luxury animations (rotation, float, breathe, sparkle)
    val infiniteTransition = rememberInfiniteTransition(label = "LuxuryAnimations")
    
    // Slow continuous time parameter in seconds
    val continuousTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ContinuousTime"
    )

    // Launch the 5-second entry animation immediately on start
    LaunchedEffect(Unit) {
        entryProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 5000, easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f))
        )
    }

    val progress = entryProgress.value

    // Animation parameter blending:
    // We want the effects to fade in smoothly as progress advances
    val introductionFactor = (progress / 1.0f).coerceIn(0f, 1f)
    
    // 1. Continuous rotation parameters (extremely slow, cinematic)
    val baseAngleY = PI.toFloat() / 6f // fixed starting angle
    val rotationYSpeed = 0.15f // very slow
    val currentAngleY = baseAngleY + (continuousTime * rotationYSpeed * introductionFactor)
    
    val baseAngleX = -PI.toFloat() / 12f // slight tilt forward to see the table facets
    val rotationXSpeed = 0.08f
    val currentAngleX = baseAngleX + (sin(continuousTime * 0.4f) * 0.12f * introductionFactor)

    // 2. Float and Breathe
    // Very gentle float (up and down) starting after the crystal is mostly formed
    val floatAnimFactor = if (progress > 0.6f) ((progress - 0.6f) / 0.4f) else 0f
    val floatOffset = sin(continuousTime * 0.8f) * 20f * floatAnimFactor

    // Very gentle breathing scale
    val breatheAnimFactor = if (progress > 0.6f) ((progress - 0.6f) / 0.4f) else 0f
    val breathingScale = 1f + (cos(continuousTime * 1.0f) * 0.035f * breatheAnimFactor)

    // Define the base brilliant-cut 3D vertices of the Althaki Jewel
    val baseVertices = remember {
        listOf(
            // 0: Top Apex / Table center
            Vertex3D(0f, -145f, 0f),
            // 1..6: Crown Ring (upper boundary, higher up and narrower)
            Vertex3D(90f * cos(0f), -55f, 90f * sin(0f)),
            Vertex3D(90f * cos(PI.toFloat() / 3f), -55f, 90f * sin(PI.toFloat() / 3f)),
            Vertex3D(90f * cos(2f * PI.toFloat() / 3f), -55f, 90f * sin(2f * PI.toFloat() / 3f)),
            Vertex3D(90f * cos(PI.toFloat()), -55f, 90f * sin(PI.toFloat())),
            Vertex3D(90f * cos(4f * PI.toFloat() / 3f), -55f, 90f * sin(4f * PI.toFloat() / 3f)),
            Vertex3D(90f * cos(5f * PI.toFloat() / 3f), -55f, 90f * sin(5f * PI.toFloat() / 3f)),
            // 7..12: Girdle Ring (middle boundary, wider, offset by 30 deg for perfect triangular mesh)
            Vertex3D(135f * cos(PI.toFloat() / 6f), 20f, 135f * sin(PI.toFloat() / 6f)),
            Vertex3D(135f * cos(3f * PI.toFloat() / 6f), 20f, 135f * sin(3f * PI.toFloat() / 6f)),
            Vertex3D(135f * cos(5f * PI.toFloat() / 6f), 20f, 135f * sin(5f * PI.toFloat() / 6f)),
            Vertex3D(135f * cos(7f * PI.toFloat() / 6f), 20f, 135f * sin(7f * PI.toFloat() / 6f)),
            Vertex3D(135f * cos(9f * PI.toFloat() / 6f), 20f, 135f * sin(9f * PI.toFloat() / 6f)),
            Vertex3D(135f * cos(11f * PI.toFloat() / 6f), 20f, 135f * sin(11f * PI.toFloat() / 6f)),
            // 13: Bottom Tip (pavilion/culet, deep bottom apex)
            Vertex3D(0f, 175f, 0f)
        )
    }

    // Define unique wireframe edge lines connecting vertices
    val edges = remember {
        listOf(
            // Crown to top apex
            Pair(0, 1), Pair(0, 2), Pair(0, 3), Pair(0, 4), Pair(0, 5), Pair(0, 6),
            // Crown ring itself
            Pair(1, 2), Pair(2, 3), Pair(3, 4), Pair(4, 5), Pair(5, 6), Pair(6, 1),
            // Middle band connections
            Pair(1, 7), Pair(2, 7), Pair(2, 8), Pair(3, 8), Pair(3, 9), Pair(4, 9),
            Pair(4, 10), Pair(5, 10), Pair(5, 11), Pair(6, 11), Pair(6, 12), Pair(1, 12),
            // Girdle ring itself
            Pair(7, 8), Pair(8, 9), Pair(9, 10), Pair(10, 11), Pair(11, 12), Pair(12, 7),
            // Girdle to bottom tip
            Pair(13, 7), Pair(13, 8), Pair(13, 9), Pair(13, 10), Pair(13, 11), Pair(13, 12)
        )
    }

    // Define faces (facets)
    val faces = remember {
        listOf(
            // Crown triangles
            Face(0, 1, 2, 0),
            Face(0, 2, 3, 1),
            Face(0, 3, 4, 0),
            Face(0, 4, 5, 1),
            Face(0, 5, 6, 0),
            Face(0, 6, 1, 1),

            // Middle band downward triangles
            Face(1, 2, 7, 2),
            Face(2, 3, 8, 3),
            Face(3, 4, 9, 2),
            Face(4, 5, 10, 3),
            Face(5, 6, 11, 2),
            Face(6, 1, 12, 3),

            // Middle band upward triangles
            Face(2, 7, 8, 4),
            Face(3, 8, 9, 5),
            Face(4, 9, 10, 4),
            Face(5, 10, 11, 5),
            Face(6, 11, 12, 4),
            Face(1, 12, 7, 5),

            // Lower pavilion triangles
            Face(13, 7, 8, 6),
            Face(13, 8, 9, 7),
            Face(13, 9, 10, 6),
            Face(13, 10, 11, 7),
            Face(13, 11, 12, 6),
            Face(13, 12, 7, 7)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030406)) // Luxury deep space black as base background
            .testTag("magic_diamond_splash")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val screenWidth = size.width
            val screenHeight = size.height
            val centerX = screenWidth / 2f
            val centerY = (screenHeight / 2f) + floatOffset // center point with floating offset

            // Determine scale dynamically to ensure it is always perfectly visible
            val baseScaleFactor = (min(screenWidth, screenHeight) / 3.4f).coerceIn(150f, 320f)
            val scale = baseScaleFactor * breathingScale

            // Draw Luxury Cosmic Radial Gradient Background centered behind the Jewel
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0C1322), // Soft dark cosmic navy
                        Color(0xFF05070D), // Deeper space
                        Color(0xFF020204)  // Pitch black borders
                    ),
                    center = Offset(centerX, centerY),
                    radius = max(screenWidth, screenHeight) * 0.75f
                )
            )

            // --- STAGE 1 & 2: Center Core Light Flare / Nebula ---
            // A glowing energy flare that starts small and intensifies, then sits inside the jewel
            val flareProgress = (progress / 0.35f).coerceIn(0f, 1f)
            val flareSizeFactor = if (progress <= 0.35f) {
                // expanding core
                0.2f + 0.8f * flareProgress
            } else {
                // pulsing inside
                1f + 0.1f * sin(continuousTime * 1.5f)
            }
            
            val flareOpacity = if (progress <= 0.35f) {
                flareProgress * 0.85f
            } else {
                // remain as an intense ambient inner glow
                0.4f + 0.15f * cos(continuousTime * 1.0f)
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF).copy(alpha = flareOpacity),
                        Color(0xFF38BDF8).copy(alpha = flareOpacity * 0.6f),
                        Color(0xFF6366F1).copy(alpha = flareOpacity * 0.25f),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = 160f * flareSizeFactor
                ),
                radius = 160f * flareSizeFactor
            )

            // 3D Projection Engine
            // Projects 3D base vertices to 2D screen coordinates based on current yaw (Y) and pitch (X)
            val projectedPoints = baseVertices.map { vertex ->
                // Yaw (Y axis rotation)
                val cosY = cos(currentAngleY)
                val sinY = sin(currentAngleY)
                val x1 = vertex.x * cosY - vertex.z * sinY
                val z1 = vertex.x * sinY + vertex.z * cosY

                // Pitch (X axis rotation)
                val cosX = cos(currentAngleX)
                val sinX = sin(currentAngleX)
                val y2 = vertex.y * cosX - z1 * sinX
                val z2 = vertex.y * sinX + z1 * cosX

                // Simple perspective projection with comfortable camera depth
                val d = 600f
                val perspectiveFactor = d / (d + z2)

                val screenX = centerX + x1 * scale * perspectiveFactor
                val screenY = centerY + y2 * scale * perspectiveFactor

                // Store both projected offset and actual rotated Z (z2) for Painter's Algorithm sorting
                Pair(Offset(screenX, screenY), z2)
            }

            // --- STAGE 3: Crystal Faces Solidifying ---
            // Faces start appearing from progress 0.45 to 0.75.
            // We use the Painter's Algorithm: Sort faces from back to front (largest Z to smallest Z) to draw translucency perfectly.
            if (progress >= 0.40f) {
                // Map each face to its vertices and average Z depth
                val faceDepths = faces.mapIndexed { index, face ->
                    val p1 = projectedPoints[face.v1]
                    val p2 = projectedPoints[face.v2]
                    val p3 = projectedPoints[face.v3]
                    val avgZ = (p1.second + p2.second + p3.second) / 3f
                    Triple(index, face, avgZ)
                }.sortedByDescending { it.third } // Draw largest Z (furthest back) first

                // Draw each face
                faceDepths.forEach { (_, face, avgZ) ->
                    val p1 = projectedPoints[face.v1].first
                    val p2 = projectedPoints[face.v2].first
                    val p3 = projectedPoints[face.v3].first

                    // Determine normal direction (facing front vs back in 2D space)
                    // 2D cross product of vectors (p2 - p1) and (p3 - p1)
                    val normalZ = (p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x)
                    val isFrontFacing = normalZ > 0

                    // Average height of the face to stagger crystallization downwards
                    val y1_raw = baseVertices[face.v1].y
                    val y2_raw = baseVertices[face.v2].y
                    val y3_raw = baseVertices[face.v3].y
                    val avgYRaw = (y1_raw + y2_raw + y3_raw) / 3f
                    
                    // Normalize average Y from top (-145) to bottom (175) to [0, 1] range
                    val normalizedY = ((avgYRaw - (-145f)) / (175f - (-145f))).coerceIn(0f, 1f)

                    // Stagger calculation for this face
                    val faceStartProgress = 0.40f + 0.15f * normalizedY
                    val faceEndProgress = faceStartProgress + 0.20f
                    val faceCrystallizeProgress = ((progress - faceStartProgress) / (faceEndProgress - faceStartProgress)).coerceIn(0f, 1f)

                    if (faceCrystallizeProgress > 0f) {
                        // Color schemes for different facet groupings
                        val (colorStart, colorEnd) = when (face.colorType) {
                            0, 1 -> Pair(Color(0xFF2563EB), Color(0xFF1D4ED8)) // Top Crown Sapphire
                            2, 3 -> Pair(Color(0xFF06B6D4), Color(0xFF0891B2)) // Girdle upper Teal/Cyan
                            4, 5 -> Pair(Color(0xFF6366F1), Color(0xFF4F46E5)) // Girdle lower Indigo
                            else -> Pair(Color(0xFF7C3AED), Color(0xFF6D28D9)) // Pavilion Violet-Amethyst
                        }

                        // Determine final opacity. Back-facing facets are drawn with very low opacity (glass refraction),
                        // front-facing facets are drawn with a rich, shimmering opacity.
                        val baseAlpha = if (isFrontFacing) 0.32f else 0.08f
                        val faceAlpha = baseAlpha * faceCrystallizeProgress

                        // Shimmer effect on faces - dynamically changes depending on rotation (reflection)
                        val reflectionFactor = (cos(currentAngleY + face.colorType * (PI.toFloat() / 4f)) * 0.4f + 0.6f)
                        val shimmerColorStart = colorStart.copy(alpha = faceAlpha * reflectionFactor)
                        val shimmerColorEnd = colorEnd.copy(alpha = faceAlpha * 0.4f)

                        // Light highlight (specular reflection) for front-facing facets
                        val highlightsBlend = if (isFrontFacing) {
                            val highlightIntensity = (sin(currentAngleY * 2f + currentAngleX + face.colorType) * 0.5f + 0.5f)
                            if (highlightIntensity > 0.7f) {
                                Color.White.copy(alpha = 0.25f * (highlightIntensity - 0.7f) / 0.3f * faceCrystallizeProgress)
                            } else Color.Transparent
                        } else Color.Transparent

                        val path = Path().apply {
                            moveTo(p1.x, p1.y)
                            lineTo(p2.x, p2.y)
                            lineTo(p3.x, p3.y)
                            close()
                        }

                        // Draw the base gradient facet
                        drawPath(
                            path = path,
                            brush = Brush.linearGradient(
                                colors = listOf(shimmerColorStart, shimmerColorEnd),
                                start = p1,
                                end = p3
                            )
                        )

                        // Draw specular highlight overlay
                        if (highlightsBlend != Color.Transparent) {
                            drawPath(
                                path = path,
                                color = highlightsBlend
                            )
                        }
                    }
                }
            }

            // --- STAGE 2: Crystal Lines Self-Assembling ---
            // Wireframe lines start drawing from progress 0.15 to 0.55.
            if (progress >= 0.15f) {
                edges.forEach { (u, v) ->
                    val p1 = projectedPoints[u].first
                    val p2 = projectedPoints[v].first

                    // Always animate line from the higher vertex (smaller base Y) to the lower vertex
                    val (startPoint, endPoint) = if (baseVertices[u].y <= baseVertices[v].y) {
                        Pair(p1, p2)
                    } else {
                        Pair(p2, p1)
                    }

                    // Height calculation to stagger the wireframe build-up downwards
                    val avgYRaw = (baseVertices[u].y + baseVertices[v].y) / 2f
                    val normalizedY = ((avgYRaw - (-145f)) / (175f - (-145f))).coerceIn(0f, 1f)

                    // Stagger calculation for this specific edge
                    val lineStartProgress = 0.15f + 0.20f * normalizedY
                    val lineEndProgress = lineStartProgress + 0.15f
                    val lineAssemblyProgress = ((progress - lineStartProgress) / (lineEndProgress - lineStartProgress)).coerceIn(0f, 1f)

                    if (lineAssemblyProgress > 0f) {
                        // Calculate current line end position
                        val currentEnd = Offset(
                            x = startPoint.x + (endPoint.x - startPoint.x) * lineAssemblyProgress,
                            y = startPoint.y + (endPoint.y - startPoint.y) * lineAssemblyProgress
                        )

                        // Double-stroke drawing to create a premium glowing crystal wireframe
                        // 1. Thick soft background glow (Cyan/Indigo)
                        drawLinearLine(
                            start = startPoint,
                            end = currentEnd,
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.35f), Color(0xFF6366F1).copy(alpha = 0.35f)),
                                start = startPoint,
                                end = endPoint
                            ),
                            strokeWidth = 6f
                        )

                        // 2. Core bright crisp line (Pure Brilliant White-Cyan)
                        drawLinearLine(
                            start = startPoint,
                            end = currentEnd,
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFFFFFFF).copy(alpha = 0.95f), Color(0xFFE0F2FE).copy(alpha = 0.90f)),
                                start = startPoint,
                                end = endPoint
                            ),
                            strokeWidth = 2f
                        )
                    }
                }
            }

            // --- STAGE 4: Luxury Glimmer / Sparkle Overlay ---
            // High-end cinematic sparkles flashing at specific major vertices (apex, crown, girdle)
            if (progress >= 0.70f) {
                val sparkleFactor = ((progress - 0.70f) / 0.30f).coerceIn(0f, 1f)
                
                // Define 4 coordinates to sparkle, mapped from 3D vertices (0, 2, 5, 10)
                val sparkleVertices = listOf(0, 2, 5, 10)
                sparkleVertices.forEachIndexed { i, vertexIndex ->
                    val vertexPos = projectedPoints[vertexIndex].first
                    
                    // Modulate sparkle intensity with different phase offsets
                    val waveValue = sin(continuousTime * 3f + i * 1.6f) * 0.5f + 0.5f
                    val sparkleIntensity = waveValue * sparkleFactor
                    
                    if (sparkleIntensity > 0.1f) {
                        val sparkleSize = 14f + 16f * sparkleIntensity
                        val sparkleAlpha = sparkleIntensity * 0.85f
                        
                        // Draw a luxury 4-pointed star sparkle
                        drawSparkleStar(
                            center = vertexPos,
                            size = sparkleSize,
                            alpha = sparkleAlpha
                        )
                    }
                }
            }
        }
    }
}

// Extension function to draw a line with a brush
fun DrawScope.drawLinearLine(start: Offset, end: Offset, brush: Brush, strokeWidth: Float) {
    drawLine(
        brush = brush,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}

// Helper to draw a luxury 4-pointed star sparkle (bezier curves from ends to center)
fun DrawScope.drawSparkleStar(center: Offset, size: Float, alpha: Float) {
    val path = Path().apply {
        // Top tip
        moveTo(center.x, center.y - size)
        // Curve to right tip
        quadraticTo(center.x, center.y, center.x + size, center.y)
        // Curve to bottom tip
        quadraticTo(center.x, center.y, center.x, center.y + size)
        // Curve to left tip
        quadraticTo(center.x, center.y, center.x - size, center.y)
        // Curve back to top tip
        quadraticTo(center.x, center.y, center.x, center.y - size)
        close()
    }
    
    // Draw the sharp elegant star
    drawPath(
        path = path,
        color = Color.White.copy(alpha = alpha)
    )
    
    // Draw a small soft circular core for intense shine
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = alpha), Color.Transparent),
            center = center,
            radius = size * 0.3f
        ),
        radius = size * 0.3f,
        center = center
    )
}
