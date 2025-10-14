package com.project.taskmanagercivil.domain.repository

import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.models.Task

interface ProjectRepository {
    suspend fun getAllProjects(): List<Project>
    suspend fun getProjectById(id: String): Project?
    suspend fun getTasksByProject(projectId: String): List<Task>
    suspend fun createProject(project: Project): Project
    suspend fun updateProject(project: Project): Project
    suspend fun deleteProject(projectId: String): Boolean
}
