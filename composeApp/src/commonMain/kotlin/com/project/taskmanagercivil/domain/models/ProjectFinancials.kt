package com.project.taskmanagercivil.domain.models

/**
 * Representa os dados financeiros de um projeto/obra
 */
data class ProjectFinancials(
    val projectId: String,
    val projectName: String,

    // Informações da Obra
    val client: String,
    val technicalManager: String,
    val startDate: String,
    val status: String,
    val estimatedDuration: Int, // dias
    val actualDuration: Int?, // dias
    val durationDelta: Int?, // diferença em dias

    // KPIs da Obra
    val physicalProgress: Double, // 0.0 a 1.0
    val disciplines: Int,
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val financialProgress: Double, // 0.0 a 1.0
    val averageEfficiency: Double,
    val reworkPercentage: Double,

    // Informações Financeiras
    val contractValue: Double,
    val estimatedCost: Double,
    val actualCost: Double,
    val estimatedProfit: Double,
    val projectedProfit: Double,
    val estimatedMargin: Double,
    val actualMargin: Double,

    // Custos por Categoria
    val internalLaborCost: Double,
    val reworkCost: Double,
    val outsourcedCost: Double,
    val travelTaxesCost: Double,

    // Distribuição por Disciplina
    val disciplineDistribution: Map<String, Int>, // disciplina -> número de tarefas

    // Revisões
    val totalRevisions: Int,
    val revisionCost: Double,
    val scheduleImpactDays: Int,
    val revisionsByDiscipline: Map<String, Int>,
    val revisionCauses: List<RevisionCause>
)

data class RevisionCause(
    val cause: String,
    val count: Int
)
