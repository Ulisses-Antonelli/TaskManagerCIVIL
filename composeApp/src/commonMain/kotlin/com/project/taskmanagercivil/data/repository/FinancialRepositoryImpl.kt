package com.project.taskmanagercivil.data.repository

import com.project.taskmanagercivil.domain.models.*
import com.project.taskmanagercivil.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Implementação do FinancialRepository usando dados mockados
 * TODO: Substituir por implementação real com chamadas HTTP quando backend estiver pronto
 */
class FinancialRepositoryImpl : FinancialRepository {

    override fun getFinancialTasks(
        projectId: String?,
        disciplineId: String?,
        responsibleId: String?,
        status: String?,
        period: String?
    ): Flow<List<FinancialTask>> {
        var tasks = mockFinancialTasks

        // Aplicar filtros
        if (projectId != null) {
            tasks = tasks.filter { it.projectId == projectId }
        }
        if (status != null && status != "Todos") {
            tasks = tasks.filter { it.status == status }
        }
        // TODO: Implementar filtros por disciplina, responsável e período

        return flowOf(tasks)
    }

    override fun getProjectFinancials(projectId: String): Flow<ProjectFinancials?> {
        return flowOf(mockProjectFinancials)
    }

    override fun getCompanyFinancials(period: String): Flow<CompanyFinancials> {
        return flowOf(mockCompanyFinancials)
    }

    override suspend fun updateTaskFinancials(
        taskId: String,
        actualDays: Int,
        actualCost: Double
    ): Result<Unit> {
        // TODO: Implementar quando backend estiver pronto
        return Result.success(Unit)
    }

    // ========== DADOS MOCKADOS ==========

    private val mockFinancialTasks = listOf(
        FinancialTask(
            id = "ft1",
            number = 1,
            taskName = "Projeto Estrutural",
            projectId = "p1",
            projectName = "Ed. Alpha",
            responsibleId = "e1",
            responsibleName = "Marcos",
            estimatedDays = 5,
            actualDays = 4,
            estimatedCost = 1800.00,
            actualCost = 1440.00,
            profitLoss = 360.00,
            revisions = 0,
            status = "Concluída"
        ),
        FinancialTask(
            id = "ft2",
            number = 2,
            taskName = "Elétrica - 1º Pavto",
            projectId = "p1",
            projectName = "Ed. Alpha",
            responsibleId = "e2",
            responsibleName = "Carla",
            estimatedDays = 3,
            actualDays = 5,
            estimatedCost = 1100.00,
            actualCost = 1780.00,
            profitLoss = -680.00,
            revisions = 2,
            status = "Concluída"
        ),
        FinancialTask(
            id = "ft3",
            number = 3,
            taskName = "Hidráulica - 2º Pavto",
            projectId = "p2",
            projectName = "Ed. Beta",
            responsibleId = "e3",
            responsibleName = "João",
            estimatedDays = 4,
            actualDays = 4,
            estimatedCost = 1250.00,
            actualCost = 1250.00,
            profitLoss = 0.00,
            revisions = 1,
            status = "Em revisão"
        ),
        FinancialTask(
            id = "ft4",
            number = 4,
            taskName = "Arquitetônico - Layout",
            projectId = "p2",
            projectName = "Ed. Beta",
            responsibleId = "e4",
            responsibleName = "Ana",
            estimatedDays = 6,
            actualDays = null,
            estimatedCost = 2900.00,
            actualCost = null,
            profitLoss = null,
            revisions = 0,
            status = "Em curso"
        ),
        FinancialTask(
            id = "ft5",
            number = 5,
            taskName = "PPCI",
            projectId = "p3",
            projectName = "Ed. Gamma",
            responsibleId = "e5",
            responsibleName = "Pedro",
            estimatedDays = 2,
            actualDays = 1,
            estimatedCost = 900.00,
            actualCost = 450.00,
            profitLoss = 450.00,
            revisions = 0,
            status = "Concluída"
        ),
        FinancialTask(
            id = "ft6",
            number = 6,
            taskName = "Sanitário - Revisão",
            projectId = "p1",
            projectName = "Ed. Alpha",
            responsibleId = "e2",
            responsibleName = "Carla",
            estimatedDays = 1,
            actualDays = 2,
            estimatedCost = 350.00,
            actualCost = 700.00,
            profitLoss = -350.00,
            revisions = 1,
            status = "Concluída"
        )
    )

