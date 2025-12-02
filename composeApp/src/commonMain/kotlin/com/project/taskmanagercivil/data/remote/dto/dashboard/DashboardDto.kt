package com.project.taskmanagercivil.data.remote.dto.dashboard

import kotlinx.serialization.Serializable

/**
 * DTO para Dashboard - compatível com Spring Boot backend
 *
 * Endpoint do backend:
 * - GET /api/dashboard - Retorna dados agregados do dashboard
 *
 * ⚠️ IMPORTANTE: Este endpoint deve fazer TODOS os cálculos no backend:
 * - Contagens de projetos por status
 * - Estatísticas de progresso
 * - Prazos críticos (próximos 7 dias ou atrasados)
 * - Indicadores financeiros por projeto
 * - Dados mensais para gráficos (últimos 6 meses)
 *
 * O backend retorna tudo calculado para otimizar performance.
 * O frontend apenas exibe os dados recebidos.
 *
 * Permissões por Role:
 * - ADMIN: Vê dados de todos os projetos
 * - GESTOR_OBRAS: Vê dados de projetos que gerencia
 * - LIDER_EQUIPE: Vê dados de projetos/tarefas da sua equipe
 * - FUNCIONARIO: Vê dados resumidos (sem detalhes financeiros)
 */
@Serializable
data class DashboardDto(
    val projectStatusSummary: ProjectStatusSummaryDto,
    val progressStats: ProgressStatsDto,
    val criticalDeadlines: List<CriticalDeadlineDto>,
    val financialIndicators: List<FinancialIndicatorDto>,
    val monthlyProjectData: List<MonthlyProjectDataDto>
)

/**
 * DTO para resumo quantitativo de projetos por status
 *
 * Conta quantos projetos possuem pelo menos UMA tarefa com cada status.
 * Um projeto pode ser contado múltiplas vezes se tiver tarefas com diferentes status.
 */
@Serializable
data class ProjectStatusSummaryDto(
    val todoCount: Int = 0,
    val inProgressCount: Int = 0,
    val inReviewCount: Int = 0,
    val completedCount: Int = 0,
    val blockedCount: Int = 0,
    val total: Int = 0
)

/**
 * DTO para estatísticas de progresso geral
 */
@Serializable
data class ProgressStatsDto(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val averageProgress: Float = 0f,
    val tasksOverdue: Int = 0,
    val progressPercentage: Float = 0f
)

/**
 * DTO para alerta de prazo crítico
 *
 * Tarefas não concluídas com prazo nos próximos 7 dias ou já atrasadas
 */
@Serializable
data class CriticalDeadlineDto(
    val projectId: String,
    val projectName: String,
    val taskId: String,
    val taskTitle: String,
    val dueDate: String,  // ISO 8601: "2024-12-31"
    val daysRemaining: Int,  // Negativo se atrasado
    val priority: String,  // TaskPriority enum value
    val assignedToId: String,
    val assignedToName: String
)

/**
 * DTO para indicadores financeiros de um projeto
 *
 * ⚠️ ATENÇÃO: Dados financeiros são sensíveis
 * Backend deve filtrar por permissão:
 * - ADMIN/GESTOR_OBRAS: Vê todos os indicadores
 * - LIDER_EQUIPE: Vê apenas projetos da sua equipe
 * - FUNCIONARIO: Não deve receber dados financeiros
 */
@Serializable
data class FinancialIndicatorDto(
    val projectId: String,
    val projectName: String,
    val budgeted: Double,
    val spent: Double,
    val spentPercentage: Float,
    val remaining: Double,
    val isOverBudget: Boolean
)

/**
 * DTO para dados mensais de projetos (para gráfico de colunas)
 *
 * Representa a distribuição de projetos por status em cada mês
 */
@Serializable
data class MonthlyProjectDataDto(
    val month: String,  // "Jan/2024"
    val year: Int,
    val monthNumber: Int,  // 1-12
    val todoCount: Int = 0,
    val inProgressCount: Int = 0,
    val inReviewCount: Int = 0,
    val completedCount: Int = 0,
    val blockedCount: Int = 0,
    val total: Int = 0
)
