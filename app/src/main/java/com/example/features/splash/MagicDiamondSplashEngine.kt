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

    // Slow 360-degree rotation around Y axis (12-second cycle)
    val rotationY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationY"
    )

    // Gentle wobble around X axis (4.5-second cycle)
    val rotationX by infiniteTransition.animateFloat(
        initialValue = -0.15f,
        targetValue = -0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotationX"
    )

    // Breathing pulse scale for the entire view (3.5-second cycle)
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Vertical floating hover offset for the logo below (3-second cycle, out of phase)
    val hoverOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hoverOffset"
    )

    // Dynamic light sweep factor (passes from left to right over the diamond)
    val lightSweepFactor by infiniteTransition.animateFloat(
        initialValue = -1.8f,
        targetValue = 1.8f,
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
                    pulseScale = pulseScale,
                    rotationY = rotationY,
                    rotationX = rotationX,
                    lightSweepFactor = lightSweepFactor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Luxury Task Manager Pen and Paper Logo (matching design style)
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .testTag("luxury_task_logo_container")
                    .offset(y = hoverOffset.dp),
                contentAlignment = Alignment.Center
            ) {
                LuxuryPenAndPaperModel(pulseScale = pulseScale)
            }
        }
    }
}

@Composable
private fun DiamondGeometricModel(
    pulseScale: Float,
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

        // scale sized beautifully within canvas
        val baseScale = min(size.width, size.height) * 0.45f
        val scale = baseScale * pulseScale

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

        // Project 3D rotated coordinates onto 2D screen
        val projected = rotated3D.map { vertex ->
            val cameraDepth = 600f
            val perspectiveFactor = cameraDepth / (cameraDepth + vertex.z)

            val screenX = centerX + vertex.x * (scale / 175f) * perspectiveFactor
            val screenY = centerY + vertex.y * (scale / 175f) * perspectiveFactor

            Pair(Offset(screenX, screenY), vertex.z)
        }

        // Fixed light source direction in world space (top-front-right of the viewer)
        // High luxury shine vector
        val lx = 0.5f
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
            val sweepShine = if (sweepDist < 0.22f) {
                (1.0f - sweepDist / 0.22f) * 0.45f
            } else {
                0f
            }

            // Dynamic ambient/diffuse shading intensity
            val intensity = 0.35f + 0.65f * diffuse

            // Map face groups to luxury gradient palettes (Crystal White, Light Blue, Cyan, Royal Blue)
            val (colorStart, colorEnd) = when (face.colorType) {
                0, 1 -> Pair(Color(0xFFF8FAFC), Color(0xFFE0F2FE)) // Crystal White -> Light Sky Blue
                2, 3 -> Pair(Color(0xFFBAE6FD), Color(0xFF38BDF8)) // Light Blue -> Cyan
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
                strokeWidth = 2f
            )
        }
    }
}

