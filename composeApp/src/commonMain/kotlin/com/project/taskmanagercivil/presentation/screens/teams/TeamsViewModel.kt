package com.project.taskmanagercivil.presentation.screens.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.taskmanagercivil.domain.models.Team
import com.project.taskmanagercivil.domain.models.TeamDepartment
import com.project.taskmanagercivil.domain.repository.TeamRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


data class TeamsUiState(
    val teams: List<Team> = emptyList(),
    val filteredTeams: List<Team> = emptyList(),
    val searchQuery: String = "",
    val filterDepartment: TeamFilterDepartment = TeamFilterDepartment.ALL,
    val sortOrder: TeamSortOrder = TeamSortOrder.NAME_ASC,
    val isLoading: Boolean = false
)


enum class TeamFilterDepartment {
    ALL,                // Todos
    ARCHITECTURE,       // Arquitetura
    STRUCTURE,          // Estruturas/Fundações
    HYDRAULIC,          // Hidráulica - Obra
    ELECTRICAL,         // Elétrica - Obra
    MASONRY,            // Alvenaria
    FINISHING,          // Acabamento
    CLEANING,           // Limpeza/Canteiro de Obras
    SAFETY,             // Segurança do Trabalho
    ADMINISTRATION,     // Administração
    PURCHASING,         // Compras
    QUALITY,            // Qualidade
    PLANNING            // Planejamento
}


enum class TeamSortOrder(val displayName: String) {
    NAME_ASC("Nome (A-Z)"),
    NAME_DESC("Nome (Z-A)"),
    MEMBERS_COUNT("Número de Membros"),
    PROJECTS_COUNT("Número de Projetos"),
    CREATED_DATE("Data de Criação")
}


class TeamsViewModel(
    private val repository: TeamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeamsUiState())
    val uiState: StateFlow<TeamsUiState> = _uiState.asStateFlow()

    init {
        loadTeams()
    }

    private fun loadTeams() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getAllTeams().collect { teams ->
                _uiState.update { currentState ->
                    currentState.copy(
                        teams = teams,
                        filteredTeams = applyFiltersAndSort(
                            teams,
                            currentState.searchQuery,
                            currentState.filterDepartment,
                            currentState.sortOrder
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredTeams = applyFiltersAndSort(
                    currentState.teams,
                    query,
                    currentState.filterDepartment,
                    currentState.sortOrder
                )
            )
        }
    }

    fun onFilterDepartmentChange(department: TeamFilterDepartment) {
        _uiState.update { currentState ->
            currentState.copy(
                filterDepartment = department,
                filteredTeams = applyFiltersAndSort(
                    currentState.teams,
                    currentState.searchQuery,
                    department,
                    currentState.sortOrder
                )
            )
        }
    }

    fun onSortOrderChange(sortOrder: TeamSortOrder) {
        _uiState.update { currentState ->
            currentState.copy(
                sortOrder = sortOrder,
                filteredTeams = applyFiltersAndSort(
                    currentState.teams,
                    currentState.searchQuery,
                    currentState.filterDepartment,
                    sortOrder
                )
            )
        }
    }

    private fun applyFiltersAndSort(
        teams: List<Team>,
        searchQuery: String,
        filterDepartment: TeamFilterDepartment,
        sortOrder: TeamSortOrder
    ): List<Team> {
        var result = teams

        
        result = when (filterDepartment) {
            TeamFilterDepartment.ALL -> result
            TeamFilterDepartment.ARCHITECTURE -> result.filter { it.department == TeamDepartment.ARCHITECTURE }
            TeamFilterDepartment.STRUCTURE -> result.filter { it.department == TeamDepartment.STRUCTURE }
            TeamFilterDepartment.HYDRAULIC -> result.filter { it.department == TeamDepartment.HYDRAULIC }
            TeamFilterDepartment.ELECTRICAL -> result.filter { it.department == TeamDepartment.ELECTRICAL }
            TeamFilterDepartment.MASONRY -> result.filter { it.department == TeamDepartment.MASONRY }
            TeamFilterDepartment.FINISHING -> result.filter { it.department == TeamDepartment.FINISHING }
            TeamFilterDepartment.CLEANING -> result.filter { it.department == TeamDepartment.CLEANING }
            TeamFilterDepartment.SAFETY -> result.filter { it.department == TeamDepartment.SAFETY }
            TeamFilterDepartment.ADMINISTRATION -> result.filter { it.department == TeamDepartment.ADMINISTRATION }
            TeamFilterDepartment.PURCHASING -> result.filter { it.department == TeamDepartment.PURCHASING }
            TeamFilterDepartment.QUALITY -> result.filter { it.department == TeamDepartment.QUALITY }
            TeamFilterDepartment.PLANNING -> result.filter { it.department == TeamDepartment.PLANNING }
        }

        
        if (searchQuery.isNotBlank()) {
            result = result.filter { team ->
                team.name.contains(searchQuery, ignoreCase = true) ||
                team.description.contains(searchQuery, ignoreCase = true)
            }
        }

        
        result = when (sortOrder) {
            TeamSortOrder.NAME_ASC -> result.sortedBy { it.name }
            TeamSortOrder.NAME_DESC -> result.sortedByDescending { it.name }
            TeamSortOrder.MEMBERS_COUNT -> result.sortedByDescending { it.getTotalMembers() }
            TeamSortOrder.PROJECTS_COUNT -> result.sortedByDescending { it.getTotalProjects() }
            TeamSortOrder.CREATED_DATE -> result.sortedByDescending { it.createdDate }
        }

        return result
    }

    fun deleteTeam(teamId: String) {
        viewModelScope.launch {
            repository.deleteTeam(teamId)
        }
    }
}
