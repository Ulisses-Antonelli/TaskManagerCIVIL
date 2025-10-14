package com.project.taskmanagercivil.data.repository

import com.project.taskmanagercivil.data.MockData
import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.domain.repository.ProjectRepository
import kotlinx.coroutines.delay

class ProjectRepositoryImpl : ProjectRepository {
    private val mockData = MockData()

    // Lista mutável para simular CRUD (em memória)
    private val projects = mockData.projects.toMutableList()
    private val tasks = mockData.tasks.toMutableList()

    override suspend fun getAllProjects(): List<Project> {
        delay(200) // Simula delay de rede
        return projects.toList()
    }

    override suspend fun getProjectById(id: String): Project? {
        delay(100)
        return projects.find { it.id == id }
    }

    override suspend fun getTasksByProject(projectId: String): List<Task> {
        delay(150)
        return tasks.filter { it.project.id == projectId }
    }

    override suspend fun createProject(project: Project): Project {
        delay(300)
        projects.add(project)
        return project
    }

    override suspend fun updateProject(project: Project): Project {
        delay(250)
        val index = projects.indexOfFirst { it.id == project.id }
        if (index != -1) {
            projects[index] = project
        }
        return project
    }

    override suspend fun deleteProject(projectId: String): Boolean {
        delay(200)
        val projectToRemove = projects.find { it.id == projectId }
        return if (projectToRemove != null) {
            projects.remove(projectToRemove)
        } else {
            false
        }
    }
}
