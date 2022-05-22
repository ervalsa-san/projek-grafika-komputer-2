package com.ervalsa.projek_grafika_komputer_2.ui.feature.glbb

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun GlbbScreen() {


    // State
    var velocityTextState by remember { mutableStateOf(500f.toString()) }
    val glbbState by remember { mutableStateOf(GlbbState()) }
    val horizontalSlider = glbbState.pos.x
    val verticalSlider = glbbState.pos.y

    var velocity = 100f
    var ballPosX = glbbState.pos.x
    val ballPosY = glbbState.pos.y
    val size = glbbState.size
    val gravity = 1

    var velocityFloat by remember { mutableStateOf(velocity) }
    velocityTextState.toFloatOrNull()?.let {
        velocityFloat = it
    }

    fun stepLeft() {
        if (glbbState.pos.x < 0 || glbbState.pos.x < size.width) {
            glbbState.pos = Offset(ballPosX - 20f, ballPosY)
            glbbState.clamp()
        }
    }

    fun stepRight() {
        if (glbbState.pos.x < 0 || glbbState.pos.x < size.width) {
            glbbState.pos = Offset(ballPosX + 20f, ballPosY)
            glbbState.clamp()
        }
    }

    fun moveRight() {
        if (velocity != 0f) {
            ballPosX += velocity
            if  (ballPosX < 0 || ballPosX > size.width) {
                velocity = -velocity
                glbbState.clamp()
            }
        }
    }

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
                                    awaitFirstDown();
                                    do {
                                        //This PointerEvent contains details including
                                        // event, id, position and more
                                        val event = awaitPointerEvent()
                                        // ACTION_MOVE loop

                                        // Consuming event prevents other gestures or scroll to intercept
                                        event.changes.forEach { change ->
                                            change.consumePositionChange()
                                            glbbState.setPosClamped(glbbState.posFromScreen(change.position));
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
                            stepLeft()
                        },
                    ) {
                        Text(text = "<")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {

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
                            stepRight()
                        },
                    ) {
                        Text(text = ">")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            moveRight()
                            print(moveRight())
                        },
                    ) {
                        Text(text = ">>")
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column() {
                    Button(
                        onClick = {

                        },
                    ) {
                        Text(text = "W")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if  (glbbState.pos.y < 0 || glbbState.pos.y < size.height) {
                                glbbState.pos = Offset(ballPosX , ballPosY - 20f)
                                glbbState.clamp()
                                println(glbbState.pos.y)
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
                    valueRange = 0f..(glbbState.posMax().y.coerceAtLeast(0f)),
                    onValueChange = { glbbState.pos = Offset(glbbState.pos.x, it) }
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
                    onValueChange = { glbbState.pos = Offset(it, glbbState.pos.y) }
                )
            }
        }

    }
}