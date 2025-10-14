package com.project.taskmanagercivil.data.repository

import com.project.taskmanagercivil.data.MockData
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.domain.repository.TaskRepository

class TaskRepositoryImpl : TaskRepository {
    private val mockData = MockData()

    override suspend fun getAllTasks(): List<Task> {
        // Simula delay de rede
        kotlinx.coroutines.delay(300)
        return mockData.tasks
    }

    override suspend fun getTaskById(id: String): Task? {
        kotlinx.coroutines.delay(100)
        return mockData.tasks.find { it.id == id }
    }
}
