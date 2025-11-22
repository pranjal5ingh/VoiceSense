package com.example.voicesense.ui.screens

import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
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
fun PhotoCaptureScreen(
    viewModel: MainViewModel,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder(context) }

    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var description by remember { mutableStateOf("") }

    var lastDuration by remember { mutableStateOf<Int?>(null) }
    var audioPath by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    // True only when we have a real preview
    val imageCaptured = imageBitmap != null

    val canSubmit = imageCaptured &&
            description.isNotBlank() &&
            errorText == null

    // Camera launcher: returns a Bitmap preview
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        imageBitmap = bitmap
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Photo Capture Task", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                // Open camera to capture preview
                cameraLauncher.launch(null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (imageCaptured) "Retake Photo" else "Capture Image")
        }

        Spacer(Modifier.height(16.dp))

        // Show preview if we have one
        imageBitmap?.let { bmp ->
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "Captured photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Describe the photo in your language") },
            minLines = 3
        )

        Spacer(Modifier.height(16.dp))

        Text("Optional audio description (10–20 s)")
        Spacer(Modifier.height(8.dp))

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

        Spacer(Modifier.height(8.dp))
        lastDuration?.let { Text("Audio duration: ${it}s") }
        errorText?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val now = Instant.now().toString()
                val duration = lastDuration ?: 0

                val task = TaskHistoryItem(
                    id = System.currentTimeMillis(),
                    taskType = TaskType.PHOTO_CAPTURE,
                    text = description,
                    imagePath = "preview_bitmap_${System.currentTimeMillis()}", // placeholder id
                    audioPath = audioPath,
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