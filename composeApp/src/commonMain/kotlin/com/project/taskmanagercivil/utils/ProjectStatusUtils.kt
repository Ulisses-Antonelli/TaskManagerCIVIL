package com.project.taskmanagercivil.utils

import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.domain.models.TaskStatus

/**
 * Utilitários para cálculo de status e progresso de projetos/obras
 */
object ProjectStatusUtils {

    /**
     * Calcula o status derivado de uma obra baseado nos status de suas tarefas
     *
     * Ordem de prioridade:
     * 1. BLOQUEADA - se existe pelo menos 1 tarefa bloqueada
     * 2. EM REVISÃO - se nenhuma bloqueada e existe pelo menos 1 em revisão
     * 3. EM ANDAMENTO - se nenhuma bloqueada/revisão e existe ao menos 1 em andamento ou concluída (mas nem todas concluídas)
     * 4. CONCLUÍDA - se todas as tarefas estão concluídas
     * 5. A FAZER - se nenhuma tarefa cadastrada ou todas em "A Fazer"
     */
    fun calculateProjectStatus(tasks: List<Task>, projectId: String): TaskStatus {
        val projectTasks = tasks.filter { it.project.id == projectId }

        // Nenhuma tarefa cadastrada ou lista vazia
        if (projectTasks.isEmpty()) {
            return TaskStatus.TODO
        }

        // 1. Verifica se tem alguma tarefa bloqueada
        if (projectTasks.any { it.status == TaskStatus.BLOCKED }) {
            return TaskStatus.BLOCKED
        }

        // 2. Verifica se tem alguma em revisão
        if (projectTasks.any { it.status == TaskStatus.IN_REVIEW }) {
            return TaskStatus.IN_REVIEW
        }

        // 4. Verifica se todas estão concluídas
        if (projectTasks.all { it.status == TaskStatus.COMPLETED }) {
            return TaskStatus.COMPLETED
        }

        // 3. Verifica se tem pelo menos uma em andamento ou concluída
        if (projectTasks.any { it.status == TaskStatus.IN_PROGRESS || it.status == TaskStatus.COMPLETED }) {
            return TaskStatus.IN_PROGRESS
        }

        // 5. Todas estão em "A Fazer"
        return TaskStatus.TODO
    }

    /**
     * Calcula o progresso médio ponderado de uma obra
     * baseado no progresso de todas as suas tarefas
     *
     * @return Percentual de 0 a 100
     */
    fun calculateProjectProgress(tasks: List<Task>, projectId: String): Float {
        val projectTasks = tasks.filter { it.project.id == projectId }

        if (projectTasks.isEmpty()) {
            return 0f
        }

        // Média ponderada simples (todas as tarefas têm peso igual)
        val totalProgress = projectTasks.sumOf { it.progress.toDouble() }
        return (totalProgress / projectTasks.size).toFloat()
    }

    /**
     * Retorna todas as obras que possuem pelo menos uma tarefa com o status especificado
     */
    fun filterProjectsByTaskStatus(
        projects: List<Project>,
        tasks: List<Task>,
        taskStatus: TaskStatus
    ): List<Project> {
        val projectIdsWithStatus = tasks
            .filter { it.status == taskStatus }
            .map { it.project.id }
            .toSet()

        return projects.filter { it.id in projectIdsWithStatus }
    }

    /**
     * Retorna todas as obras que têm o status derivado especificado
     */
    fun filterProjectsByDerivedStatus(
        projects: List<Project>,
        tasks: List<Task>,
        derivedStatus: TaskStatus
    ): List<Project> {
        return projects.filter { project ->
            calculateProjectStatus(tasks, project.id) == derivedStatus
        }
    }

    /**
     * Conta quantas obras têm cada status derivado
     */
    fun countProjectsByDerivedStatus(
        projects: List<Project>,
        tasks: List<Task>
    ): Map<TaskStatus, Int> {
        val counts = mutableMapOf<TaskStatus, Int>()

        TaskStatus.entries.forEach { status ->
            counts[status] = 0
        }

        projects.forEach { project ->
            val derivedStatus = calculateProjectStatus(tasks, project.id)
            counts[derivedStatus] = (counts[derivedStatus] ?: 0) + 1
        }

        return counts
    }

    /**
     * Conta quantas obras possuem pelo menos uma tarefa com cada status
     * Este é o critério usado no Dashboard para os cards de resumo
     *
     * Exemplo: Se uma obra tem tarefas com status [TODO, IN_PROGRESS, BLOCKED],
     * ela será contada nos 3 contadores (todoCount, inProgressCount, blockedCount)
     */
    fun countProjectsByTaskStatus(
        projects: List<Project>,
        tasks: List<Task>
    ): Map<TaskStatus, Int> {
        val counts = mutableMapOf<TaskStatus, Int>()

        TaskStatus.entries.forEach { status ->
            counts[status] = 0
        }

        projects.forEach { project ->
            val projectTasks = tasks.filter { it.project.id == project.id }

            // Para cada status, verifica se o projeto tem pelo menos uma tarefa com esse status
            TaskStatus.entries.forEach { status ->
                if (projectTasks.any { it.status == status }) {
                    counts[status] = (counts[status] ?: 0) + 1
                }
            }
        }

        return counts
    }
}

/**
 * Extensões para facilitar o uso
 */

/**
 * Calcula o status derivado desta obra baseado nas tarefas fornecidas
 */
fun Project.calculateDerivedStatus(tasks: List<Task>): TaskStatus {
    return ProjectStatusUtils.calculateProjectStatus(tasks, this.id)
}

/**
 * Calcula o progresso médio desta obra baseado nas tarefas fornecidas
 */
fun Project.calculateProgress(tasks: List<Task>): Float {
    return ProjectStatusUtils.calculateProjectProgress(tasks, this.id)
}

/**
 * Retorna as tarefas pertencentes a este projeto
 */
fun Project.getTasks(allTasks: List<Task>): List<Task> {
    return allTasks.filter { it.project.id == this.id }
}
