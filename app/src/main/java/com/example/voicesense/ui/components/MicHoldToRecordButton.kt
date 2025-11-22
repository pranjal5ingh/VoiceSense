package com.example.voicesense.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun MicHoldToRecordButton(
    iconRes: Int,
    onPressStart: () -> Unit,
    onPressEnd: () -> Unit
) {
    Surface(
        shape = CircleShape,
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .size(72.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        // finger goes down
                        onPressStart()
                        try {
                            // wait until finger is lifted or gesture cancelled
                            tryAwaitRelease()
                        } finally {
                            // always called when finger is up
                            onPressEnd()
                        }
                    }
                )
            }
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "Mic",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}