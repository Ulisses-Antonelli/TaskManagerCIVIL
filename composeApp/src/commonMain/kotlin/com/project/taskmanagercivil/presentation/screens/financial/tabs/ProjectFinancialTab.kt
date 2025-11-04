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
fun ProjectFinancialTab(projectFinancials: ProjectFinancials) {
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
                InfoRow("Valor Contratado:", formatCurrency(projectFinancials.contractValue), Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Custo Previsto da Obra:", formatCurrency(projectFinancials.estimatedCost), Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Custo Real Atual:", formatCurrency(projectFinancials.actualCost), Modifier.weight(1f))
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
                val estimatedSign = if (projectFinancials.estimatedProfit >= 0) "+" else ""
                InfoRow("Resultado Previsto:", "$estimatedSign${formatCurrency(projectFinancials.estimatedProfit)}", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                val projectedSign = if (projectFinancials.projectedProfit >= 0) "+" else ""
                InfoRow("Resultado Atual Projetado:", "$projectedSign${formatCurrency(projectFinancials.projectedProfit)}", Modifier.weight(1f))
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
                InfoRow("Margem Prevista:", "${(projectFinancials.estimatedMargin * 100).toInt()}%", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                val actualMarginInt = projectFinancials.actualMargin.toInt()
                val actualMarginDecimal = ((projectFinancials.actualMargin - actualMarginInt) * 1000).toInt()
                InfoRow("Margem Atual:", "$actualMarginInt.${actualMarginDecimal.toString().padStart(1, '0')}%", Modifier.weight(1f))
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
                CostCategoryItem("Mão de Obra Interna:", formatCurrency(projectFinancials.internalLaborCost))
                CostCategoryItem("Retrabalho/Revisões:", formatCurrency(projectFinancials.reworkCost))
                CostCategoryItem("Terceirizados:", formatCurrency(projectFinancials.outsourcedCost))
                CostCategoryItem("Deslocamentos / Taxas:", formatCurrency(projectFinancials.travelTaxesCost))
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
            ProgressBarRow("Real:", projectFinancials.financialProgress.toFloat(), "${(projectFinancials.financialProgress * 100).toInt()}%")
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
