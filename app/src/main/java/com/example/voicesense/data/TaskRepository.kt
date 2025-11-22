package com.example.voicesense.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.voicesense.model.TaskHistoryItem

class TaskRepository {
    private val _tasks = MutableStateFlow<List<TaskHistoryItem>>(emptyList())
    val tasks: StateFlow<List<TaskHistoryItem>> = _tasks

    fun addTask(task: TaskHistoryItem) {
        _tasks.value = _tasks.value + task
    }
}
