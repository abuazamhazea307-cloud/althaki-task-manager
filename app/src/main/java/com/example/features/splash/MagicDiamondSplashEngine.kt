package com.example.features.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import kotlin.math.*

// 3D Vertex representation
private data class Vertex3D(val x: Float, val y: Float, val z: Float)

// 3D Face representation
private data class Face(val v1: Int, val v2: Int, val v3: Int, val colorType: Int)

@Composable
fun MagicDiamondSplashEngine() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("magic_diamond_splash_surface"),
        color = Color(0xFF030406) // Luxury deep space black
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            DiamondGeometricModel()
        }
    }
}

@Composable
private fun DiamondGeometricModel() {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .testTag("diamond_geometric_canvas")
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        // Bounding dimension around 180dp (scaled proportionally but capped elegantly)
        val scale = (min(size.width, size.height) * 0.38f).coerceIn(160f, 320f)

        // 3D model vertices for a symmetric brilliant cut diamond
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

        // Symmetrical projection angle (completely static perspective)
        val angleY = 0.45f
        val angleX = -0.25f

        // Project 3D points to 2D space
        val projected = vertices.map { vertex ->
            // Rotate around Y axis
            val cosY = cos(angleY)
            val sinY = sin(angleY)
            val x1 = vertex.x * cosY - vertex.z * sinY
            val z1 = vertex.x * sinY + vertex.z * cosY

            // Rotate around X axis
            val cosX = cos(angleX)
            val sinX = sin(angleX)
            val y2 = vertex.y * cosX - z1 * sinX
            val z2 = vertex.y * sinX + z1 * cosX

            // Perspective factor
            val cameraDepth = 600f
            val perspectiveFactor = cameraDepth / (cameraDepth + z2)

            // Scaled Screen Offset
            val screenX = centerX + x1 * (scale / 175f) * perspectiveFactor
            val screenY = centerY + y2 * (scale / 175f) * perspectiveFactor

            Pair(Offset(screenX, screenY), z2)
        }

        // Painter's algorithm: sort faces by depth (largest Z average is drawn first)
        val sortedFaces = faces.mapIndexed { index, face ->
            val p1 = projected[face.v1]
            val p2 = projected[face.v2]
            val p3 = projected[face.v3]
            val avgZ = (p1.second + p2.second + p3.second) / 3f
            Triple(face, avgZ, index)
        }.sortedByDescending { it.second }

        // Draw sorted facets
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

            // Map face groups to luxury gradient palettes (Crystal White, Light Blue, Cyan, Royal Blue)
            val (colorStart, colorEnd) = when (face.colorType) {
                0, 1 -> Pair(Color(0xFFF8FAFC), Color(0xFFE0F2FE)) // Crystal White -> Light Sky Blue
                2, 3 -> Pair(Color(0xFFBAE6FD), Color(0xFF38BDF8)) // Light Blue -> Cyan
                4, 5 -> Pair(Color(0xFF0EA5E9), Color(0xFF1D4ED8)) // Cyan -> Royal Blue
                else -> Pair(Color(0xFF1E40AF), Color(0xFF0369A1)) // Royal Blue -> Deep Sapphire
            }

            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = listOf(colorStart, colorEnd),
                    start = p1,
                    end = p3
                )
            )
        }

        // Draw crystal-white outer/inner boundaries (borders)
        edges.forEach { (u, v) ->
            val p1 = projected[u].first
            val p2 = projected[v].first
            drawLine(
                color = Color(0xFFFFFFFF).copy(alpha = 0.85f),
                start = p1,
                end = p2,
                strokeWidth = 2.5f
            )
        }
    }
}
