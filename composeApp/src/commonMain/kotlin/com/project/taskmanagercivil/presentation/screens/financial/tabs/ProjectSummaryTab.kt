package com.project.taskmanagercivil.presentation.screens.financial.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.presentation.components.*

@Composable
fun ProjectSummaryTab() {
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
                InfoRow("Cliente:", "Construtora XYZ", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Responsável Técnico:", "Eng. Marcos", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Início:", "05/08/2025", Modifier.weight(1f))
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
                InfoRow("Status:", "Em Execução", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Prazo Previsto:", "90 dias", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Prazo Real:", "102 dias (-12)", Modifier.weight(1f), isAlert = true)
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
                ProjectKPIItem("Progresso Físico:", "68%", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                ProjectKPIItem("Disciplinas:", "4", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                ProjectKPIItem("Tarefas:", "32 (22 concl. / 10)", Modifier.weight(1f))
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.3f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProjectKPIItem("Progresso Financeiro:", "64%", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                ProjectKPIItem("Eficiência Média:", "1.12x", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                ProjectKPIItem("Retrabalho:", "9%", Modifier.weight(1f))
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

            ProgressBarRow("Físico:", 0.68f, "68%")
            ProgressBarRow("Financeiro:", 0.64f, "64%")
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
                DisciplineItem("Arquitetura:", "10")
                VerticalDivider(modifier = Modifier.height(40.dp))
                DisciplineItem("Estrutural:", "8")
                VerticalDivider(modifier = Modifier.height(40.dp))
                DisciplineItem("Elétrica:", "7")
                VerticalDivider(modifier = Modifier.height(40.dp))
                DisciplineItem("Hidráulica:", "7")
            }
        }
    }
}
