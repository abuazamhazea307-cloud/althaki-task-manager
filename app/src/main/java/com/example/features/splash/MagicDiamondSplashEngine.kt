package com.example.features.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlin.math.*

// 3D Vertex representation
private data class Vertex3D(val x: Float, val y: Float, val z: Float)

// 3D Face representation
private data class Face(val v1: Int, val v2: Int, val v3: Int, val colorType: Int)

@Composable
fun MagicDiamondSplashEngine() {
    val infiniteTransition = rememberInfiniteTransition(label = "SplashTransition")

    // Very slow, extremely smooth 360-degree rotation around Y axis (15-second cycle for 60FPS precision)
    val rotationY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationY"
    )

    // Extremely stable, slight tilt around X axis (constant -0.25f, no wobble/shaking)
    val rotationX = -0.25f

    // Dynamic light sweep factor passing from left to right over the facets (3.2-second cycle)
    val lightSweepFactor by infiniteTransition.animateFloat(
        initialValue = -2.0f,
        targetValue = 2.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "lightSweep"
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("magic_diamond_splash_surface"),
        color = Color(0xFF030406) // Luxury deep space black background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. Fully animated 3D Crystal Diamond
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .testTag("diamond_geometric_container"),
                contentAlignment = Alignment.Center
            ) {
                DiamondGeometricModel(
                    rotationY = rotationY,
                    rotationX = rotationX,
                    lightSweepFactor = lightSweepFactor
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Luxury Task Manager Document and Checkmark Logo (matching design style)
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .testTag("luxury_task_logo_container"),
                contentAlignment = Alignment.Center
            ) {
                LuxuryDocumentCheckModel()
            }
        }
    }
}

