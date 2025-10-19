package com.project.taskmanagercivil.data.repository

import com.project.taskmanagercivil.data.MockData
import com.project.taskmanagercivil.domain.models.*
import com.project.taskmanagercivil.domain.repository.DashboardRepository
import com.project.taskmanagercivil.utils.ProjectStatusUtils
import kotlinx.coroutines.delay
import kotlinx.datetime.*

class DashboardRepositoryImpl : DashboardRepository {
    private val mockData = MockData()
    private val projects = mockData.projects
    private val tasks = mockData.tasks

    override suspend fun getDashboardData(): DashboardData {
        delay(150) // Simula latência de rede

        return DashboardData(
            projectStatusSummary = calculateProjectStatusSummary(),
            progressStats = calculateProgressStats(),
            criticalDeadlines = getCriticalDeadlines(),
            financialIndicators = calculateFinancialIndicators(),
            monthlyProjectData = calculateMonthlyProjectData()
        )
    }

    /**
     * Calcula o resumo quantitativo de obras por status baseado nas tarefas
     * Usa as regras de negócio para derivar status automaticamente das tarefas
     */
    private fun calculateProjectStatusSummary(): ProjectStatusSummary {
        // Usa a função utilitária para contar projetos por status derivado
        val statusCounts = ProjectStatusUtils.countProjectsByDerivedStatus(projects, tasks)

        return ProjectStatusSummary(
            todoCount = statusCounts[TaskStatus.TODO] ?: 0,
            inProgressCount = statusCounts[TaskStatus.IN_PROGRESS] ?: 0,
            inReviewCount = statusCounts[TaskStatus.IN_REVIEW] ?: 0,
            completedCount = statusCounts[TaskStatus.COMPLETED] ?: 0,
            blockedCount = statusCounts[TaskStatus.BLOCKED] ?: 0
        )
    }

    /**
     * Calcula estatísticas de progresso baseadas nas tarefas
     */
    private fun calculateProgressStats(): ProgressStats {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

        val totalTasks = tasks.size
        val completedTasks = tasks.count { it.status == TaskStatus.COMPLETED }
        val averageProgress = tasks.map { it.progress }.average().toFloat()
        val tasksOverdue = tasks.count { task ->
            task.status != TaskStatus.COMPLETED && task.dueDate < today
        }

        return ProgressStats(
            totalTasks = totalTasks,
            completedTasks = completedTasks,
            averageProgress = averageProgress,
            tasksOverdue = tasksOverdue
        )
    }

    /**
     * Retorna lista de prazos críticos (tarefas com prazo nos próximos 7 dias ou atrasadas)
     */
    private fun getCriticalDeadlines(): List<CriticalDeadline> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val criticalThreshold = today.plus(7, DateTimeUnit.DAY)

