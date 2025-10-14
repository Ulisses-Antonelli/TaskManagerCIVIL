package com.project.taskmanagercivil.data.repository

import com.project.taskmanagercivil.data.MockData
import com.project.taskmanagercivil.domain.models.Team
import com.project.taskmanagercivil.domain.repository.TeamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementação do repositório de times usando dados mockados
 */
class TeamRepositoryImpl : TeamRepository {
    private val mockData = MockData()
    private val _teams = MutableStateFlow(mockData.teams)
    private val teams: Flow<List<Team>> = _teams.asStateFlow()

    override fun getAllTeams(): Flow<List<Team>> = teams

    override fun getTeamById(id: String): Team? {
        return _teams.value.find { it.id == id }
    }

    override fun getTeamsByProject(projectId: String): List<Team> {
        return _teams.value.filter { projectId in it.projectIds }
    }

    override fun getTeamsByDepartment(department: String): List<Team> {
        return _teams.value.filter { it.department.name == department }
    }

    override fun addTeam(team: Team) {
        val currentList = _teams.value.toMutableList()
        currentList.add(team)
        _teams.value = currentList
    }

    override fun updateTeam(team: Team) {
        val currentList = _teams.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == team.id }
        if (index != -1) {
            currentList[index] = team
            _teams.value = currentList
        }
    }

    override fun deleteTeam(id: String) {
        val currentList = _teams.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList.removeAt(index)
            _teams.value = currentList
        }
    }
}