@Composable
private fun LuxuryPenAndPaperModel(pulseScale: Float) {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val w = size.width
        val h = size.height
        val centerX = w / 2f
        val centerY = h / 2f

        // Draw Paper (semi-translucent glassmorphic sheet with neon borders)
        withTransform({
            translate(left = centerX, top = centerY + 10f)
            rotate(degrees = -10f) // slight artistic tilt
            scale(scaleX = pulseScale, scaleY = pulseScale)
        }) {
            val paperW = w * 0.45f
            val paperH = h * 0.55f

            val paperPath = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(-paperW / 2f, -paperH / 2f, paperW / 2f, paperH / 2f),
                        cornerRadius = CornerRadius(10.dp.toPx())
                    )
                )
            }

            // Translucent crystal blue background
            drawPath(
                path = paperPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x330EA5E9), // Neon blue glow
                        Color(0x11030406), // Deep space dark
                        Color(0x221E40AF)  // Sapphire translucent
                    ),
                    start = Offset(-paperW / 2f, -paperH / 2f),
                    end = Offset(paperW / 2f, paperH / 2f)
                )
            )

            // Glowing border stroke
            drawPath(
                path = paperPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF38BDF8).copy(alpha = 0.8f),
                        Color(0xFF1D4ED8).copy(alpha = 0.2f),
                        Color(0xFF0EA5E9).copy(alpha = 0.9f)
                    ),
                    start = Offset(-paperW / 2f, -paperH / 2f),
                    end = Offset(paperW / 2f, paperH / 2f)
                ),
                style = Stroke(width = 2.5f)
            )

            // Draw task list items
            val lineStartX = -paperW * 0.28f
            val lineEndX = paperW * 0.32f
            val startY = -paperH * 0.28f
            val lineSpacing = paperH * 0.19f

            for (i in 0 until 4) {
                val lineY = startY + i * lineSpacing
                val boxX = lineStartX - 10f
                val boxSize = 12f

                // Draw tiny checklist boxes
                drawRoundRect(
                    color = Color(0xFF38BDF8).copy(alpha = 0.65f),
                    topLeft = Offset(boxX - boxSize / 2f, lineY - boxSize / 2f),
                    size = Size(boxSize, boxSize),
                    cornerRadius = CornerRadius(2.5f),
                    style = Stroke(width = 1.5f)
                )

                // Render checkmarks inside some boxes
                if (i == 0 || i == 2) {
                    val checkPath = Path().apply {
                        moveTo(boxX - boxSize / 3f, lineY)
                        lineTo(boxX - boxSize / 12f, lineY + boxSize / 3f)
                        lineTo(boxX + boxSize / 2f, lineY - boxSize / 3f)
                    }
                    drawPath(
                        path = checkPath,
                        color = Color(0xFF38BDF8),
                        style = Stroke(width = 1.8f, cap = StrokeCap.Round)
                    )
                }

                // Draw horizontal writing lines
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFE0F2FE).copy(alpha = 0.8f),
                            Color(0xFF38BDF8).copy(alpha = 0.15f)
                        )
                    ),
                    start = Offset(lineStartX + 12f, lineY),
                    end = Offset(lineEndX, lineY),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }
        }

        // Draw Pen (diagonal luxury stylus pen with metallic gold tips and cyan highlights)
        withTransform({
            translate(left = centerX + 18f, top = centerY - 15f)
            rotate(degrees = -35f) // Pen pointing towards paper tip
            scale(scaleX = pulseScale, scaleY = pulseScale)
        }) {
            val penLength = h * 0.5f
            val penWidth = 12f

            // 1. Tip (Luxury golden stylus nib)
            val tipPath = Path().apply {
                moveTo(-penWidth / 2f, penLength * 0.32f)
                lineTo(0f, penLength * 0.42f) // Pointy stylus tip
                lineTo(penWidth / 2f, penLength * 0.32f)
                close()
            }
            drawPath(
                path = tipPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFBBF24), // Gold
                        Color(0xFFD97706)  // Bronze/Deep Gold
                    )
                )
            )

            // 2. Sleek crystal-blue metallic body
            val bodyPath = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(-penWidth / 2f, -penLength * 0.38f, penWidth / 2f, penLength * 0.32f),
                        cornerRadius = CornerRadius(3f)
                    )
                )
            }
            drawPath(
                path = bodyPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0EA5E9), // Glowing cyan
                        Color(0xFF1E3A8A), // Metallic sapphire blue
                        Color(0xFF38BDF8)  // Bright cyan edge
                    ),
                    start = Offset(-penWidth / 2f, 0f),
                    end = Offset(penWidth / 2f, 0f)
                )
            )

            // Highlight stroke
            drawPath(
                path = bodyPath,
                color = Color(0xFFFFFFFF).copy(alpha = 0.5f),
                style = Stroke(width = 1.2f)
            )

            // 3. Golden pen clip/cap at the top
            val capPath = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(-penWidth / 2f - 2f, -penLength * 0.43f, penWidth / 2f + 2f, -penLength * 0.36f),
                        cornerRadius = CornerRadius(2f)
                    )
                )
            }
            drawPath(
                path = capPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFBBF24),
                        Color(0xFFD97706)
                    )
                )
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
