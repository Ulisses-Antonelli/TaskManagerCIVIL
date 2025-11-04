package com.project.taskmanagercivil.domain.models

/**
 * Representa os dados financeiros da empresa
 */
data class CompanyFinancials(
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
    val projectRankings: List<ProjectRanking>,
    val revenueBreakdown: RevenueBreakdown,
    val expenseBreakdown: ExpenseBreakdown,
    val cashFlow: CashFlow,
    val cashForecast: CashForecast,
    val complementaryIndicators: ComplementaryIndicators
)

data class ProjectRanking(
    val position: Int,
    val projectId: String,
    val projectName: String,
    val profit: Double
)

data class RevenueBreakdown(
    val total: Double,
    val fromProjects: Double,
    val otherRevenue: Double
)

data class ExpenseBreakdown(
    val total: Double,
    val projectCosts: Double,
    val administrative: Double,
    val taxesFees: Double
)

data class CashFlow(
    val accountsReceivable30Days: Double,
    val accountsPayable30Days: Double,
    val projectedBalance: Double
)

data class CashForecast(
    val currentMonth: Double,
    val nextMonth: Double,
    val twoMonthsAhead: Double,
    val trend: String // "growing", "stable", "declining"
)

data class ComplementaryIndicators(
    val averageCostPerTask: Double,
    val averageTicketPerProject: Double,
    val tasksOnTimePercentage: Double,
    val projectsOnTimePercentage: Double,
    val companyReworkPercentage: Double,
    val reworkTarget: Double,
    val averageTaskExecutionDays: Double
)
