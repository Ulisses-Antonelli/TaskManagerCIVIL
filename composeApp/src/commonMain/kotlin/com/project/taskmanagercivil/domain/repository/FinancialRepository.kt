package com.project.taskmanagercivil.domain.repository

import com.project.taskmanagercivil.domain.models.CompanyFinancials
import com.project.taskmanagercivil.domain.models.FinancialTask
import com.project.taskmanagercivil.domain.models.ProjectFinancials
import kotlinx.coroutines.flow.Flow

/**
 * Repository para operações financeiras
 */
interface FinancialRepository {
    /**
     * Obtém todas as tarefas financeiras com filtros opcionais
     */
    fun getFinancialTasks(
        projectId: String? = null,
        disciplineId: String? = null,
        responsibleId: String? = null,
        status: String? = null,
        period: String? = null
    ): Flow<List<FinancialTask>>

    /**
     * Obtém os dados financeiros de um projeto específico
     */
    fun getProjectFinancials(projectId: String): Flow<ProjectFinancials?>

    /**
     * Obtém os dados financeiros da empresa
     */
    fun getCompanyFinancials(period: String = "current_month"): Flow<CompanyFinancials>

    /**
     * Atualiza os dados financeiros de uma tarefa
     * (será usado quando integrar com backend)
     */
    suspend fun updateTaskFinancials(taskId: String, actualDays: Int, actualCost: Double): Result<Unit>
}
