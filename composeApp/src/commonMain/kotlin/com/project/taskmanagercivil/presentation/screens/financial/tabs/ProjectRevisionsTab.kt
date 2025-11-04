package com.project.taskmanagercivil.presentation.screens.financial.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.ProjectFinancials
import com.project.taskmanagercivil.presentation.components.*

@Composable
fun ProjectRevisionsTab(projectFinancials: ProjectFinancials) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Resumo das Revisões
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Resumo das Revisões:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoRow("Total de Revisões:", "${projectFinancials.totalRevisions}", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Custo Total de Revisões:", formatCurrency(projectFinancials.revisionCost), Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Impacto no Prazo:", "+${projectFinancials.scheduleImpactDays} dias", Modifier.weight(1f), isAlert = true)
            }
        }

        // Revisões por Disciplina
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Revisões por Disciplina:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val revisionsByDiscipline = projectFinancials.revisionsByDiscipline.entries.toList()
                revisionsByDiscipline.forEachIndexed { index, (disciplineName, revisionCount) ->
                    DisciplineItem("$disciplineName:", "$revisionCount")
                    if (index < revisionsByDiscipline.size - 1) {
                        VerticalDivider(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }

        // Principais Causas de Revisão
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Principais Causas de Revisão:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                projectFinancials.revisionCauses.forEachIndexed { index, revisionCause ->
                    RevisionCauseItem(index + 1, revisionCause.cause, "${revisionCause.count}")
                }
            }
        }
    }
}

private fun formatCurrency(value: Double): String {
    val intPart = value.toInt()
    val intStr = intPart.toString()
    val formattedInt = if (intStr.length > 3) {
        intStr.reversed().chunked(3).joinToString(".").reversed()
    } else { intStr }
    return "R$ $formattedInt,00"
}
