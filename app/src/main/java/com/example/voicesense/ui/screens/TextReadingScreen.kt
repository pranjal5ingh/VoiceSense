package com.example.voicesense.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.voicesense.audio.AudioRecorder
import com.example.voicesense.data.MainViewModel
import com.example.voicesense.model.TaskHistoryItem
import com.example.voicesense.model.TaskType
import com.example.voicesense.ui.components.MicHoldToRecordButton
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TextReadingScreen(
    viewModel: MainViewModel,
    onDone: () -> Unit
) {
    val passage =
        "Mega long lasting fragrance for your everyday freshness and confidence."

    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder(context) }

    var lastDuration by remember { mutableStateOf<Int?>(null) }
    var audioPath by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var hasNoNoise by remember { mutableStateOf(false) }
    var noMistakes by remember { mutableStateOf(false) }
    var beechMeNoMistake by remember { mutableStateOf(false) }

    val canSubmit = lastDuration != null &&
            errorText == null &&
            hasNoNoise && noMistakes && beechMeNoMistake &&
            audioPath != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Text Reading Task", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        Text(passage, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(12.dp))

        Text("Instruction: Read the passage aloud in your native language.")
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

        Spacer(Modifier.height(16.dp))

        Text("Self-check before submitting:")
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = hasNoNoise, onCheckedChange = { hasNoNoise = it })
            Text("No background noise")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = noMistakes, onCheckedChange = { noMistakes = it })
            Text("No mistakes while reading")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = beechMeNoMistake,
                onCheckedChange = { beechMeNoMistake = it }
            )
            Text("Beech me koi galti nahi hai")
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = {
                    lastDuration = null
                    audioPath = null
                    errorText = null
                    hasNoNoise = false
                    noMistakes = false
                    beechMeNoMistake = false
                    audioRecorder.stopIfRunning()
                }
            ) {
                Text("Record again")
            }

            Button(
                onClick = {
                    val duration = lastDuration ?: return@Button
                    val path = audioPath ?: return@Button
                    val now = Instant.now().toString()

                    val task = TaskHistoryItem(
                        id = System.currentTimeMillis(),
                        taskType = TaskType.TEXT_READING,
                        text = passage,
                        audioPath = path,
                        durationSec = duration,
                        timestampIso = now
                    )
                    viewModel.addTask(task)
                    onDone()
                },
                enabled = canSubmit
            ) {
                Text("Submit")
            }
        }
    }
}