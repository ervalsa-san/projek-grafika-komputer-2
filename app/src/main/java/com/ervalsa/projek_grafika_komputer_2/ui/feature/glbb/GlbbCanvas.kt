package com.ervalsa.projek_grafika_komputer_2.ui.feature.glbb

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

class GlbbState {
    var pos by mutableStateOf(Offset(0f, 0f))
    var radius by mutableStateOf(100f)
    var size by mutableStateOf(Size(0f, 0f))

    fun posToScreen(size: Size): Offset {
        val x = pos.x + radius;
        val y = size.height - radius - pos.y;

        return Offset(x, y)
    }

    fun posMax() = Offset(size.width, size.height) - (Offset(radius, radius) * 2f)

    fun setPosClamped(pos: Offset) {
        this.pos = pos;
        clamp();
    }

    fun clamp() {
        val max = posMax();
        val x = pos.x.coerceAtMost(max.x).coerceAtLeast(0f);
        val y = pos.y.coerceAtMost(max.y).coerceAtLeast(0f);
        pos = Offset(x,y);
    }

    fun posFromScreen(pos: Offset): Offset {
        val x = pos.x - radius;
        val y = size.height - pos.y - radius;

        return Offset(x, y);
    }
}

@Composable
fun GlbbCanvas(
    modifier: Modifier,
    state: GlbbState

) {
    Canvas(modifier = modifier) {
        state.size = size;
        val pos = state.posToScreen(size);
        val radius = state.radius

        drawCircle(
            color = Color.Red,
            radius = radius,
            center = pos
        )


    }
}