@Composable
private fun DiamondGeometricModel(
    rotationY: Float,
    rotationX: Float,
    lightSweepFactor: Float
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .testTag("diamond_geometric_canvas")
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        // scale sized beautifully within canvas (approx 40% of the smallest screen dimension)
        val baseScale = min(size.width, size.height) * 0.40f
        val scale = baseScale

        // Symmetrical brilliant cut diamond model vertices
        val vertices = listOf(
            // 0: Top Table Center
            Vertex3D(0f, -145f, 0f),
            // 1..6: Crown Ring (upper boundary)
            Vertex3D(90f * cos(0f), -55f, 90f * sin(0f)),
            Vertex3D(90f * cos(PI.toFloat() / 3f), -55f, 90f * sin(PI.toFloat() / 3f)),
            Vertex3D(90f * cos(2f * PI.toFloat() / 3f), -55f, 90f * sin(2f * PI.toFloat() / 3f)),
            Vertex3D(90f * cos(PI.toFloat()), -55f, 90f * sin(PI.toFloat())),
            Vertex3D(90f * cos(4f * PI.toFloat() / 3f), -55f, 90f * sin(4f * PI.toFloat() / 3f)),
            Vertex3D(90f * cos(5f * PI.toFloat() / 3f), -55f, 90f * sin(5f * PI.toFloat() / 3f)),
            // 7..12: Girdle Ring (middle boundary, wider)
            Vertex3D(135f * cos(PI.toFloat() / 6f), 20f, 135f * sin(PI.toFloat() / 6f)),
            Vertex3D(135f * cos(3f * PI.toFloat() / 6f), 20f, 135f * sin(3f * PI.toFloat() / 6f)),
            Vertex3D(135f * cos(5f * PI.toFloat() / 6f), 20f, 135f * sin(5f * PI.toFloat() / 6f)),
            Vertex3D(135f * cos(7f * PI.toFloat() / 6f), 20f, 135f * sin(7f * PI.toFloat() / 6f)),
            Vertex3D(135f * cos(9f * PI.toFloat() / 6f), 20f, 135f * sin(9f * PI.toFloat() / 6f)),
            Vertex3D(135f * cos(11f * PI.toFloat() / 6f), 20f, 135f * sin(11f * PI.toFloat() / 6f)),
            // 13: Bottom Pavilion Tip (culet)
            Vertex3D(0f, 175f, 0f)
        )

        // Unique edge lines connecting vertices
        val edges = listOf(
            Pair(0, 1), Pair(0, 2), Pair(0, 3), Pair(0, 4), Pair(0, 5), Pair(0, 6),
            Pair(1, 2), Pair(2, 3), Pair(3, 4), Pair(4, 5), Pair(5, 6), Pair(6, 1),
            Pair(1, 7), Pair(2, 7), Pair(2, 8), Pair(3, 8), Pair(3, 9), Pair(4, 9),
            Pair(4, 10), Pair(5, 10), Pair(5, 11), Pair(6, 11), Pair(6, 12), Pair(1, 12),
            Pair(7, 8), Pair(8, 9), Pair(9, 10), Pair(10, 11), Pair(11, 12), Pair(12, 7),
            Pair(13, 7), Pair(13, 8), Pair(13, 9), Pair(13, 10), Pair(13, 11), Pair(13, 12)
        )

        // Geometric facets / faces
        val faces = listOf(
            // Top crown facets (Sapphire & Crystal White)
            Face(0, 1, 2, 0),
            Face(0, 2, 3, 1),
            Face(0, 3, 4, 0),
            Face(0, 4, 5, 1),
            Face(0, 5, 6, 0),
            Face(0, 6, 1, 1),

            // Middle band downward facets (Teal & Cyan)
            Face(1, 2, 7, 2),
            Face(2, 3, 8, 3),
            Face(3, 4, 9, 2),
            Face(4, 5, 10, 3),
            Face(5, 6, 11, 2),
            Face(6, 1, 12, 3),

            // Middle band upward facets (Indigo & Royal Blue)
            Face(2, 7, 8, 4),
            Face(3, 8, 9, 5),
            Face(4, 9, 10, 4),
            Face(5, 10, 11, 5),
            Face(6, 11, 12, 4),
            Face(1, 12, 7, 5),

            // Lower pavilion facets (Deep Royal & Sapphire Blue)
            Face(13, 7, 8, 6),
            Face(13, 8, 9, 7),
            Face(13, 9, 10, 6),
            Face(13, 10, 11, 7),
            Face(13, 11, 12, 6),
            Face(13, 12, 7, 7)
        )

        // Project and rotate 3D vertices
        val rotated3D = vertices.map { vertex ->
            // Rotate around Y axis
            val cosY = cos(rotationY)
            val sinY = sin(rotationY)
            val x1 = vertex.x * cosY - vertex.z * sinY
            val z1 = vertex.x * sinY + vertex.z * cosY

            // Rotate around X axis (slight tilting)
            val cosX = cos(rotationX)
            val sinX = sin(rotationX)
            val y2 = vertex.y * cosX - z1 * sinX
            val z2 = vertex.y * sinX + z1 * cosX

            Vertex3D(x1, y2, z2)
        }

        // Project 3D rotated coordinates onto 2D screen with Perspective
        val projected = rotated3D.map { vertex ->
            val cameraDepth = 600f
            val perspectiveFactor = cameraDepth / (cameraDepth + vertex.z)

            val screenX = centerX + vertex.x * (scale / 175f) * perspectiveFactor
            val screenY = centerY + vertex.y * (scale / 175f) * perspectiveFactor

            Pair(Offset(screenX, screenY), vertex.z)
        }

        // Fixed light source direction in world space (top-front-left of the viewer)
        val lx = -0.6f
        val ly = -0.8f
        val lz = -1.0f
        val lLen = sqrt(lx * lx + ly * ly + lz * lz)
        val lnx = lx / lLen
        val lny = ly / lLen
        val lnz = lz / lLen

        // Painter's algorithm: sort faces by depth (largest Z average is drawn first)
        val sortedFaces = faces.mapIndexed { index, face ->
            val p1 = projected[face.v1]
            val p2 = projected[face.v2]
            val p3 = projected[face.v3]
            val avgZ = (p1.second + p2.second + p3.second) / 3f
            Triple(face, avgZ, index)
        }.sortedByDescending { it.second }

        // Draw sorted facets with real reflection computations
        sortedFaces.forEach { (face, _, _) ->
            val p1 = projected[face.v1].first
            val p2 = projected[face.v2].first
            val p3 = projected[face.v3].first

            val path = Path().apply {
                moveTo(p1.x, p1.y)
                lineTo(p2.x, p2.y)
                lineTo(p3.x, p3.y)
                close()
            }

            // Calculate face normal using cross-product of rotated 3D coordinates
            val v1_3D = rotated3D[face.v1]
            val v2_3D = rotated3D[face.v2]
            val v3_3D = rotated3D[face.v3]

            val ux = v2_3D.x - v1_3D.x
            val uy = v2_3D.y - v1_3D.y
            val uz = v2_3D.z - v1_3D.z

            val vx = v3_3D.x - v1_3D.x
            val vy = v3_3D.y - v1_3D.y
            val vz = v3_3D.z - v1_3D.z

            val nx = uy * vz - uz * vy
            val ny = uz * vx - ux * vz
            val nz = ux * vy - uy * vx

            val nLen = sqrt(nx * nx + ny * ny + nz * nz)
            val (fnx, fny, fnz) = if (nLen > 0f) {
                Triple(nx / nLen, ny / nLen, nz / nLen)
            } else {
                Triple(0f, 0f, 1f)
            }

            // Real reflection factor (dot product of face normal and light vector)
            val dot = fnx * lnx + fny * lny + fnz * lnz
            val diffuse = max(0f, dot)
            
            // Extreme specular reflection for bright glints
            val specular = max(0f, dot).pow(14f)

            // Dynamic light sweep passing across the face from left to right
            val faceCenterX = (p1.x + p2.x + p3.x) / 3f
            val normalizedX = (faceCenterX - centerX) / (scale.coerceAtLeast(1f))
            val sweepDist = abs(normalizedX - lightSweepFactor)
            val sweepShine = if (sweepDist < 0.25f) {
                (1.0f - sweepDist / 0.25f) * 0.50f
            } else {
                0f
            }

            // Dynamic ambient/diffuse shading intensity
            val intensity = 0.35f + 0.65f * diffuse

            // Map face groups to luxury gradient palettes (Crystal White, Light Blue, Cyan, Royal Blue)
            val (colorStart, colorEnd) = when (face.colorType) {
                0, 1 -> Pair(Color(0xFFF8FAFC), Color(0xFFE0F2FE)) // Crystal White -> Ice Blue
                2, 3 -> Pair(Color(0xFFBAE6FD), Color(0xFF38BDF8)) // Light Cyan -> Ice Cyan
                4, 5 -> Pair(Color(0xFF0EA5E9), Color(0xFF1D4ED8)) // Cyan -> Royal Blue
                else -> Pair(Color(0xFF1E40AF), Color(0xFF0369A1)) // Royal Blue -> Deep Sapphire
            }

            // Apply intensity shading
            val shadedStart = applyIntensity(colorStart, intensity)
            val shadedEnd = applyIntensity(colorEnd, intensity)

            // Blend in pure white light reflection according to specular factor and sweepShine
            val totalHighlight = (specular + sweepShine).coerceIn(0f, 1f)
            val finalStart = interpolateColor(shadedStart, Color.White, totalHighlight)
            val finalEnd = interpolateColor(shadedEnd, Color.White, totalHighlight)

            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = listOf(finalStart, finalEnd),
                    start = p1,
                    end = p3
                )
            )
        }

        // Draw crystal-white glowing boundary edges
        edges.forEach { (u, v) ->
            val p1 = projected[u].first
            val p2 = projected[v].first
            drawLine(
                color = Color(0xFFFFFFFF).copy(alpha = 0.55f),
                start = p1,
                end = p2,
                strokeWidth = 2.0f
            )
        }

        // Calculate light facing factor for each vertex to place the spectacular dynamic sparkle
        val vertexSpecular = rotated3D.mapIndexed { index, rotated ->
            val rLen = sqrt(rotated.x * rotated.x + rotated.y * rotated.y + rotated.z * rotated.z)
            if (rLen > 0f) {
                val rnx = rotated.x / rLen
                val rny = rotated.y / rLen
                val rnz = rotated.z / rLen
                // Dot product of vertex outward normal with light source vector (top-front-left)
                val dot = rnx * lnx + rny * lny + rnz * lnz
                val spec = max(0f, dot).pow(12f)
                Pair(index, spec)
            } else {
                Pair(index, 0f)
            }
        }

        val brightestVertex = vertexSpecular.maxByOrNull { it.second }
        if (brightestVertex != null && brightestVertex.second > 0.3f) {
            val vertexId = brightestVertex.first
            val intensity = brightestVertex.second
            val screenPos = projected[vertexId].first

            // Draw a soft radial glow under the sparkle
            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = 0.35f * intensity),
                radius = 24.dp.toPx() * intensity,
                center = screenPos
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.65f * intensity),
                radius = 10.dp.toPx() * intensity,
                center = screenPos
            )

            // Draw main horizontal flare beam
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.95f * intensity), Color.Transparent),
                    start = Offset(screenPos.x - 35.dp.toPx() * intensity, screenPos.y),
                    end = Offset(screenPos.x + 35.dp.toPx() * intensity, screenPos.y)
                ),
                start = Offset(screenPos.x - 35.dp.toPx() * intensity, screenPos.y),
                end = Offset(screenPos.x + 35.dp.toPx() * intensity, screenPos.y),
                strokeWidth = 3.dp.toPx() * intensity
            )

            // Draw main vertical flare beam
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.95f * intensity), Color.Transparent),
                    start = Offset(screenPos.x, screenPos.y - 35.dp.toPx() * intensity),
                    end = Offset(screenPos.x, screenPos.y + 35.dp.toPx() * intensity)
                ),
                start = Offset(screenPos.x, screenPos.y - 35.dp.toPx() * intensity),
                end = Offset(screenPos.x, screenPos.y + 35.dp.toPx() * intensity),
                strokeWidth = 3.dp.toPx() * intensity
            )

            // Draw diagonal flare beams (X shape) for the ultimate premium starry look
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color(0xFFBAE6FD).copy(alpha = 0.75f * intensity), Color.Transparent),
                    start = Offset(screenPos.x - 18.dp.toPx() * intensity, screenPos.y - 18.dp.toPx() * intensity),
                    end = Offset(screenPos.x + 18.dp.toPx() * intensity, screenPos.y + 18.dp.toPx() * intensity)
                ),
                start = Offset(screenPos.x - 18.dp.toPx() * intensity, screenPos.y - 18.dp.toPx() * intensity),
                end = Offset(screenPos.x + 18.dp.toPx() * intensity, screenPos.y + 18.dp.toPx() * intensity),
                strokeWidth = 1.5.dp.toPx() * intensity
            )
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color(0xFFBAE6FD).copy(alpha = 0.75f * intensity), Color.Transparent),
                    start = Offset(screenPos.x + 18.dp.toPx() * intensity, screenPos.y - 18.dp.toPx() * intensity),
                    end = Offset(screenPos.x - 18.dp.toPx() * intensity, screenPos.y + 18.dp.toPx() * intensity)
                ),
                start = Offset(screenPos.x + 18.dp.toPx() * intensity, screenPos.y - 18.dp.toPx() * intensity),
                end = Offset(screenPos.x - 18.dp.toPx() * intensity, screenPos.y + 18.dp.toPx() * intensity),
                strokeWidth = 1.5.dp.toPx() * intensity
            )
        }
    }
}

