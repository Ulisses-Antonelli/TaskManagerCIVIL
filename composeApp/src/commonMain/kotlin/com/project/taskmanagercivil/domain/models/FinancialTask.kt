package com.project.taskmanagercivil.domain.models

/**
 * Representa uma tarefa no contexto financeiro
 */
data class FinancialTask(
    val id: String,
    val number: Int,
    val taskName: String,
    val projectId: String,
    val projectName: String,
    val responsibleId: String,
    val responsibleName: String,
    val estimatedDays: Int,
    val actualDays: Int?,
    val estimatedCost: Double,
    val actualCost: Double?,
    val profitLoss: Double?,
    val revisions: Int,
    val status: String
)
