package com.project.taskmanagercivil.data.repository

import com.project.taskmanagercivil.data.remote.api.DashboardApiService
import com.project.taskmanagercivil.data.remote.dto.dashboard.*
import com.project.taskmanagercivil.domain.models.*
import com.project.taskmanagercivil.domain.repository.DashboardRepository
import kotlinx.datetime.LocalDate

/**
 * Implementação do repositório de dashboard com integração ao backend
 *
 * Esta implementação se conecta ao backend via DashboardApiService
 * e faz a conversão entre DTOs (camada de dados) e domain models
 *
 * ⚠️ ARQUITETURA PROFISSIONAL:
 * O backend calcula TODOS os dados agregados e retorna pronto.
 * O frontend apenas converte DTOs para domain models e exibe.
 *
 * Vantagens desta abordagem:
 * - Performance: 1 requisição ao invés de múltiplas
 * - Segurança: Lógica de negócio no backend
 * - Escalabilidade: Backend pode cachear resultados
 * - Manutenção: Regras de cálculo centralizadas
 *
 * @param dashboardApiService Serviço de API para dashboard
 */
class DashboardRepositoryImpl(
    private val dashboardApiService: DashboardApiService = DashboardApiService()
) : DashboardRepository {

    /**
     * Busca dados do dashboard do backend
     *
     * O backend retorna todos os dados calculados:
     * - Contagens de projetos por status
     * - Estatísticas de progresso (total, concluídas, média, atrasadas)
     * - Prazos críticos (próximos 7 dias ou atrasados)
     * - Indicadores financeiros por projeto (filtrado por permissão)
     * - Dados mensais para gráficos (últimos 6 meses)
     *
     * @return DashboardData com todos os dados do dashboard
     * @throws Exception se houver erro de rede ou autenticação
     */
    override suspend fun getDashboardData(): DashboardData {
        return try {
            val dashboardDto = dashboardApiService.getDashboardData()
            dashboardDto.toDomainModel()
        } catch (e: Exception) {
            println("❌ Erro ao buscar dados do dashboard: ${e.message}")
            throw e
        }
    }

    /**
     * Busca dados do dashboard para um período específico
     *
     * @param startDate Data inicial no formato "yyyy-MM-dd"
     * @param endDate Data final no formato "yyyy-MM-dd"
     * @return DashboardData com dados do período
     * @throws Exception se houver erro
     */
    suspend fun getDashboardDataByPeriod(startDate: String, endDate: String): DashboardData {
        return try {
            val dashboardDto = dashboardApiService.getDashboardDataByPeriod(startDate, endDate)
            dashboardDto.toDomainModel()
        } catch (e: Exception) {
            println("❌ Erro ao buscar dados do dashboard por período: ${e.message}")
            throw e
        }
    }

    /**
     * Busca dados do dashboard para um projeto específico
     *
     * @param projectId ID do projeto
     * @return DashboardData focado no projeto
     * @throws Exception se houver erro
     */
    suspend fun getDashboardDataByProject(projectId: String): DashboardData {
        return try {
            val dashboardDto = dashboardApiService.getDashboardDataByProject(projectId)
            dashboardDto.toDomainModel()
        } catch (e: Exception) {
            println("❌ Erro ao buscar dados do dashboard do projeto $projectId: ${e.message}")
            throw e
        }
    }

    // ========== Conversões DTO ↔ Domain Model ==========

    /**
     * Converte DashboardDto (backend) para DashboardData (domain model)
     */
    private fun DashboardDto.toDomainModel(): DashboardData {
        return DashboardData(
            projectStatusSummary = this.projectStatusSummary.toDomainModel(),
            progressStats = this.progressStats.toDomainModel(),
            criticalDeadlines = this.criticalDeadlines.map { it.toDomainModel() },
            financialIndicators = this.financialIndicators.map { it.toDomainModel() },
            monthlyProjectData = this.monthlyProjectData.map { it.toDomainModel() }
        )
    }

    private fun ProjectStatusSummaryDto.toDomainModel(): ProjectStatusSummary {
        return ProjectStatusSummary(
            todoCount = this.todoCount,
            inProgressCount = this.inProgressCount,
            inReviewCount = this.inReviewCount,
            completedCount = this.completedCount,
            blockedCount = this.blockedCount
        )
    }

    private fun ProgressStatsDto.toDomainModel(): ProgressStats {
        return ProgressStats(
            totalTasks = this.totalTasks,
            completedTasks = this.completedTasks,
            averageProgress = this.averageProgress,
            tasksOverdue = this.tasksOverdue
        )
    }

    private fun CriticalDeadlineDto.toDomainModel(): CriticalDeadline {
        // TODO: Buscar User real do UserRepository quando disponível
        val tempUser = User(
            id = this.assignedToId,
            name = this.assignedToName,
            email = "user@example.com",
            username = "user_${this.assignedToId}",
            roles = listOf(Role.FUNCIONARIO),
            avatarUrl = null,
            isActive = true
        )

        return CriticalDeadline(
            projectId = this.projectId,
            projectName = this.projectName,
            taskId = this.taskId,
            taskTitle = this.taskTitle,
            dueDate = LocalDate.parse(this.dueDate),
            daysRemaining = this.daysRemaining,
            priority = TaskPriority.valueOf(this.priority),
            assignedTo = tempUser
        )
    }

    private fun FinancialIndicatorDto.toDomainModel(): FinancialIndicator {
        return FinancialIndicator(
            projectId = this.projectId,
            projectName = this.projectName,
            budgeted = this.budgeted,
            spent = this.spent,
            spentPercentage = this.spentPercentage,
            remaining = this.remaining,
            isOverBudget = this.isOverBudget
        )
    }

    private fun MonthlyProjectDataDto.toDomainModel(): MonthlyProjectData {
        return MonthlyProjectData(
            month = this.month,
            year = this.year,
            monthNumber = this.monthNumber,
            todoCount = this.todoCount,
            inProgressCount = this.inProgressCount,
            inReviewCount = this.inReviewCount,
            completedCount = this.completedCount,
            blockedCount = this.blockedCount
        )
    }
}
