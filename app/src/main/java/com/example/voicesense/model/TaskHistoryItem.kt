package com.example.voicesense.model

data class TaskHistoryItem(
    val id: Long,
    val taskType: TaskType,
    val text: String? = null,
    val imageUrl: String? = null,
    val imagePath: String? = null,
    val audioPath: String? = null,
    val durationSec: Int,
    val timestampIso: String
)