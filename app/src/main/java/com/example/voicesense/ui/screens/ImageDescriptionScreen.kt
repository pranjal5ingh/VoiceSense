package com.example.voicesense.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.voicesense.audio.AudioRecorder
import com.example.voicesense.data.MainViewModel
import com.example.voicesense.model.TaskHistoryItem
import com.example.voicesense.model.TaskType
import com.example.voicesense.ui.components.MicHoldToRecordButton
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ImageDescriptionScreen(
    viewModel: MainViewModel,
    onDone: () -> Unit
) {
    val imageUrl = "https://picsum.photos/400/300"

    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder(context) }

    var lastDuration by remember { mutableStateOf<Int?>(null) }
    var audioPath by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    // For this task, audio is required: must have valid recording 10–20s
    val canSubmit = audioPath != null && errorText == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Image Description Task", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        val painter = rememberAsyncImagePainter(imageUrl)
        val painterState = painter.state

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painter,
                contentDescription = "Sample image",
                modifier = Modifier.fillMaxSize()
            )

            when (val state = painterState) {
                is coil.compose.AsyncImagePainter.State.Loading -> {
                    Text("Loading...", style = MaterialTheme.typography.bodySmall)
                }
                is coil.compose.AsyncImagePainter.State.Error -> {
                    val errorMsg = state.result.throwable.localizedMessage ?: "Unknown error"
                    Text(
                        text = "Failed: $errorMsg",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                else -> Unit
            }
        }

        Spacer(Modifier.height(12.dp))
        Text("Instruction: Describe what you see in your native language.")

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            MicHoldToRecordButton(
                iconRes = android.R.drawable.ic_btn_speak_now,
                onPressStart = {
                    val ok = audioRecorder.startRecording()
                    lastDuration = null
                    audioPath = null
                    errorText = null
                    if (!ok) {
                        errorText = "Unable to start recording.\nCheck microphone permission or if another app is using the mic."
                    }
                },
                onPressEnd = {
                    val result = audioRecorder.stopRecording()
                    if (result != null) {
                        lastDuration = result.durationSec
                        audioPath = result.filePath
                        errorText = when {
                            result.durationSec < 5 -> "Recording too short (min 5 s)."
                            result.durationSec > 20 -> "Recording too long (max 20 s)."
                            else -> null
                        }
                    } else if (errorText == null) {
                        errorText = "Recording failed. Please try again."
                    }
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        lastDuration?.let {
            Text("Recorded duration: ${it}s")
        }
        errorText?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val duration = lastDuration ?: return@Button
                val path = audioPath ?: return@Button
                val now = Instant.now().toString()

                val task = TaskHistoryItem(
                    id = System.currentTimeMillis(),
                    taskType = TaskType.IMAGE_DESCRIPTION,
                    imageUrl = imageUrl,
                    audioPath = path,
                    durationSec = duration,
                    timestampIso = now
                )
                viewModel.addTask(task)
                onDone()
            },
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}