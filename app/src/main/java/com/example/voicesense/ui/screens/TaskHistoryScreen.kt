package com.example.voicesense.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.voicesense.model.TaskHistoryItem
import com.example.voicesense.model.TaskType
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskHistoryScreen(
    tasksFlow: StateFlow<List<TaskHistoryItem>>,
    onBack: () -> Unit
) {
    val tasks by tasksFlow.collectAsState()

    val totalTasks = tasks.size
    val totalDuration = tasks.sumOf { it.durationSec }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Tasks: $totalTasks")
                Text("Total Duration: ${totalDuration}s")
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn {
                items(tasks) { task ->
                    TaskHistoryItemRow(task)
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun TaskHistoryItemRow(task: TaskHistoryItem) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("ID: ${task.id}", style = MaterialTheme.typography.labelSmall)
        Text(
            "Type: " + when (task.taskType) {
                TaskType.TEXT_READING -> "Text Reading"
                TaskType.IMAGE_DESCRIPTION -> "Image Description"
                TaskType.PHOTO_CAPTURE -> "Photo Capture"
            },
            style = MaterialTheme.typography.bodyMedium
        )
        Text("Duration: ${task.durationSec}s • ${task.timestampIso}")

        task.text?.takeIf { it.isNotBlank() }?.let {
            Text("Preview: ${it.take(40)}...", style = MaterialTheme.typography.bodySmall)
        }
        task.imageUrl?.let {
            Text("Image URL: $it", style = MaterialTheme.typography.bodySmall)
        }
        task.imagePath?.let {
            Text("Image Path: $it", style = MaterialTheme.typography.bodySmall)
        }
    }
}