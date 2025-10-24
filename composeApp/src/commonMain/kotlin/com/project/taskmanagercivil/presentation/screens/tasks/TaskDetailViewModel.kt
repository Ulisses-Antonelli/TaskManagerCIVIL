package com.project.taskmanagercivil.presentation.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

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

    fun deliverTask(description: String) {
        viewModelScope.launch {
            try {
                val task = _uiState.value.task ?: return@launch

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
                    status = com.project.taskmanagercivil.domain.models.TaskStatus.COMPLETED,
                    revisions = task.revisions + newRevision
                )

                taskRepository.updateTask(updatedTask)
                loadTask() // Reload to refresh UI
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Erro ao entregar tarefa: ${e.message}"
                )
            }
        }
    }

    fun requestRevision(description: String) {
        viewModelScope.launch {
            try {
                val task = _uiState.value.task ?: return@launch

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
                    status = com.project.taskmanagercivil.domain.models.TaskStatus.IN_REVIEW,
                    revisions = task.revisions + newRevision
                )

                taskRepository.updateTask(updatedTask)
                loadTask() // Reload to refresh UI
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Erro ao solicitar revisão: ${e.message}"
                )
            }
        }
    }

    fun createPartialDelivery(description: String, completedItems: Int, totalItems: Int, checklistItems: List<com.project.taskmanagercivil.domain.models.ChecklistItem>) {
        viewModelScope.launch {
            try {
                val task = _uiState.value.task ?: return@launch

                // Se todos os itens estão completos, cria revisão e marca como COMPLETED
                if (completedItems >= totalItems) {
                    val currentRevisionNumber = task.revisions.maxOfOrNull { it.revisionNumber } ?: -1
                    val newRevisionNumber = currentRevisionNumber + 1

                    val newRevision = com.project.taskmanagercivil.domain.models.TaskRevision(
                        revisionNumber = newRevisionNumber,
                        author = task.assignedTo.name,
                        description = description,
                        startDate = task.startDate,
                        deliveryDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
                    )

                    val currentDeliveryNumber = task.partialDeliveries.maxOfOrNull { it.deliveryNumber } ?: 0
                    val newDeliveryNumber = currentDeliveryNumber + 1

                    val newPartialDelivery = com.project.taskmanagercivil.domain.models.PartialDelivery(
                        deliveryNumber = newDeliveryNumber,
                        author = task.assignedTo.name,
                        description = description,
                        deliveryDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                        completedItems = completedItems,
                        totalItems = totalItems
                    )

                    val updatedTask = task.copy(
                        partialDeliveries = task.partialDeliveries + newPartialDelivery,
                        revisions = task.revisions + newRevision,
                        status = com.project.taskmanagercivil.domain.models.TaskStatus.COMPLETED,
                        checklistItems = checklistItems
                    )

                    taskRepository.updateTask(updatedTask)
                    loadTask()
                } else {
                    // Entrega parcial normal - apenas IN_PROGRESS
                    val currentDeliveryNumber = task.partialDeliveries.maxOfOrNull { it.deliveryNumber } ?: 0
                    val newDeliveryNumber = currentDeliveryNumber + 1

                    val newPartialDelivery = com.project.taskmanagercivil.domain.models.PartialDelivery(
                        deliveryNumber = newDeliveryNumber,
                        author = task.assignedTo.name,
                        description = description,
                        deliveryDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                        completedItems = completedItems,
                        totalItems = totalItems
                    )

                    val updatedTask = task.copy(
                        partialDeliveries = task.partialDeliveries + newPartialDelivery,
                        status = com.project.taskmanagercivil.domain.models.TaskStatus.IN_PROGRESS,
                        checklistItems = checklistItems
                    )

                    taskRepository.updateTask(updatedTask)
                    loadTask()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Erro ao criar entrega parcial: ${e.message}"
                )
            }
        }
    }
}
