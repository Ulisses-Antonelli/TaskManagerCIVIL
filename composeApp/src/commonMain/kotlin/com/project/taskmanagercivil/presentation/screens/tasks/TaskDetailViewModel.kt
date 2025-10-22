package com.project.taskmanagercivil.presentation.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TaskDetailUiState(
    val task: Task? = null,
    val relatedTasks: List<Task> = emptyList(), // Tarefas do mesmo projeto
    val predecessorTasks: List<Task> = emptyList(), // Tarefas das quais esta depende
    val isLoading: Boolean = false,
    val error: String? = null
)

class TaskDetailViewModel(
    private val taskId: String,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailUiState(isLoading = true))
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    init {
        loadTask()
    }

    fun loadTask() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                // Carrega a tarefa específica
                val task = taskRepository.getTaskById(taskId)

                if (task != null) {
                    // Carrega tarefas relacionadas (do mesmo projeto)
                    val allTasks = taskRepository.getAllTasks()
                    val relatedTasks = allTasks.filter {
                        it.project.id == task.project.id && it.id != task.id
                    }

                    // Carrega tarefas predecessoras (dependências)
                    val predecessorTasks = allTasks.filter {
                        task.dependencies.contains(it.id)
                    }

                    _uiState.value = _uiState.value.copy(
                        task = task,
                        relatedTasks = relatedTasks,
                        predecessorTasks = predecessorTasks,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Tarefa não encontrada"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro ao carregar tarefa: ${e.message}"
                )
            }
        }
    }

    fun refresh() {
        loadTask()
    }
}
