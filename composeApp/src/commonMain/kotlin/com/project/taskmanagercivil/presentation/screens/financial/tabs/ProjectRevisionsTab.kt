package com.project.taskmanagercivil.presentation.screens.financial.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.presentation.components.*

@Composable
fun ProjectRevisionsTab() {
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
                InfoRow("Total de Revisões:", "7", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Custo Total de Revisões:", "R$ 5.900,00", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Impacto no Prazo:", "+14 dias", Modifier.weight(1f), isAlert = true)
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
                DisciplineItem("Arquitetura:", "2")
                VerticalDivider(modifier = Modifier.height(40.dp))
                DisciplineItem("Elétrica:", "3")
                VerticalDivider(modifier = Modifier.height(40.dp))
                DisciplineItem("Hidráulica:", "2")
                VerticalDivider(modifier = Modifier.height(40.dp))
                DisciplineItem("Estrutural:", "0")
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
                RevisionCauseItem(1, "Mudança solicitada pelo cliente", "4")
                RevisionCauseItem(2, "Erro de compatibilização", "2")
                RevisionCauseItem(3, "Falha de comunicação", "1")
            }
        }
    }
}
