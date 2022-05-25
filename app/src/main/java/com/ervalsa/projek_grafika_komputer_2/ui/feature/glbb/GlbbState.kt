package com.ervalsa.projek_grafika_komputer_2.ui.feature.glbb

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import kotlin.math.abs
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Instant.Companion.now() {
    (Clock.System.now() - Clock.System.now())
}

class Duration {

    companion object {
        private val now = (Clock.System.now())

        @OptIn(ExperimentalTime::class)
        val ZERO = now - now

        fun fromSecsDouble(secs: Double) {
//            .plus(secs.times(1000 * 1000 * 1000), DateTimeUnit.NANOSECOND)
        }
    }
}

class GlbbState {
    var pos by mutableStateOf(Offset(0f, 0f))
    var radius by mutableStateOf(100f)
    var horizontal = HorizontalState()
    var vertical = VerticalState()
    var velocity: Float
        get() = horizontal.velocity
        set(value) {
            horizontal.velocity = value
        }

    val scaledRadius: Float
        get() = run {
            val ballScale = Math.max(pos.y, 0f) / Math.max(size.height, 1f)
            val minScale = 0.5f * ballScale
            val scaled = 1.0f - Math.min(minScale, 0.9f)
            return radius * scaled
        }

    var size by mutableStateOf(Size(0f, 0f))

    fun posToScreen(size: Size): Offset {
        val x = pos.x + scaledRadius
        val y = size.height - scaledRadius - pos.y
        return Offset(x, y)
    }

    fun posFromScreen(pos: Offset): Offset {
        val x = pos.x - scaledRadius
        val y = size.height - pos.y - scaledRadius

        return Offset(x, y);
    }

    fun posMax() = Offset(size.width, size.height) - (Offset(scaledRadius, scaledRadius) * 2f)

    fun clamp() {
        val max = posMax();
        val x = pos.x.coerceAtMost(max.x).coerceAtLeast(0f);
        val y = pos.y.coerceAtMost(max.y).coerceAtLeast(0f);
        pos = Offset(x, y);
    }

    fun setPosClamped(pos: Offset) {
        this.pos = pos
        clamp()
    }

    val isPlaying: Boolean
        get() = horizontal.isPlaying || vertical.isPlaying

    @OptIn(ExperimentalTime::class)
    class HorizontalState {
        var start = Clock.System.now()
        var velocity by mutableStateOf(300f)
        var direction by mutableStateOf(0, referentialEqualityPolicy())
        val acceleration = 100f

        var duration = (Clock.System.now() - Clock.System.now())

        @OptIn(ExperimentalTime::class)
        fun play(direction: Int) {
            this.direction = direction
            start = Clock.System.now()

            val duration = abs(velocity) / abs(acceleration)

            this.duration = Clock.System.now().plus(
                duration.times(1000 * 1000).toLong(),
                DateTimeUnit.MICROSECOND
            ) - Clock.System.now()
        }

        val isPlaying: Boolean
            get() = direction != 0

        fun distance_at(time: Float): Float {
            return velocity * time + (0.5f) * -100f * (time * time)
        }

        fun velocity_at(time: Float): Float {
            return velocity + -100f * time
        }

        @OptIn(ExperimentalTime::class)
        fun move(pos: Float, range: IntRange): Float {
            return if (isPlaying) {
                var pos = pos

                val elapsed = (Clock.System.now() - start).coerceAtMost(duration)
                val time = elapsed.toDouble(DurationUnit.SECONDS).toFloat()
                var distance = distance_at(time)

                while (distance > 0) {
                    val moveBy = distance.coerceAtMost(5f)
                    distance -= moveBy
                    pos += moveBy * direction.toFloat()

                    if (!range.contains(pos.toInt())) {
                        direction *= -1
                    }
                }

                velocity = velocity_at(time)


                if (elapsed < duration) {
                    play(direction)
                } else {
                    direction = 0
                    velocity = 0f
                }

                pos
            } else {
                pos
            }
        }
    }

    class VerticalState {
        private var direction by mutableStateOf(0)
        var accel = 10f

        @OptIn(ExperimentalTime::class)
        var start = Clock.System.now()

        fun fall() {
            direction = -1;
            start = Clock.System.now()
            accel = 10f
        }

        fun stop() {
            direction = 0
        }

        val isPlaying
            get() = direction != 0

        @OptIn(ExperimentalTime::class)
        fun move(pos: Float): Float {
            return if (isPlaying) {
                var pos = pos
                val elapsed = (Clock.System.now() - start)
                val time = elapsed.toDouble(DurationUnit.SECONDS).toFloat()
                println("time: $time elapsed: $elapsed $pos $direction")

                // drop
                if (direction == -1) {
                    accel += 9.8f;
                    accel += accel * time;

                    pos -= accel

                    if (pos <= 0f) {
                        direction *= -1
                        pos = 0f

                    }

                } else if (direction == 1) {
                    // up
                    accel -= 9.8f
                    accel -= accel * time
                    pos += accel

                    if (accel <= 0f) {
                        direction *= -1
                    }
                }

                start = (Clock.System.now())

                if (accel <= 0f && pos <= 0f) {
                    direction = 0
                }

                pos
            } else {
                pos
            }
        }
    }

    fun playHorizontal(direction: Int) {
        horizontal.play(direction)
    }

    fun playVertical() {
        vertical.fall()
    }

    fun move() {
        val x = horizontal.move(pos.x, 0..(posMax().x.toInt()))
        val y = vertical.move(pos.y)
        println("$x $y $pos")
        this.pos = Offset(x, y)
    }
}