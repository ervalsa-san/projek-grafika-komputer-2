package com.ervalsa.projek_grafika_komputer_2.ui.feature.glbb

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GlbbCanvas(
    modifier: Modifier,
    state: GlbbState

) {
    Canvas(modifier = modifier) {
        state.size = size;
        val pos = state.posToScreen(size)
        val ballRadius = state.radius

        fun radius() : Float{
            val ballScale = Math.max(pos.y, 0f) / Math.max(size.height, 1f)
            val minScale = 0.5f * (1f - ballScale)
            val scaled = 1.0f - Math.min(minScale, 0.9f)
            return ballRadius * scaled
        }

        drawCircle(
            color = Color.Red,
            radius = radius(),
            center = pos
        )

        val line = 4;
        val lineFloat = line.toFloat();
        val points = (0..line)
            .map { it * 360.0 / lineFloat }
            .map { it + pos.x + pos.y }
            .map { Math.toRadians(it) }
            .map {
                Offset(
                    (pos.x + (radius() * cos(it)).toFloat()),
                    (pos.y + (radius() * sin(it)).toFloat())
                )
            };

        for (it in points) {
            drawLine(
                color = Color.Yellow,
                start = pos,
                end = it,
                strokeWidth = Stroke.DefaultMiter
            );
        }
    }
}
