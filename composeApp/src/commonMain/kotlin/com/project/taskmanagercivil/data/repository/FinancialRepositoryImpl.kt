package com.project.taskmanagercivil.data.repository

import com.project.taskmanagercivil.data.remote.api.FinancialApiService
import com.project.taskmanagercivil.data.remote.dto.financial.*
import com.project.taskmanagercivil.domain.models.*
import com.project.taskmanagercivil.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementação do repositório financeiro com integração ao backend
 *
 * Esta implementação se conecta ao backend via FinancialApiService
 * e faz a conversão entre DTOs (camada de dados) e domain models
 *
 * ⚠️ SEGURANÇA CRÍTICA: Dados financeiros são ALTAMENTE sensíveis
 * O backend valida permissões rigorosamente baseado no JWT token
 *
 * @param financialApiService Serviço de API para dados financeiros
 */
class FinancialRepositoryImpl(
    private val financialApiService: FinancialApiService = FinancialApiService()
) : FinancialRepository {

    /**
     * Obtém todas as tarefas financeiras com filtros opcionais
     *
     * @param projectId ID do projeto (opcional)
     * @param disciplineId ID da disciplina (opcional)
     * @param responsibleId ID do responsável (opcional)
     * @param status Status da tarefa (opcional)
     * @param period Período para filtrar (opcional)
     * @return Flow com lista de FinancialTask
     */
    override fun getFinancialTasks(
        projectId: String?,
        disciplineId: String?,
        responsibleId: String?,
        status: String?,
        period: String?
    ): Flow<List<FinancialTask>> = flow {
        try {
            val tasksDto = financialApiService.getFinancialTasks(
                projectId = projectId,
                disciplineId = disciplineId,
                responsibleId = responsibleId,
                status = status,
                period = period
            )
            emit(tasksDto.map { it.toDomainModel() })
        } catch (e: Exception) {
            println("❌ Erro ao buscar tarefas financeiras: ${e.message}")
            emit(emptyList())
        }
    }

    /**
     * Obtém os dados financeiros de um projeto específico
     *
     * @param projectId ID do projeto
     * @return Flow com ProjectFinancials ou null se não encontrado
     */
    override fun getProjectFinancials(projectId: String): Flow<ProjectFinancials?> = flow {
        try {
            val projectFinancialsDto = financialApiService.getProjectFinancials(projectId)
            emit(projectFinancialsDto.toDomainModel())
        } catch (e: Exception) {
            println("❌ Erro ao buscar dados financeiros do projeto $projectId: ${e.message}")
            emit(null)
        }
    }

    /**
     * Obtém os dados financeiros da empresa
     *
     * @param period Período para análise ("current_month", "last_quarter", "year_to_date")
     * @return Flow com CompanyFinancials
     */
    override fun getCompanyFinancials(period: String): Flow<CompanyFinancials> = flow {
        try {
            val companyFinancialsDto = financialApiService.getCompanyFinancials(period)
            emit(companyFinancialsDto.toDomainModel())
        } catch (e: Exception) {
            println("❌ Erro ao buscar dados financeiros da empresa: ${e.message}")
            // Retorna dados vazios em caso de erro
            emit(CompanyFinancials(
                period = period,
                revenue = 0.0,
                totalCosts = 0.0,
                netProfit = 0.0,
                netMargin = 0.0,
                activeProjects = 0,
                completedProjects = 0,
                averageRework = 0.0,
                overallEfficiency = 0.0,
                revenueLastSixMonths = 0.0,
                costsLastSixMonths = 0.0,
                profitLastSixMonths = 0.0,
                projectRankings = emptyList(),
                revenueBreakdown = RevenueBreakdown(0.0, 0.0, 0.0),
                expenseBreakdown = ExpenseBreakdown(0.0, 0.0, 0.0, 0.0),
                cashFlow = CashFlow(0.0, 0.0, 0.0),
                cashForecast = CashForecast(0.0, 0.0, 0.0, "stable"),
                complementaryIndicators = ComplementaryIndicators(0.0, 0.0, 0.0, 0.0, 0.0, 0.05, 0.0)
            ))
        }
    }

    /**
     * Atualiza os dados financeiros de uma tarefa
     *
     * @param taskId ID da tarefa
     * @param actualDays Dias reais gastos
     * @param actualCost Custo real incorrido
     * @return Result com sucesso ou erro
     */
    override suspend fun updateTaskFinancials(
        taskId: String,
        actualDays: Int,
        actualCost: Double
    ): Result<Unit> {
        return try {
            financialApiService.updateTaskFinancials(taskId, actualDays, actualCost)
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ Erro ao atualizar dados financeiros da tarefa $taskId: ${e.message}")
            Result.failure(e)
        }
    }

    // ========== Conversões DTO ↔ Domain Model ==========

    private fun FinancialTaskDto.toDomainModel(): FinancialTask {
        return FinancialTask(
            id = this.id,
            number = this.number,
            taskName = this.taskName,
            projectId = this.projectId,
            projectName = this.projectName,
            responsibleId = this.responsibleId,
            responsibleName = this.responsibleName,
            estimatedDays = this.estimatedDays,
            actualDays = this.actualDays,
            estimatedCost = this.estimatedCost,
            actualCost = this.actualCost,
            profitLoss = this.profitLoss,
            revisions = this.revisions,
            status = this.status
        )
    }

    private fun ProjectFinancialsDto.toDomainModel(): ProjectFinancials {
        return ProjectFinancials(
            projectId = this.projectId,
            projectName = this.projectName,
            client = this.client,
            technicalManager = this.technicalManager,
            startDate = this.startDate,
            status = this.status,
            estimatedDuration = this.estimatedDuration,
            actualDuration = this.actualDuration,
            durationDelta = this.durationDelta,
            physicalProgress = this.physicalProgress,
            disciplines = this.disciplines,
            totalTasks = this.totalTasks,
            completedTasks = this.completedTasks,
            pendingTasks = this.pendingTasks,
            financialProgress = this.financialProgress,
            averageEfficiency = this.averageEfficiency,
            reworkPercentage = this.reworkPercentage,
            contractValue = this.contractValue,
            estimatedCost = this.estimatedCost,
            actualCost = this.actualCost,
            estimatedProfit = this.estimatedProfit,
            projectedProfit = this.projectedProfit,
            estimatedMargin = this.estimatedMargin,
            actualMargin = this.actualMargin,
            internalLaborCost = this.internalLaborCost,
            reworkCost = this.reworkCost,
            outsourcedCost = this.outsourcedCost,
            travelTaxesCost = this.travelTaxesCost,
            disciplineDistribution = this.disciplineDistribution,
            totalRevisions = this.totalRevisions,
            revisionCost = this.revisionCost,
            scheduleImpactDays = this.scheduleImpactDays,
            revisionsByDiscipline = this.revisionsByDiscipline,
            revisionCauses = this.revisionCauses.map { it.toDomainModel() }
        )
    }

    private fun RevisionCauseDto.toDomainModel(): RevisionCause {
        return RevisionCause(
            cause = this.cause,
            count = this.count
        )
    }

    private fun CompanyFinancialsDto.toDomainModel(): CompanyFinancials {
        return CompanyFinancials(
            period = this.period,
            revenue = this.revenue,
            totalCosts = this.totalCosts,
            netProfit = this.netProfit,
            netMargin = this.netMargin,
            activeProjects = this.activeProjects,
            completedProjects = this.completedProjects,
            averageRework = this.averageRework,
            overallEfficiency = this.overallEfficiency,
            revenueLastSixMonths = this.revenueLastSixMonths,
            costsLastSixMonths = this.costsLastSixMonths,
            profitLastSixMonths = this.profitLastSixMonths,
            projectRankings = this.projectRankings.map { it.toDomainModel() },
            revenueBreakdown = this.revenueBreakdown.toDomainModel(),
            expenseBreakdown = this.expenseBreakdown.toDomainModel(),
            cashFlow = this.cashFlow.toDomainModel(),
            cashForecast = this.cashForecast.toDomainModel(),
            complementaryIndicators = this.complementaryIndicators.toDomainModel()
        )
    }

    private fun ProjectRankingDto.toDomainModel(): ProjectRanking {
        return ProjectRanking(
            position = this.position,
            projectId = this.projectId,
            projectName = this.projectName,
            profit = this.profit
        )
    }

    private fun RevenueBreakdownDto.toDomainModel(): RevenueBreakdown {
        return RevenueBreakdown(
            total = this.total,
            fromProjects = this.fromProjects,
            otherRevenue = this.otherRevenue
        )
    }

    private fun ExpenseBreakdownDto.toDomainModel(): ExpenseBreakdown {
        return ExpenseBreakdown(
            total = this.total,
            projectCosts = this.projectCosts,
            administrative = this.administrative,
            taxesFees = this.taxesFees
        )
    }

    private fun CashFlowDto.toDomainModel(): CashFlow {
        return CashFlow(
            accountsReceivable30Days = this.accountsReceivable30Days,
            accountsPayable30Days = this.accountsPayable30Days,
            projectedBalance = this.projectedBalance
        )
    }

    private fun CashForecastDto.toDomainModel(): CashForecast {
        return CashForecast(
            currentMonth = this.currentMonth,
            nextMonth = this.nextMonth,
            twoMonthsAhead = this.twoMonthsAhead,
            trend = this.trend
        )
    }

    private fun ComplementaryIndicatorsDto.toDomainModel(): ComplementaryIndicators {
        return ComplementaryIndicators(
            averageCostPerTask = this.averageCostPerTask,
            averageTicketPerProject = this.averageTicketPerProject,
            tasksOnTimePercentage = this.tasksOnTimePercentage,
            projectsOnTimePercentage = this.projectsOnTimePercentage,
            companyReworkPercentage = this.companyReworkPercentage,
            reworkTarget = this.reworkTarget,
            averageTaskExecutionDays = this.averageTaskExecutionDays
        )
    }
}