@Composable
private fun LuxuryDocumentCheckModel() {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val w = size.width
        val h = size.height
        val centerX = w / 2f
        val centerY = h / 2f

        val docW = w * 0.45f
        val docH = h * 0.55f

        // Document card path with folded top-right corner to make it look professional
        val docPath = Path().apply {
            val radius = 10.dp.toPx()
            val foldSize = docW * 0.28f

            // Start at top-left
            moveTo(-docW / 2f + radius, -docH / 2f)
            // Go to the beginning of the fold
            lineTo(docW / 2f - foldSize, -docH / 2f)
            // Fold corner
            lineTo(docW / 2f, -docH / 2f + foldSize)
            // Go to bottom-right
            lineTo(docW / 2f, docH / 2f - radius)
            // Round bottom-right corner
            quadraticTo(docW / 2f, docH / 2f, docW / 2f - radius, docH / 2f)
            // Go to bottom-left
            lineTo(-docW / 2f + radius, docH / 2f)
            // Round bottom-left corner
            quadraticTo(-docW / 2f, docH / 2f, -docW / 2f, docH / 2f - radius)
            // Go to top-left
            lineTo(-docW / 2f, -docH / 2f + radius)
            // Round top-left corner
            quadraticTo(-docW / 2f, -docH / 2f, -docW / 2f + radius, -docH / 2f)
            close()
        }

        withTransform({
            translate(left = centerX, top = centerY)
        }) {
            // Document gradient fill (crystalline deep sapphire, cyan & translucent space)
            drawPath(
                path = docPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x330EA5E9), // Neon crystal cyan
                        Color(0x1A030406), // Deep black-blue
                        Color(0x2B1E40AF)  // Sapphire royal blue translucent
                    ),
                    start = Offset(-docW / 2f, -docH / 2f),
                    end = Offset(docW / 2f, docH / 2f)
                )
            )

            // Neon glowing crystalline edge border
            drawPath(
                path = docPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF38BDF8).copy(alpha = 0.85f),
                        Color(0xFF1D4ED8).copy(alpha = 0.25f),
                        Color(0xFF0EA5E9).copy(alpha = 0.90f)
                    ),
                    start = Offset(-docW / 2f, -docH / 2f),
                    end = Offset(docW / 2f, docH / 2f)
                ),
                style = Stroke(width = 2.5f)
            )

            // Fold accent corner piece
            val foldSize = docW * 0.28f
            val foldPath = Path().apply {
                moveTo(docW / 2f - foldSize, -docH / 2f)
                lineTo(docW / 2f - foldSize, -docH / 2f + foldSize)
                lineTo(docW / 2f, -docH / 2f + foldSize)
                close()
            }
            drawPath(
                path = foldPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFBAE6FD).copy(alpha = 0.85f),
                        Color(0xFF0EA5E9).copy(alpha = 0.50f)
                    )
                )
            )

            // Draw a large, luxury glowing Checkmark (علامة صح) in the middle of the document
            val checkPath = Path().apply {
                moveTo(-docW * 0.18f, docH * 0.05f)
                lineTo(-docW * 0.03f, docH * 0.20f)
                lineTo(docW * 0.22f, -docH * 0.12f)
            }

            // Draw checkmark stroke (Neon cyan / Royal blue premium glow)
            drawPath(
                path = checkPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFE0F2FE), // Ice white
                        Color(0xFF38BDF8), // Cyan
                        Color(0xFF0EA5E9)  // Deep Cyan
                    )
                ),
                style = Stroke(width = 5.0f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

private fun interpolateColor(start: Color, end: Color, fraction: Float): Color {
    val r = start.red + (end.red - start.red) * fraction
    val g = start.green + (end.green - start.green) * fraction
    val b = start.blue + (end.blue - start.blue) * fraction
    val a = start.alpha + (end.alpha - start.alpha) * fraction
    return Color(r, g, b, a)
}

private fun applyIntensity(color: Color, intensity: Float): Color {
    val r = (color.red * intensity).coerceIn(0f, 1f)
    val g = (color.green * intensity).coerceIn(0f, 1f)
    val b = (color.blue * intensity).coerceIn(0f, 1f)
    return Color(r, g, b, color.alpha)
}