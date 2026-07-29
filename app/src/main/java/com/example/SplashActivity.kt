package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.*

private data class Point3D(val x: Float, val y: Float, val z: Float)

private data class Facet(val indices: IntArray)

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Keep on screen for 2000ms, then launch MainActivity
            LaunchedEffect(Unit) {
                delay(2000)
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF00B4D8)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Diamond3DRenderer(
                        modifier = Modifier
                            .size(320.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    TaskManagerLogo(
                        modifier = Modifier
                            .size(100.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Diamond3DRenderer(modifier: Modifier = Modifier) {
    val timeMs = remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        val startTime = withFrameMillis { it }
        while (true) {
            withFrameMillis { frameTime ->
                timeMs.value = frameTime - startTime
            }
        }
    }

    val tableRadius = 0.42f
    val girdleRadius = 0.95f
    val tableY = 0.45f
    val girdleY = 0.1f
    val culetY = -0.75f

    // Vertices definition
    val vertices = remember {
        val tablePoints = List(8) { i ->
            val angle = (i * PI / 4.0).toFloat()
            Point3D(tableRadius * cos(angle), tableY, tableRadius * sin(angle))
        }
        val girdlePoints = List(16) { j ->
            val angle = (j * PI / 8.0).toFloat()
            Point3D(girdleRadius * cos(angle), girdleY, girdleRadius * sin(angle))
        }
        val culetPoint = Point3D(0f, culetY, 0f)
        tablePoints + girdlePoints + listOf(culetPoint)
    }

    // Facets definition
    val facets = remember {
        val list = mutableListOf<Facet>()
        
        // 1. Table facet (Octagon)
        list.add(Facet(intArrayOf(0, 1, 2, 3, 4, 5, 6, 7)))

        // 2. Crown Star Triangles (8 facets)
        for (i in 0..7) {
            list.add(Facet(intArrayOf(i, (i + 1) % 8, 8 + (2 * i + 1) % 16)))
        }

        // 3. Crown Kite left & right triangles (16 facets)
        for (i in 0..7) {
            list.add(Facet(intArrayOf(i, 8 + (2 * i + 1) % 16, 8 + (2 * i) % 16)))
            list.add(Facet(intArrayOf(i, 8 + (2 * i) % 16, 8 + (2 * i - 1 + 16) % 16)))
        }

        // 4. Pavilion triangles (16 facets)
        for (j in 0..15) {
            list.add(Facet(intArrayOf(8 + j, 24, 8 + (j + 1) % 16)))
        }

        list.toList()
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val scale = min(width, height) * 0.48f
        val centerX = width / 2f
        val centerY = height / 2f
        val cameraD = 3.0f

        val t = timeMs.value
        // Very slow Y-axis rotation
        val angleY = (t % 10000) / 10000f * 2f * PI.toFloat()
        // Constant slight X-axis tilt (so we look down at the beautiful facets)
        val angleX = -0.32f

        val cosY = cos(angleY)
        val sinY = sin(angleY)
        val cosX = cos(angleX)
        val sinX = sin(angleX)

        // Rotate vertices in 3D
        val rotatedVertices = vertices.map { pt ->
            // Rotate around Y
            val x1 = pt.x * cosY + pt.z * sinY
            val y1 = pt.y
            val z1 = -pt.x * sinY + pt.z * cosY

            // Rotate around X
            val rx = x1
            val ry = y1 * cosX - z1 * sinX
            val rz = y1 * sinX + z1 * cosX

            Point3D(rx, ry, rz)
        }

        // Project vertices to 2D
        val projectedVertices = rotatedVertices.map { pt ->
            val factor = cameraD / (cameraD - pt.z)
            val px = centerX + pt.x * factor * scale
            val py = centerY - pt.y * factor * scale
            PointF(px, py)
        }

        // Calculate face normals and average Z for depth sorting (Painter's Algorithm)
        val sortedFacetsWithDepth = facets.map { facet ->
            // Depth is average Z of rotated vertices
            val avgZ = facet.indices.map { rotatedVertices[it].z }.average().toFloat()
            facet to avgZ
        }.sortedBy { it.second } // Sort from back to front (lowest Z to highest Z)

        // Light sources in 3D
        val l1x = 0.3f; val l1y = 0.5f; val l1z = 0.8f // Primary Ice White
        val l2x = -0.8f; val l2y = 0.2f; val l2z = 0.5f // Neon Cyan
        val l3x = 0.5f; val l3y = -0.6f; val l3z = -0.3f // Sapphire/Royal Blue

        // Light Sweep moving light
        val sweepAngle = (t % 4000) / 4000f * 2f * PI.toFloat()
        val lsx = cos(sweepAngle)
        val lsy = 0.3f
        val lsz = sin(sweepAngle)

        // Draw each sorted facet
        sortedFacetsWithDepth.forEach { (facet, _) ->
            val indices = facet.indices

            // Take first 3 vertices of the facet to compute flat normal
            val p0 = rotatedVertices[indices[0]]
            val p1 = rotatedVertices[indices[1]]
            val p2 = rotatedVertices[indices[2]]

            val ux = p1.x - p0.x
            val uy = p1.y - p0.y
            val uz = p1.z - p0.z

            val vx = p2.x - p0.x
            val vy = p2.y - p0.y
            val vz = p2.z - p0.z

            // Cross product
            var nx = uy * vz - uz * vy
            var ny = uz * vx - ux * vz
            var nz = ux * vy - uy * vx

            // Normalize normal vector
            val len = sqrt(nx * nx + ny * ny + nz * nz)
            if (len > 1e-6f) {
                nx /= len
                ny /= len
                nz /= len
            } else {
                nx = 0f; ny = 0f; nz = 1f
            }

            // Absolute value of dot products for double-sided crystal lighting/refraction simulation
            val dot1 = abs(nx * l1x + ny * l1y + nz * l1z)
            val dot2 = abs(nx * l2x + ny * l2y + nz * l2z)
            val dot3 = abs(nx * l3x + ny * l3y + nz * l3z)
            val dotSweep = abs(nx * lsx + ny * lsy + nz * lsz)

            // Specular reflections
            val spec = nz.pow(16) // Shiny highlights aligned with viewer
            val specSweep = dotSweep.pow(24) // Extra bright sweep highlights

            // Combine components for gorgeous, vibrant gemstone lighting
            val r = (0.05f + 0.60f * dot1 + 0.10f * dot2 + 0.05f * dot3 + spec * 0.35f + specSweep * 0.40f).coerceIn(0f, 1f)
            val g = (0.08f + 0.60f * dot1 + 0.65f * dot2 + 0.10f * dot3 + spec * 0.35f + specSweep * 0.45f).coerceIn(0f, 1f)
            val b = (0.22f + 0.60f * dot1 + 0.35f * dot2 + 0.85f * dot3 + spec * 0.45f + specSweep * 0.45f).coerceIn(0f, 1f)

            // Dynamic transparency (light facing are more opaque, back faces are beautiful translucent refractions)
            val alpha = (0.50f + 0.25f * dot1 + specSweep * 0.20f).coerceIn(0.2f, 0.95f)

            val fillColor = Color(r, g, b, alpha)
            val strokeColor = Color(
                (0.85f + specSweep * 0.15f).coerceIn(0f, 1f),
                (0.92f + specSweep * 0.08f).coerceIn(0f, 1f),
                1.0f,
                (0.20f + specSweep * 0.45f).coerceIn(0f, 1f)
            )

            // Draw facet path
            val path = Path().apply {
                val fPt = projectedVertices[indices[0]]
                moveTo(fPt.x, fPt.y)
                for (k in 1 until indices.size) {
                    val pt = projectedVertices[indices[k]]
                    lineTo(pt.x, pt.y)
                }
                close()
            }

            drawPath(path = path, color = fillColor)
            drawPath(path = path, color = strokeColor, style = Stroke(width = 1.dp.toPx()))
        }
    }
}

@Composable
fun TaskManagerLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Draw glowing background blur behind the logo
        drawCircle(
            color = Color(0x1200E5FF),
            radius = w * 0.45f,
            center = androidx.compose.ui.geometry.Offset(w / 2f, h / 2f)
        )

        // 2. Define the sheet of paper path with folded bottom-right corner
        val px0 = w * 0.22f
        val px1 = w * 0.78f
        val py0 = h * 0.14f
        val py1 = h * 0.86f

        // The fold starts at:
        val foldX = w * 0.58f
        val foldY = h * 0.66f

        val paperPath = Path().apply {
            moveTo(px0, py0) // Top-Left
            lineTo(px1, py0) // Top-Right
            lineTo(px1, foldY) // Bottom-Right Fold crease start
            lineTo(foldX, py1) // Bottom-Right Fold crease end
            lineTo(px0, py1) // Bottom-Left
            close()
        }

        // Glassmorphic paper gradient fill
        val paperGradient = Brush.linearGradient(
            colors = listOf(Color(0x1EFFFFFF), Color(0x06FFFFFF)),
            start = androidx.compose.ui.geometry.Offset(px0, py0),
            end = androidx.compose.ui.geometry.Offset(px1, py1)
        )
        drawPath(path = paperPath, brush = paperGradient)

        // Border stroke gradient
        val borderGradient = Brush.linearGradient(
            colors = listOf(Color(0x40FFFFFF), Color(0x1800E5FF), Color(0x101565C0)),
            start = androidx.compose.ui.geometry.Offset(px0, py0),
            end = androidx.compose.ui.geometry.Offset(px1, py1)
        )
        drawPath(path = paperPath, brush = borderGradient, style = Stroke(width = 1.5.dp.toPx()))

        // 3. Draw the inward folded paper corner (crease)
        val foldPath = Path().apply {
            moveTo(px1, foldY)
            lineTo(foldX, foldY)
            lineTo(foldX, py1)
            close()
        }
        drawPath(path = foldPath, color = Color(0x28FFFFFF))
        drawPath(path = foldPath, color = Color(0x50FFFFFF), style = Stroke(width = 1.dp.toPx()))

        // 4. Draw elegant blank horizontal ruled lines (minimalist task sheet)
        val linesCount = 4
        val lineStartPercent = 0.34f
        val lineEndPercent = 0.66f
        val linesYRange = py0 + (py1 - py0) * 0.18f
        val linesSpacing = (py1 - py0) * 0.11f

        for (i in 0 until linesCount) {
            val ly = linesYRange + i * linesSpacing
            // Ruled line brush
            val lineBrush = Brush.linearGradient(
                colors = listOf(Color(0x25FFFFFF), Color(0x05FFFFFF)),
                start = androidx.compose.ui.geometry.Offset(w * lineStartPercent, ly),
                end = androidx.compose.ui.geometry.Offset(w * lineEndPercent, ly)
            )
            drawLine(
                brush = lineBrush,
                start = androidx.compose.ui.geometry.Offset(w * lineStartPercent, ly),
                end = androidx.compose.ui.geometry.Offset(w * lineEndPercent, ly),
                strokeWidth = 1.5.dp.toPx()
            )
        }

        // 5. Draw neon cyan glowing checkmark on top of the sheet of paper
        val checkPath = Path().apply {
            moveTo(w * 0.32f, h * 0.50f)
            lineTo(w * 0.46f, h * 0.64f)
            lineTo(w * 0.72f, h * 0.32f)
        }

        // Layered strokes for neon glow effect
        // Layer A: Outer broad glowing halo
        drawPath(
            path = checkPath,
            color = Color(0x0E00E5FF),
            style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        // Layer B: Medium glowing halo
        drawPath(
            path = checkPath,
            color = Color(0x2E00E5FF),
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        // Layer C: Core bright neon checkmark
        val checkGradient = Brush.linearGradient(
            colors = listOf(Color(0xFFE0F7FA), Color(0xFF00E5FF)),
            start = androidx.compose.ui.geometry.Offset(w * 0.32f, h * 0.50f),
            end = androidx.compose.ui.geometry.Offset(w * 0.72f, h * 0.32f)
        )
        drawPath(
            path = checkPath,
            brush = checkGradient,
            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

private class PointF(val x: Float, val y: Float)
