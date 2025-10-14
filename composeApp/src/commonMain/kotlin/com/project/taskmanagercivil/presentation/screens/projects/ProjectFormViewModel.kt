package com.project.taskmanagercivil.presentation.screens.projects

import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.repository.ProjectRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

data class ProjectFormUiState(
    val projectId: String? = null,
    val name: String = "",
    val description: String = "",
    val client: String = "",
    val location: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val budget: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val nameError: String? = null,
    val descriptionError: String? = null,
    val clientError: String? = null,
    val locationError: String? = null,
    val budgetError: String? = null,
    val dateError: String? = null
)

class ProjectFormViewModel(
    private val projectRepository: ProjectRepository,
    private val projectId: String? = null
) {
    private val viewModelScope = CoroutineScope(SupervisorJob())

    private val _uiState = MutableStateFlow(ProjectFormUiState(projectId = projectId))
    val uiState: StateFlow<ProjectFormUiState> = _uiState.asStateFlow()

    init {
        if (projectId != null) {
            loadProject()
        } else {
        
            val today = kotlinx.datetime.Clock.System.now().toString().substring(0, 10)
            _uiState.update {
                it.copy(
                    startDate = today,
                    endDate = today
                )
            }
        }
    }

    private fun loadProject() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val project = projectRepository.getProjectById(projectId!!)
                if (project != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            name = project.name,
                            description = project.description,
                            client = project.client,
                            location = project.location,
                            startDate = project.startDate.toString(),
                            endDate = project.endDate.toString(),
                            budget = project.budget.toString()
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Projeto não encontrado")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Erro ao carregar projeto: ${e.message}")
                }
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, nameError = null) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value, descriptionError = null) }
    }

    fun onClientChange(value: String) {
        _uiState.update { it.copy(client = value, clientError = null) }
    }

    fun onLocationChange(value: String) {
        _uiState.update { it.copy(location = value, locationError = null) }
    }

    fun onStartDateChange(value: String) {
        _uiState.update { it.copy(startDate = value, dateError = null) }
    }

    fun onEndDateChange(value: String) {
        _uiState.update { it.copy(endDate = value, dateError = null) }
    }

    fun onBudgetChange(value: String) {
        _uiState.update { it.copy(budget = value, budgetError = null) }
    }

    fun saveProject(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        // Validação
        var hasError = false

        if (currentState.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Nome é obrigatório") }
            hasError = true
        }

        if (currentState.description.isBlank()) {
            _uiState.update { it.copy(descriptionError = "Descrição é obrigatória") }
            hasError = true
        }

        if (currentState.client.isBlank()) {
            _uiState.update { it.copy(clientError = "Cliente é obrigatório") }
            hasError = true
        }

        if (currentState.location.isBlank()) {
            _uiState.update { it.copy(locationError = "Localização é obrigatória") }
            hasError = true
        }

        if (currentState.budget.isBlank() || currentState.budget.toDoubleOrNull() == null) {
            _uiState.update { it.copy(budgetError = "Orçamento inválido") }
            hasError = true
        }

        // Validação de datas
        val startDate = try {
            LocalDate.parse(currentState.startDate)
        } catch (e: Exception) {
            _uiState.update { it.copy(dateError = "Data de início inválida (formato: YYYY-MM-DD)") }
            hasError = true
            null
        }

        val endDate = try {
            LocalDate.parse(currentState.endDate)
        } catch (e: Exception) {
            _uiState.update { it.copy(dateError = "Data final inválida (formato: YYYY-MM-DD)") }
            hasError = true
            null
        }

        if (startDate != null && endDate != null && startDate > endDate) {
            _uiState.update { it.copy(dateError = "Data de início não pode ser posterior à data final") }
            hasError = true
        }

        if (hasError) return

        // Salvar
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                val project = Project(
                    id = currentState.projectId ?: generateId(),
                    name = currentState.name,
                    description = currentState.description,
                    client = currentState.client,
                    location = currentState.location,
                    startDate = startDate!!,
                    endDate = endDate!!,
                    budget = currentState.budget.toDouble()
                )

                if (currentState.projectId != null) {
                    projectRepository.updateProject(project)
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            successMessage = "Projeto atualizado com sucesso!"
                        )
                    }
                } else {
                    projectRepository.createProject(project)
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            successMessage = "Projeto criado com sucesso!"
                        )
                    }
                }

                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, error = "Erro ao salvar: ${e.message}")
                }
            }
        }
    }

    private fun generateId(): String {
        return "proj_${kotlinx.datetime.Clock.System.now().toEpochMilliseconds()}"
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
