package com.example.voicesense.data

import androidx.lifecycle.ViewModel
import com.example.voicesense.model.TaskHistoryItem
import kotlinx.coroutines.flow.StateFlow


class MainViewModel : ViewModel() {

    private val repository = TaskRepository()
    val tasks: StateFlow<List<TaskHistoryItem>> = repository.tasks

    fun addTask(task: TaskHistoryItem) = repository.addTask(task)
}