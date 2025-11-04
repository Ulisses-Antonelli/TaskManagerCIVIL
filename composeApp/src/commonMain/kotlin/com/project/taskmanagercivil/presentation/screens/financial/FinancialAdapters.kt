package com.project.taskmanagercivil.presentation.screens.financial

import com.project.taskmanagercivil.domain.models.FinancialTask
import com.project.taskmanagercivil.presentation.components.FinancialTaskRow

/**
 * Adapters para converter domain models em presentation models
 */

fun FinancialTask.toTaskRow(): FinancialTaskRow {
    return FinancialTaskRow(
        number = this.number,
        taskName = this.taskName,
        projectName = this.projectName,
        responsibleName = this.responsibleName,
        estimatedDays = this.estimatedDays,
        actualDays = this.actualDays,
        estimatedCost = this.estimatedCost,
        actualCost = this.actualCost,
        profitLoss = this.profitLoss,
        revisions = this.revisions,
        status = this.status
    )
}

fun List<FinancialTask>.toTaskRows(): List<FinancialTaskRow> {
    return this.map { it.toTaskRow() }
}
