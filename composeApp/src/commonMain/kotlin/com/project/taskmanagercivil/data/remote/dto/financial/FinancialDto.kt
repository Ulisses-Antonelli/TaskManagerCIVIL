package com.project.taskmanagercivil.data.remote.dto.financial

import kotlinx.serialization.Serializable

/**
 * DTOs para Painel Financeiro - compatível com Spring Boot backend
 *
 * Endpoints do backend:
 * - GET /api/financial/tasks - Lista tarefas financeiras (com filtros)
 * - GET /api/financial/project/{projectId} - Dados financeiros de um projeto
 * - GET /api/financial/company - Dados financeiros da empresa
 * - PUT /api/financial/tasks/{taskId} - Atualiza dados financeiros de uma tarefa
 *
 * ⚠️ IMPORTANTE: Dados financeiros são ALTAMENTE sensíveis
 * Backend DEVE filtrar rigorosamente por permissões:
 * - ADMIN: Acesso total a todos os dados financeiros
 * - GESTOR_OBRAS: Vê dados de projetos que gerencia
 * - LIDER_EQUIPE: Vê dados resumidos (sem detalhes de custos/lucros)
 * - FUNCIONARIO: NÃO deve ter acesso (retornar 403 Forbidden)
 */

/**
 * DTO para Tarefa Financeira
 */
@Serializable
data class FinancialTaskDto(
    val id: String,
    val number: Int,
    val taskName: String,
    val projectId: String,
    val projectName: String,
    val responsibleId: String,
    val responsibleName: String,
    val estimatedDays: Int,
    val actualDays: Int? = null,
    val estimatedCost: Double,
    val actualCost: Double? = null,
    val profitLoss: Double? = null,  // Calculado: actualCost - estimatedCost
    val revisions: Int = 0,
    val status: String  // "Em curso", "Concluída", "Em revisão"
)

/**
 * DTO para Dados Financeiros de um Projeto
 */
@Serializable
data class ProjectFinancialsDto(
    val projectId: String,
    val projectName: String,
    val client: String,
    val technicalManager: String,
    val startDate: String,
    val status: String,
    val estimatedDuration: Int,
    val actualDuration: Int? = null,
    val durationDelta: Int? = null,
    val physicalProgress: Double,
    val disciplines: Int,
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val financialProgress: Double,
    val averageEfficiency: Double,
    val reworkPercentage: Double,
    val contractValue: Double,
    val estimatedCost: Double,
    val actualCost: Double,
    val estimatedProfit: Double,
    val projectedProfit: Double,
    val estimatedMargin: Double,
    val actualMargin: Double,
    val internalLaborCost: Double,
    val reworkCost: Double,
    val outsourcedCost: Double,
    val travelTaxesCost: Double,
    val disciplineDistribution: Map<String, Int>,
    val totalRevisions: Int,
    val revisionCost: Double,
    val scheduleImpactDays: Int,
    val revisionsByDiscipline: Map<String, Int>,
    val revisionCauses: List<RevisionCauseDto>
)

@Serializable
data class RevisionCauseDto(
    val cause: String,
    val count: Int
)

/**
 * DTO para Dados Financeiros da Empresa
 */
@Serializable
data class CompanyFinancialsDto(
    val period: String,
    val revenue: Double,
    val totalCosts: Double,
    val netProfit: Double,
    val netMargin: Double,
    val activeProjects: Int,
    val completedProjects: Int,
    val averageRework: Double,
    val overallEfficiency: Double,
    val revenueLastSixMonths: Double,
    val costsLastSixMonths: Double,
    val profitLastSixMonths: Double,
    val projectRankings: List<ProjectRankingDto>,
    val revenueBreakdown: RevenueBreakdownDto,
    val expenseBreakdown: ExpenseBreakdownDto,
    val cashFlow: CashFlowDto,
    val cashForecast: CashForecastDto,
    val complementaryIndicators: ComplementaryIndicatorsDto
)

@Serializable
data class ProjectRankingDto(
    val position: Int,
    val projectId: String,
    val projectName: String,
    val profit: Double
)

@Serializable
data class RevenueBreakdownDto(
    val total: Double,
    val fromProjects: Double,
    val otherRevenue: Double
)

@Serializable
data class ExpenseBreakdownDto(
    val total: Double,
    val projectCosts: Double,
    val administrative: Double,
    val taxesFees: Double
)

@Serializable
data class CashFlowDto(
    val accountsReceivable30Days: Double,
    val accountsPayable30Days: Double,
    val projectedBalance: Double
)

@Serializable
data class CashForecastDto(
    val currentMonth: Double,
    val nextMonth: Double,
    val twoMonthsAhead: Double,
    val trend: String  // "growing", "stable", "declining"
)

@Serializable
data class ComplementaryIndicatorsDto(
    val averageCostPerTask: Double,
    val averageTicketPerProject: Double,
    val tasksOnTimePercentage: Double,
    val projectsOnTimePercentage: Double,
    val companyReworkPercentage: Double,
    val reworkTarget: Double,
    val averageTaskExecutionDays: Double
)

/**
 * DTO para atualizar dados financeiros de uma tarefa
 */
@Serializable
data class UpdateFinancialTaskDto(
    val actualDays: Int,
    val actualCost: Double
)