        return tasks
            .filter { it.status != TaskStatus.COMPLETED }
            .filter { it.dueDate <= criticalThreshold }
            .sortedBy { it.dueDate }
            .map { task ->
                val daysRemaining = (task.dueDate.toEpochDays() - today.toEpochDays()).toInt()

                CriticalDeadline(
                    projectId = task.project.id,
                    projectName = task.project.name,
                    taskId = task.id,
                    taskTitle = task.title,
                    dueDate = task.dueDate,
                    daysRemaining = daysRemaining,
                    priority = task.priority,
                    assignedTo = task.assignedTo
                )
            }
    }

    /**
     * Calcula indicadores financeiros por obra
     * Simula gastos baseados no progresso das tarefas do projeto
     */
    private fun calculateFinancialIndicators(): List<FinancialIndicator> {
        return projects.map { project ->
            val projectTasks = tasks.filter { it.project.id == project.id }

            // Calcula o progresso médio do projeto baseado nas tarefas
            val averageProgress = if (projectTasks.isNotEmpty()) {
                projectTasks.map { it.progress }.average().toFloat()
            } else {
                0f
            }

            // Simula o gasto como uma porcentagem do orçamento baseada no progresso
            // Adiciona uma variação aleatória simulada (entre -10% e +15% do esperado)
            val baseSpent = project.budget * averageProgress
            val variation = when (project.id) {
                "1" -> 1.05 // 5% acima
                "2" -> 0.95 // 5% abaixo
                "3" -> 1.12 // 12% acima
                "4" -> 0.92 // 8% abaixo
                "5" -> 1.08 // 8% acima
                "6" -> 0.98 // 2% abaixo
                else -> 1.0
            }
            val spent = baseSpent * variation

            FinancialIndicator(
                projectId = project.id,
                projectName = project.name,
                budgeted = project.budget,
                spent = spent
            )
        }
    }

    /**
     * Calcula dados mensais de projetos para o gráfico de colunas
     * Distribui os projetos por mês baseado nas datas de início e fim
     */
    private fun calculateMonthlyProjectData(): List<MonthlyProjectData> {
        val monthlyData = mutableMapOf<String, MonthlyProjectData>()

        // Gera dados para os últimos 6 meses
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val months = listOf(
            Pair(2024, 1) to "Jan/2024",
            Pair(2024, 2) to "Fev/2024",
            Pair(2024, 3) to "Mar/2024",
            Pair(2024, 4) to "Abr/2024",
            Pair(2024, 5) to "Mai/2024",
            Pair(2024, 6) to "Jun/2024"
        )

        months.forEach { (yearMonth, monthLabel) ->
            val (year, month) = yearMonth
            monthlyData[monthLabel] = MonthlyProjectData(
                month = monthLabel,
                year = year,
                monthNumber = month
            )
        }

        // Para cada projeto, determina seu status em cada mês
        projects.forEach { project ->
            val projectTasks = tasks.filter { it.project.id == project.id }

            months.forEach { (yearMonth, monthLabel) ->
                val (year, month) = yearMonth

                // Verifica se o projeto estava ativo neste mês
                val monthStart = kotlinx.datetime.LocalDate(year, month, 1)
                val monthEnd = if (month == 12) {
                    kotlinx.datetime.LocalDate(year + 1, 1, 1).minus(1, DateTimeUnit.DAY)
                } else {
                    kotlinx.datetime.LocalDate(year, month + 1, 1).minus(1, DateTimeUnit.DAY)
                }

                // Projeto está ativo se iniciou antes do fim do mês e termina depois do início do mês
                if (project.startDate <= monthEnd && project.endDate >= monthStart) {
                    val currentData = monthlyData[monthLabel]!!

                    // Determina o status do projeto neste mês
                    val tasksInMonth = projectTasks.filter { task ->
                        task.startDate <= monthEnd && task.dueDate >= monthStart
                    }

                    val status = determineProjectStatus(tasksInMonth, monthEnd)

                    monthlyData[monthLabel] = when (status) {
                        TaskStatus.TODO -> currentData.copy(todoCount = currentData.todoCount + 1)
                        TaskStatus.IN_PROGRESS -> currentData.copy(inProgressCount = currentData.inProgressCount + 1)
                        TaskStatus.IN_REVIEW -> currentData.copy(inReviewCount = currentData.inReviewCount + 1)
                        TaskStatus.COMPLETED -> currentData.copy(completedCount = currentData.completedCount + 1)
                        TaskStatus.BLOCKED -> currentData.copy(blockedCount = currentData.blockedCount + 1)
                    }
                }
            }
        }

        return monthlyData.values.sortedBy { it.year * 100 + it.monthNumber }
    }

    /**
     * Determina o status de um projeto baseado em suas tarefas
     */
    private fun determineProjectStatus(tasks: List<Task>, referenceDate: kotlinx.datetime.LocalDate): TaskStatus {
        if (tasks.isEmpty()) return TaskStatus.TODO

        // Se há alguma tarefa bloqueada, o projeto é bloqueado
        if (tasks.any { it.status == TaskStatus.BLOCKED }) {
            return TaskStatus.BLOCKED
        }

        // Se todas as tarefas estão concluídas, o projeto está concluído
        if (tasks.all { it.status == TaskStatus.COMPLETED }) {
            return TaskStatus.COMPLETED
        }

        // Se há tarefas em revisão, o projeto está em revisão
        if (tasks.any { it.status == TaskStatus.IN_REVIEW }) {
            return TaskStatus.IN_REVIEW
        }

        // Se há tarefas em andamento, o projeto está em andamento
        if (tasks.any { it.status == TaskStatus.IN_PROGRESS }) {
            return TaskStatus.IN_PROGRESS
        }

        // Caso contrário, está a fazer
        return TaskStatus.TODO
    }
}
