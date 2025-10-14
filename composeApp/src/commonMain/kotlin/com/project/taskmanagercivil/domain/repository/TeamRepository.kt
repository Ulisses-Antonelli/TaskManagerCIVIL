package com.project.taskmanagercivil.domain.repository

import com.project.taskmanagercivil.domain.models.Team
import kotlinx.coroutines.flow.Flow

/**
 * Interface de repositório para operações CRUD de times
 */
interface TeamRepository {
    fun getAllTeams(): Flow<List<Team>>
    fun getTeamById(id: String): Team?
    fun getTeamsByProject(projectId: String): List<Team>
    fun getTeamsByDepartment(department: String): List<Team>
    fun addTeam(team: Team)
    fun updateTeam(team: Team)
    fun deleteTeam(id: String)
}
