package com.project.taskmanagercivil.presentation.screens.financial.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.ProjectFinancials
import com.project.taskmanagercivil.presentation.components.*

@Composable
fun ProjectSummaryTab(projectFinancials: ProjectFinancials) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Informações principais da obra
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
                text = "Informações da Obra:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // Linha 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoRow("Cliente:", projectFinancials.client, Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Responsável Técnico:", projectFinancials.technicalManager, Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Início:", projectFinancials.startDate, Modifier.weight(1f))
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            )

            // Linha 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoRow("Status:", projectFinancials.status, Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Prazo Previsto:", "${projectFinancials.estimatedDuration} dias", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                val durationText = if (projectFinancials.actualDuration != null && projectFinancials.durationDelta != null) {
                    "${projectFinancials.actualDuration} dias (${projectFinancials.durationDelta})"
                } else {
                    "Em andamento"
                }
                InfoRow("Prazo Real:", durationText, Modifier.weight(1f), isAlert = projectFinancials.durationDelta != null && projectFinancials.durationDelta!! < 0)
            }
        }

        // KPIs da Obra
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
                text = "KPIs da Obra:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProjectKPIItem("Progresso Físico:", "${(projectFinancials.physicalProgress * 100).toInt()}%", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                ProjectKPIItem("Disciplinas:", "${projectFinancials.disciplines}", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                ProjectKPIItem("Tarefas:", "${projectFinancials.totalTasks} (${projectFinancials.completedTasks} concl. / ${projectFinancials.pendingTasks})", Modifier.weight(1f))
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.3f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProjectKPIItem("Progresso Financeiro:", "${(projectFinancials.financialProgress * 100).toInt()}%", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                val intPart = projectFinancials.averageEfficiency.toInt()
                val decimal = ((projectFinancials.averageEfficiency - intPart) * 100).toInt()
                ProjectKPIItem("Eficiência Média:", "$intPart.${decimal.toString().padStart(2, '0')}x", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                ProjectKPIItem("Retrabalho:", "${(projectFinancials.reworkPercentage * 100).toInt()}%", Modifier.weight(1f))
            }
        }

        // Progresso (Físico vs Financeiro)
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
                text = "Progresso (Físico vs Financeiro):",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ProgressBarRow("Físico:", projectFinancials.physicalProgress.toFloat(), "${(projectFinancials.physicalProgress * 100).toInt()}%")
            ProgressBarRow("Financeiro:", projectFinancials.financialProgress.toFloat(), "${(projectFinancials.financialProgress * 100).toInt()}%")
        }

        // Disciplinas (Distribuição de Tarefas)
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
                text = "Disciplinas (Distribuição de Tarefas):",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val disciplines = projectFinancials.disciplineDistribution.entries.toList()
                disciplines.forEachIndexed { index, (disciplineName, taskCount) ->
                    DisciplineItem("$disciplineName:", "$taskCount")
                    if (index < disciplines.size - 1) {
                        VerticalDivider(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}
