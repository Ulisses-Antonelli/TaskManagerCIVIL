package com.project.taskmanagercivil.presentation.screens.financial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.CompanyFinancials
import com.project.taskmanagercivil.presentation.components.KPIColumn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyFinancialPanel(
    companyFinancials: CompanyFinancials?,
    onPeriodChange: (String) -> Unit = {}
) {
    if (companyFinancials == null) {
        // Loading state
        Box(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabeçalho com título e período
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FINANCEIRO DA EMPRESA",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Seletor de período
                var periodoExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = periodoExpanded,
                    onExpandedChange = { periodoExpanded = it }
                ) {
                    OutlinedButton(
                        onClick = { periodoExpanded = true },
                        modifier = Modifier.menuAnchor()
                    ) {
                        Text(companyFinancials.period)
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                    ExposedDropdownMenu(
                        expanded = periodoExpanded,
                        onDismissRequest = { periodoExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mês Atual") },
                            onClick = {
                                onPeriodChange("current_month")
                                periodoExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Último Mês") },
                            onClick = {
                                onPeriodChange("last_month")
                                periodoExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Últimos 3 Meses") },
                            onClick = {
                                onPeriodChange("last_3_months")
                                periodoExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ano Atual") },
                            onClick = {
                                onPeriodChange("current_year")
                                periodoExpanded = false
                            }
                        )
                    }
                }
            }

            // KPIs do Mês Selecionado
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
                    text = "KPIs do Mês Selecionado:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    KPIColumn("Faturamento:", formatCurrency(companyFinancials.revenue), Modifier.weight(1f))
                    VerticalDivider(modifier = Modifier.height(50.dp))
                    KPIColumn("Custos Totais:", formatCurrency(companyFinancials.totalCosts), Modifier.weight(1f))
                    VerticalDivider(modifier = Modifier.height(50.dp))
                    KPIColumn("Lucro Líquido:", formatCurrency(companyFinancials.netProfit), Modifier.weight(1f), valueColor = Color(0xFF4CAF50))
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    KPIColumn("Margem Líquida:", formatPercentage(companyFinancials.netMargin), Modifier.weight(1f))
                    KPIColumn("Obras Ativas:", companyFinancials.activeProjects.toString(), Modifier.weight(1f))
                    KPIColumn("Obras Finalizadas:", companyFinancials.completedProjects.toString(), Modifier.weight(1f))
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    KPIColumn("Média de Retrabalho:", formatPercentage(companyFinancials.averageRework), Modifier.weight(1f))
                    VerticalDivider(modifier = Modifier.height(50.dp))
                    KPIColumn("Eficiência Geral:", "${formatDecimal(companyFinancials.overallEfficiency)}x", Modifier.weight(1f))
                    VerticalDivider(modifier = Modifier.height(50.dp))
                    Column(modifier = Modifier.weight(1f)) {}
                }
            }

            // Resumo de Resultados (Últimos 6 Meses)
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
                    text = "Resumo de Resultados (Últimos 6 Meses):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                ResultProgressBar("Faturamento:", 0.85f, "R$ 840.000")
                ResultProgressBar("Custos:", 0.65f, "R$ 546.000")
                ResultProgressBar("Lucro Líquido:", 0.45f, "R$ 294.000")
            }

            // Ranking das Obras
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
                    text = "Ranking das Obras (por lucro do mês):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                companyFinancials.projectRankings.forEachIndexed { index, ranking ->
                    if (index > 0) {
                        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))
                    }
                    val isPositive = ranking.profit >= 0
                    val sign = if (isPositive) "+" else ""
                    val label = if (!isPositive) "(prejuízo)" else ""
                    RankingItem(
                        ranking.position,
                        ranking.projectName,
                        "$sign${formatCurrency(ranking.profit)}",
                        isPositive,
                        label
                    )
                }
            }

            // Receitas x Despesas
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
                    text = "RECEITAS x DESPESAS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Receitas
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Receitas do Mês: R$ 142.000,00",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    Text(
                        text = "  - Obras: R$ 138.000,00",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "  - Outras Receitas: R$ 4.000,00",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                HorizontalDivider(thickness = 1.dp)

                // Despesas
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Despesas do Mês: R$ 98.400,00",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF44336)
                    )
                    Text(
                        text = "  - Custos com Obras: R$ 78.500,00",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "  - Administrativos: R$ 12.700,00",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "  - Impostos/Taxas: R$ 7.200,00",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                HorizontalDivider(thickness = 1.dp)

                // Resultado
                Text(
                    text = "Resultado Líquido: R$ 43.600,00 (Positivo)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }

            // Fluxo de Caixa
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Fluxo de Caixa:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                CashFlowItem("Contas a Receber (30 dias):", "R$ 52.300")
                CashFlowItem("Contas a Pagar (30 dias):", "R$ 27.900")

                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Saldo Previsto:",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "R$ 24.400",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }

            // Previsão de Caixa
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
                    text = "Previsão de Caixa (3 meses):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                CashForecastBar("Mês Atual:", 0.55f)
                CashForecastBar("+1 Mês:", 0.65f)
                CashForecastBar("+2 Meses:", 0.51f)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "↑ tendência leve de crescimento",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Indicadores Complementares
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "INDICADORES COMPLEMENTARES",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ComplementaryIndicator("Custo Médio por Tarefa:", "R$ 1.780", Modifier.weight(1f))
                    VerticalDivider(modifier = Modifier.height(50.dp))
                    ComplementaryIndicator("Ticket Médio por Obra:", "R$ 47.300", Modifier.weight(1f))
                }

                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.2f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ComplementaryIndicator("% Tarefas no Prazo:", "74%", Modifier.weight(1f))
                    VerticalDivider(modifier = Modifier.height(50.dp))
                    ComplementaryIndicator("% Obras no Prazo:", "61%", Modifier.weight(1f))
                }

                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.2f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ComplementaryIndicator("% de Retrabalho da Empresa:", "8%", Modifier.weight(1f))
                    VerticalDivider(modifier = Modifier.height(50.dp))
                    ComplementaryIndicator("Meta de Retrabalho:", "<= 5%", Modifier.weight(1f))
                }

                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.2f))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tempo Médio de Execução por Tarefa:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "3,8 dias",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}

// Componentes auxiliares
@Composable
private fun ResultProgressBar(label: String, progress: Float, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(20.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun RankingItem(position: Int, name: String, value: String, isPositive: Boolean, label: String = "") {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$position.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                if (label.isNotEmpty()) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)
        )
    }
}

@Composable
private fun CashFlowItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun CashForecastBar(label: String, progress: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(80.dp)
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.weight(1f).height(20.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun ComplementaryIndicator(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

// Helper functions
private fun formatCurrency(value: Double): String {
    val intPart = value.toInt()
    val intStr = intPart.toString()
    val formattedInt = if (intStr.length > 3) {
        intStr.reversed().chunked(3).joinToString(".").reversed()
    } else {
        intStr
    }
    return "R$ $formattedInt"
}

private fun formatPercentage(value: Double): String {
    return "${(value * 100).toInt()}%"
}

private fun formatDecimal(value: Double): String {
    val intPart = value.toInt()
    val decimal = ((value - intPart) * 100).toInt()
    return "$intPart.${decimal.toString().padStart(2, '0')}"
}
