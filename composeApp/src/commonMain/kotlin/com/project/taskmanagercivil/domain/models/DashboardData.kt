package com.project.taskmanagercivil.domain.models

import kotlinx.datetime.LocalDate

/**
 * Resumo quantitativo de obras por status
 */
data class ProjectStatusSummary(
    val todoCount: Int = 0,
    val inProgressCount: Int = 0,
    val inReviewCount: Int = 0,
    val completedCount: Int = 0,
    val blockedCount: Int = 0
) {
    val total: Int = todoCount + inProgressCount + inReviewCount + completedCount + blockedCount
}

/**
 * Estatísticas de progresso geral
 */
data class ProgressStats(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val averageProgress: Float = 0f,
    val tasksOverdue: Int = 0
) {
    val progressPercentage: Float = if (totalTasks > 0) {
        (completedTasks.toFloat() / totalTasks.toFloat()) * 100f
    } else 0f
}

/**
 * Alerta de prazo crítico
 */
data class CriticalDeadline(
    val projectId: String,
    val projectName: String,
    val taskId: String,
    val taskTitle: String,
    val dueDate: LocalDate,
    val daysRemaining: Int,
    val priority: TaskPriority,
    val assignedTo: User
)

/**
 * Indicadores financeiros de uma obra
 */
data class FinancialIndicator(
    val projectId: String,
    val projectName: String,
    val budgeted: Double,
    val spent: Double,
    val spentPercentage: Float = if (budgeted > 0) (spent.toFloat() / budgeted.toFloat()) * 100f else 0f,
    val remaining: Double = budgeted - spent,
    val isOverBudget: Boolean = spent > budgeted
)

/**
 * Dados de projetos por mês (para gráfico de colunas)
 */
data class MonthlyProjectData(
    val month: String, // "Jan/2024"
    val year: Int,
    val monthNumber: Int,
    val todoCount: Int = 0,
    val inProgressCount: Int = 0,
    val inReviewCount: Int = 0,
    val completedCount: Int = 0,
    val blockedCount: Int = 0
) {
    val total: Int = todoCount + inProgressCount + inReviewCount + completedCount + blockedCount
}

/**
 * Dados completos do Dashboard
 */
data class DashboardData(
    val projectStatusSummary: ProjectStatusSummary = ProjectStatusSummary(),
    val progressStats: ProgressStats = ProgressStats(),
    val criticalDeadlines: List<CriticalDeadline> = emptyList(),
    val financialIndicators: List<FinancialIndicator> = emptyList(),
    val monthlyProjectData: List<MonthlyProjectData> = emptyList()
)