    private val mockProjectFinancials = ProjectFinancials(
        projectId = "p1",
        projectName = "Edifício Alpha",
        client = "Construtora XYZ",
        technicalManager = "Eng. Marcos",
        startDate = "05/08/2025",
        status = "Em Execução",
        estimatedDuration = 90,
        actualDuration = 102,
        durationDelta = -12,
        physicalProgress = 0.68,
        disciplines = 4,
        totalTasks = 32,
        completedTasks = 22,
        pendingTasks = 10,
        financialProgress = 0.64,
        averageEfficiency = 1.12,
        reworkPercentage = 0.09,
        contractValue = 95000.00,
        estimatedCost = 62500.00,
        actualCost = 64800.00,
        estimatedProfit = 32500.00,
        projectedProfit = 30200.00,
        estimatedMargin = 0.34,
        actualMargin = 0.318,
        internalLaborCost = 49300.00,
        reworkCost = 5900.00,
        outsourcedCost = 7800.00,
        travelTaxesCost = 1800.00,
        disciplineDistribution = mapOf(
            "Arquitetura" to 10,
            "Estrutural" to 8,
            "Elétrica" to 7,
            "Hidráulica" to 7
        ),
        totalRevisions = 7,
        revisionCost = 5900.00,
        scheduleImpactDays = 14,
        revisionsByDiscipline = mapOf(
            "Arquitetura" to 2,
            "Elétrica" to 3,
            "Hidráulica" to 2,
            "Estrutural" to 0
        ),
        revisionCauses = listOf(
            RevisionCause("Mudança solicitada pelo cliente", 4),
            RevisionCause("Erro de compatibilização", 2),
            RevisionCause("Falha de comunicação", 1)
        )
    )

    private val mockCompanyFinancials = CompanyFinancials(
        period = "Mês Atual",
        revenue = 142000.00,
        totalCosts = 98400.00,
        netProfit = 43600.00,
        netMargin = 0.307,
        activeProjects = 6,
        completedProjects = 2,
        averageRework = 0.08,
        overallEfficiency = 1.14,
        revenueLastSixMonths = 840000.00,
        costsLastSixMonths = 546000.00,
        profitLastSixMonths = 294000.00,
        projectRankings = listOf(
            ProjectRanking(1, "p1", "Edifício Alpha", 30200.00),
            ProjectRanking(2, "p2", "Resid. Porto Azul", 18900.00),
            ProjectRanking(3, "p3", "Clínica Sorriso Feliz", 7400.00),
            ProjectRanking(4, "p4", "Condomínio Serra Nova", -3200.00),
            ProjectRanking(5, "p5", "Reforma Casa Garcia", -9800.00)
        ),
        revenueBreakdown = RevenueBreakdown(
            total = 142000.00,
            fromProjects = 138000.00,
            otherRevenue = 4000.00
        ),
        expenseBreakdown = ExpenseBreakdown(
            total = 98400.00,
            projectCosts = 78500.00,
            administrative = 12700.00,
            taxesFees = 7200.00
        ),
        cashFlow = CashFlow(
            accountsReceivable30Days = 52300.00,
            accountsPayable30Days = 27900.00,
            projectedBalance = 24400.00
        ),
        cashForecast = CashForecast(
            currentMonth = 0.55,
            nextMonth = 0.65,
            twoMonthsAhead = 0.51,
            trend = "growing"
        ),
        complementaryIndicators = ComplementaryIndicators(
            averageCostPerTask = 1780.00,
            averageTicketPerProject = 47300.00,
            tasksOnTimePercentage = 0.74,
            projectsOnTimePercentage = 0.61,
            companyReworkPercentage = 0.08,
            reworkTarget = 0.05,
            averageTaskExecutionDays = 3.8
        )
    )
}
