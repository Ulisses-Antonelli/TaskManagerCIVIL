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
fun ProjectFinancialTab() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Informações Financeiras Principais
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
                text = "Informações Financeiras:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // Linha 1: Valores principais
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoRow("Valor Contratado:", "R$ 95.000,00", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Custo Previsto da Obra:", "R$ 62.500,00", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Custo Real Atual:", "R$ 64.800,00", Modifier.weight(1f))
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            )

            // Linha 2: Resultados e Margens
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoRow("Resultado Previsto:", "+R$ 32.500,00", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Resultado Atual Projetado:", "+R$ 30.200,00", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                Column(modifier = Modifier.weight(1f)) {
                    // Vazio para manter o alinhamento
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            )

            // Linha 3: Margens
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoRow("Margem Prevista:", "34%", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Margem Atual:", "31.8%", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                Column(modifier = Modifier.weight(1f)) {
                    // Vazio para manter o alinhamento
                }
            }
        }

        // Custos por Categoria
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
                text = "Custos por Categoria:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CostCategoryItem("Mão de Obra Interna:", "R$ 49.300,00")
                CostCategoryItem("Retrabalho/Revisões:", "R$ 5.900,00")
                CostCategoryItem("Terceirizados:", "R$ 7.800,00")
                CostCategoryItem("Deslocamentos / Taxas:", "R$ 1.800,00")
            }
        }

        // Curva S (Previsto vs Real)
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
                text = "Curva S (Previsto vs Real):",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ProgressBarRow("Previsto:", 0.72f, "72%")
            ProgressBarRow("Real:", 0.64f, "64%")
        }
    }
}
