package com.project.taskmanagercivil.presentation.screens.tasks

import com.project.taskmanagercivil.data.MockData
import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.domain.models.TaskPriority
import com.project.taskmanagercivil.domain.models.TaskStatus
import com.project.taskmanagercivil.domain.models.User
import com.project.taskmanagercivil.domain.repository.ProjectRepository
import com.project.taskmanagercivil.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

enum class ViewMode {
    LIST,
    KANBAN
}

data class TasksUiState(
    val allTasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val allProjects: List<Project> = emptyList(),
    val selectedStatus: TaskStatus? = null,
    val selectedPriority: TaskPriority? = null,
    val searchQuery: String = "",
    val viewMode: ViewMode = ViewMode.LIST,
    val isLoading: Boolean = false,
    val error: String? = null
)


class TasksViewModel(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository
) {
    private val viewModelScope = CoroutineScope(SupervisorJob())
    private val mockData = MockData()

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
        loadProjects()
    }

    fun getProjects(): List<Project> {
        return _uiState.value.allProjects
    }

    private fun loadProjects() {
        viewModelScope.launch {
            try {
                val projects = projectRepository.getAllProjects()
                _uiState.update { it.copy(allProjects = projects) }
            } catch (e: Exception) {
                // Silently fail, projects loading is not critical
                println("Error loading projects: ${e.message}")
            }
        }
    }

    fun getUsers(): List<User> {
        return mockData.users
    }

    fun saveTask(task: Task, checklistItems: List<ChecklistItemData>) {
        viewModelScope.launch {
            try {
                if (task.id.isEmpty()) {
                    // Create new task with generated ID
                    val allTasks = taskRepository.getAllTasks()
                    val maxId = allTasks.mapNotNull { it.id.toIntOrNull() }.maxOrNull() ?: 0
                    val newTaskId = (maxId + 1).toString()
                    val newTask = task.copy(id = newTaskId)
                    taskRepository.createTask(newTask)
                } else {
                    // Update existing task
                    taskRepository.updateTask(task)
                }
                // Reload tasks and projects to refresh the UI
                loadTasks()
                loadProjects()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Falha ao salvar tarefa: ${e.message}") }
            }
        }
    }

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val tasks = taskRepository.getAllTasks()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        allTasks = tasks
                    )
                }
                applyFilters()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Falha ao carregar as tarefas: ${e.message}") }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onStatusFilterChange(status: TaskStatus?) {
        _uiState.update { it.copy(selectedStatus = status) }
        applyFilters()
    }

    fun onPriorityFilterChange(priority: TaskPriority?) {
        _uiState.update { it.copy(selectedPriority = priority) }
        applyFilters()
    }

    fun onViewModeChange(viewMode: ViewMode) {
        _uiState.update { it.copy(viewMode = viewMode) }
    }

    fun createRevisionAndUpdateStatus(task: Task, description: String, newStatus: TaskStatus) {
        viewModelScope.launch {
            try {
                val currentRevisionNumber = task.revisions.maxOfOrNull { it.revisionNumber } ?: -1
                val newRevisionNumber = currentRevisionNumber + 1

                val newRevision = com.project.taskmanagercivil.domain.models.TaskRevision(
                    revisionNumber = newRevisionNumber,
                    author = task.assignedTo.name,
                    description = description,
                    startDate = task.startDate,
                    deliveryDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
                )

                val updatedTask = task.copy(
                    status = newStatus,
                    revisions = task.revisions + newRevision
                )

                taskRepository.updateTask(updatedTask)
                loadTasks()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Falha ao criar revisão: ${e.message}") }
            }
        }
    }

    private fun applyFilters() {
        val currentState = _uiState.value
        val filtered = currentState.allTasks.filter { task ->
            val matchesSearch = if (currentState.searchQuery.isNotBlank()) {
                task.title.contains(currentState.searchQuery, ignoreCase = true) ||
                        task.description.contains(currentState.searchQuery, ignoreCase = true)
            } else {
                true
            }

            val matchesStatus = if (currentState.selectedStatus != null) {
                task.status == currentState.selectedStatus
            } else {
                true
            }

            val matchesPriority = if (currentState.selectedPriority != null) {
                task.priority == currentState.selectedPriority
            } else {
                true
            }

            matchesSearch && matchesStatus && matchesPriority
        }
        _uiState.update { it.copy(filteredTasks = filtered) }
    }
}