package com.project.taskmanagercivil.data.mock

import com.project.taskmanagercivil.presentation.components.FinancialTaskRow

object FinancialMockData {

    // Dados mockados para o Painel de Tarefas
    val tasksPanelTasks = listOf(
        FinancialTaskRow(
            number = 1,
            taskName = "Projeto Estrutural",
            projectName = "Ed. Alpha",
            responsibleName = "Marcos",
            estimatedDays = 5,
            actualDays = 4,
            estimatedCost = 1800.00,
            actualCost = 1440.00,
            profitLoss = 360.00,
            revisions = 0,
            status = "Concluída"
        ),
        FinancialTaskRow(
            number = 2,
            taskName = "Elétrica - 1º Pavto",
            projectName = "Ed. Alpha",
            responsibleName = "Carla",
            estimatedDays = 3,
            actualDays = 5,
            estimatedCost = 1100.00,
            actualCost = 1780.00,
            profitLoss = -680.00,
            revisions = 2,
            status = "Concluída"
        ),
        FinancialTaskRow(
            number = 3,
            taskName = "Hidráulica - 2º Pavto",
            projectName = "Ed. Beta",
            responsibleName = "João",
            estimatedDays = 4,
            actualDays = 4,
            estimatedCost = 1250.00,
            actualCost = 1250.00,
            profitLoss = 0.00,
            revisions = 1,
            status = "Em revisão"
        ),
        FinancialTaskRow(
            number = 4,
            taskName = "Arquitetônico - Layout",
            projectName = "Ed. Beta",
            responsibleName = "Ana",
            estimatedDays = 6,
            actualDays = null,
            estimatedCost = 2900.00,
            actualCost = null,
            profitLoss = null,
            revisions = 0,
            status = "Em curso"
        ),
        FinancialTaskRow(
            number = 5,
            taskName = "PPCI",
            projectName = "Ed. Gamma",
            responsibleName = "Pedro",
            estimatedDays = 2,
            actualDays = 1,
            estimatedCost = 900.00,
            actualCost = 450.00,
            profitLoss = 450.00,
            revisions = 0,
            status = "Concluída"
        ),
        FinancialTaskRow(
            number = 6,
            taskName = "Sanitário - Revisão",
            projectName = "Ed. Alpha",
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

    // Dados mockados para a aba Tarefas do Painel de Obras/Projetos
    val projectTasksTabTasks = listOf(
        FinancialTaskRow(
            number = 1,
            taskName = "Projeto Estrutural",
            projectName = "Estrutural",
            responsibleName = "Marcos",
            estimatedDays = 5,
            actualDays = 4,
            estimatedCost = 1800.00,
            actualCost = 1440.00,
            profitLoss = 360.00,
            revisions = 0,
            status = "Concluída"
        ),
        FinancialTaskRow(
            number = 2,
            taskName = "Elétrica - 1º Pavto",
            projectName = "Elétrica",
            responsibleName = "Carla",
            estimatedDays = 3,
            actualDays = 5,
            estimatedCost = 1100.00,
            actualCost = 1780.00,
            profitLoss = -680.00,
            revisions = 2,
            status = "Concluída"
        ),
        FinancialTaskRow(
            number = 3,
            taskName = "PPCI",
            projectName = "Arquitetura",
            responsibleName = "Pedro",
            estimatedDays = 2,
            actualDays = 1,
            estimatedCost = 900.00,
            actualCost = 450.00,
            profitLoss = 450.00,
            revisions = 0,
            status = "Concluída"
        ),
        FinancialTaskRow(
            number = 4,
            taskName = "Sanitário - Revisão",
            projectName = "Hidráulica",
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
}
