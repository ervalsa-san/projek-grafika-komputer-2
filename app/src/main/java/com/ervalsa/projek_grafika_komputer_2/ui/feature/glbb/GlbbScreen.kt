package com.ervalsa.projek_grafika_komputer_2.ui.feature.glbb

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun GlbbScreen() {

    // State
    val glbbState by remember { mutableStateOf(GlbbState()) }
    var velocityTextState by remember { mutableStateOf(glbbState.velocity.toString()) }
    var posSlider by remember(glbbState) { mutableStateOf(glbbState.pos) }
    var isPlaying by remember(glbbState) { mutableStateOf(false) }
    val horizontalSlider = posSlider.x
    val verticalSlider = posSlider.y

    if (isPlaying.not()) {
        glbbState.pos = posSlider
        velocityTextState.toFloatOrNull()?.let {
            glbbState.velocity = it
        }
    } else {
        posSlider = glbbState.pos
        velocityTextState = glbbState.velocity.toString()
    }

    isPlaying = glbbState.isPlaying

    val ballPosX = glbbState.pos.x
    val ballPosY = glbbState.pos.y
    val size = glbbState.size

    Surface() {
        // Canvas
        Column() {
            Box(
                modifier = Modifier
                    .background(Color.Blue)
            ) {
                GlbbCanvas(
                    modifier = Modifier
                        .width(500.dp)
                        .height(400.dp)
                        .pointerInput(glbbState) {
                            forEachGesture {
                                awaitPointerEventScope {
                                    awaitFirstDown()
                                    do {
                                        //This PointerEvent contains details including
                                        // event, id, position and more
                                        val event = awaitPointerEvent()
                                        // ACTION_MOVE loop

                                        // Consuming event prevents other gestures or scroll to intercept
                                        event.changes.forEach { change ->
                                            change.consumePositionChange()
                                            posSlider = glbbState.posFromScreen(change.position)
                                        }
                                    } while (
                                        event.changes.any { it.pressed }
                                    )
                                }
                            }
                        },
                    glbbState
                )
            }
        }

        // Control
        Column(
            modifier = Modifier
                .padding(8.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column() {
                    Button(
                        onClick = {
                            if (ballPosX < 0 || glbbState.velocity > 0) {
                                posSlider = Offset(posSlider.x - 5f, posSlider.y)
                                glbbState.velocity -= 1
                                velocityTextState = glbbState.velocity.toString()
                            }
                        },
                    ) {
                        Text(text = "<")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            glbbState.playHorizontal(-1)
                        },
                    ) {
                        Text(text = "<<")
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                TextField(
                    modifier = Modifier.width(100.dp),
                    value = velocityTextState,
                    singleLine = true,
                    onValueChange = { velocityTextState = it }
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column() {
                    Button(
                        onClick = {
                            if (ballPosX < 0 || glbbState.velocity > 0) {
                                posSlider = Offset(posSlider.x + 5f, posSlider.y)
                                glbbState.velocity -= 1
                                velocityTextState = glbbState.velocity.toString()
                            }
                        },
                    ) {
                        Text(text = ">")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            glbbState.playHorizontal(1)
                        },
                    ) {
                        Text(text = ">>")
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column() {
                    Button(
                        onClick = {
                            glbbState.playVertical()
                        },
                    ) {
                        Text(text = "W")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (ballPosY < 0 || glbbState.velocity > 0) {
                                posSlider = Offset(posSlider.x, posSlider.y - 5f)
                            }
                        },
                    ) {
                        Text(text = "V")
                    }
                }

            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Vertical Slider")
                Slider(
                    value = verticalSlider,
                    valueRange = 0f..(glbbState.size.height.coerceAtLeast(0f)),
                    onValueChange = { posSlider = Offset(posSlider.x, it) }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Horizontal Slider")
                Slider(
                    value = horizontalSlider,
                    valueRange = 0f..(glbbState.posMax().x.coerceAtLeast(0f)),
                    onValueChange = { posSlider = Offset(it, posSlider.y) }
                )
            }
        }

    }
}