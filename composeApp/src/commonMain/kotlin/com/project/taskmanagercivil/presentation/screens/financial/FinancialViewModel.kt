package com.project.taskmanagercivil.presentation.screens.financial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.taskmanagercivil.domain.models.CompanyFinancials
import com.project.taskmanagercivil.domain.models.FinancialTask
import com.project.taskmanagercivil.domain.models.ProjectFinancials
import com.project.taskmanagercivil.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FinancialUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    // Painel de Tarefas
    val tasksPanelTasks: List<FinancialTask> = emptyList(),
    val selectedProjectFilter: String? = null,
    val selectedDisciplineFilter: String? = null,
    val selectedResponsibleFilter: String? = null,
    val selectedStatusFilter: String? = null,
    val selectedPeriodFilter: String? = null,

    // Painel de Projetos
    val selectedProjectId: String? = null,
    val projectFinancials: ProjectFinancials? = null,

    // Painel da Empresa
    val companyFinancials: CompanyFinancials? = null,
    val companyPeriod: String = "current_month"
)

class FinancialViewModel(
    private val financialRepository: FinancialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinancialUiState())
    val uiState: StateFlow<FinancialUiState> = _uiState.asStateFlow()

    init {
        loadAllFinancialData()
    }

    private fun loadAllFinancialData() {
        loadTasksPanelData()
        loadProjectFinancials("p1") // Default project
        loadCompanyFinancials()
    }

    // ========== PAINEL DE TAREFAS ==========

    fun loadTasksPanelData(
        projectId: String? = null,
        disciplineId: String? = null,
        responsibleId: String? = null,
        status: String? = null,
        period: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            financialRepository.getFinancialTasks(
                projectId = projectId,
                disciplineId = disciplineId,
                responsibleId = responsibleId,
                status = status,
                period = period
            ).catch { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Erro ao carregar tarefas: ${e.message}"
                    )
                }
            }.collect { tasks ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        tasksPanelTasks = tasks,
                        selectedProjectFilter = projectId,
                        selectedDisciplineFilter = disciplineId,
                        selectedResponsibleFilter = responsibleId,
                        selectedStatusFilter = status,
                        selectedPeriodFilter = period
                    )
                }
            }
        }
    }

    fun onFilterChange(
        projectId: String? = _uiState.value.selectedProjectFilter,
        disciplineId: String? = _uiState.value.selectedDisciplineFilter,
        responsibleId: String? = _uiState.value.selectedResponsibleFilter,
        status: String? = _uiState.value.selectedStatusFilter,
        period: String? = _uiState.value.selectedPeriodFilter
    ) {
        loadTasksPanelData(projectId, disciplineId, responsibleId, status, period)
    }

    // ========== PAINEL DE PROJETOS ==========

    fun loadProjectFinancials(projectId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            financialRepository.getProjectFinancials(projectId)
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Erro ao carregar dados do projeto: ${e.message}"
                        )
                    }
                }
                .collect { projectFinancials ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            selectedProjectId = projectId,
                            projectFinancials = projectFinancials
                        )
                    }
                }
        }
    }

    fun onProjectSelected(projectId: String) {
        loadProjectFinancials(projectId)
    }

    // ========== PAINEL DA EMPRESA ==========

    fun loadCompanyFinancials(period: String = "current_month") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            financialRepository.getCompanyFinancials(period)
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Erro ao carregar dados da empresa: ${e.message}"
                        )
                    }
                }
                .collect { companyFinancials ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            companyFinancials = companyFinancials,
                            companyPeriod = period
                        )
                    }
                }
        }
    }

    fun onCompanyPeriodChange(period: String) {
        loadCompanyFinancials(period)
    }
}
