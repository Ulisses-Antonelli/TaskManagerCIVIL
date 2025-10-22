package com.project.taskmanagercivil.data.repository

import com.project.taskmanagercivil.data.MockData
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.domain.repository.TaskRepository

class TaskRepositoryImpl : TaskRepository {
    private val mockData = MockData()
    private val _tasks = mockData.tasks.toMutableList()

    override suspend fun getAllTasks(): List<Task> {
        // Simula delay de rede
        kotlinx.coroutines.delay(300)
        return _tasks.toList()
    }

    override suspend fun getTaskById(id: String): Task? {
        kotlinx.coroutines.delay(100)
        return _tasks.find { it.id == id }
    }

    override suspend fun createTask(task: Task): Task {
        kotlinx.coroutines.delay(200)
        _tasks.add(task)
        return task
    }

    override suspend fun updateTask(task: Task): Task {
        kotlinx.coroutines.delay(200)
        val index = _tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            _tasks[index] = task
        }
        return task
    }

    override suspend fun deleteTask(taskId: String) {
        kotlinx.coroutines.delay(200)
        _tasks.removeAll { it.id == taskId }
    }
}
