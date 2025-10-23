package com.project.taskmanagercivil.domain.models

import kotlinx.datetime.LocalDate

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val priority: TaskPriority,
    val assignedTo: User,
    val project: Project,
    val startDate: LocalDate,
    val dueDate: LocalDate,
    val progress: Float,
    val tags: List<String> = emptyList(),
    val dependencies: List<String> = emptyList(),
    val revisions: List<TaskRevision> = emptyList()
)

data class TaskRevision(
    val revisionNumber: Int,
    val author: String,
    val description: String,
    val startDate: LocalDate,
    val deliveryDate: LocalDate
)

enum class TaskStatus(val label: String) {
    TODO("A FAZER"),
    IN_PROGRESS("EM ANDAMENTO"),
    IN_REVIEW("EM REVISÃO"),
    COMPLETED("CONCLUIDA"),
    BLOCKED("BLOQUEADA")
}

enum class TaskPriority(val label: String) {
    LOW("BAIXA"),
    MEDIUM("MÉDIA"),
    HIGH("ALTA"),
    CRITICAL("CRITICA")
}