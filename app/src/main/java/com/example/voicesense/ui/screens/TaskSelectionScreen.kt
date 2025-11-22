package com.example.voicesense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TaskSelectionScreen(
    onTextReading: () -> Unit,
    onImageDescription: () -> Unit,
    onPhotoCapture: () -> Unit,
    onHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Choose a Sample Task", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onTextReading
        ) { Text("Text Reading Task") }

        Spacer(Modifier.height(12.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onImageDescription
        ) { Text("Image Description Task") }

        Spacer(Modifier.height(12.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onPhotoCapture
        ) { Text("Photo Capture Task") }

        Spacer(Modifier.weight(1f))

        TextButton(onClick = onHistory) {
            Text("View Task History")
        }
    }
}