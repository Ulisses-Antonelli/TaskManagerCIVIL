package com.project.taskmanagercivil.domain.repository

import com.project.taskmanagercivil.domain.models.Task

interface TaskRepository {
    suspend fun getAllTasks(): List<Task>
    suspend fun getTaskById(id: String): Task?
    suspend fun createTask(task: Task): Task
    suspend fun updateTask(task: Task): Task
    suspend fun deleteTask(taskId: String)
}
