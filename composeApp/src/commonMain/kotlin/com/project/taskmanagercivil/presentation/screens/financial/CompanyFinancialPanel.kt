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
import com.project.taskmanagercivil.presentation.components.KPIColumn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyFinancialPanel() {
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
                        Text("Mês Atual")
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                    ExposedDropdownMenu(
                        expanded = periodoExpanded,
                        onDismissRequest = { periodoExpanded = false }
                    ) {
                        DropdownMenuItem(text = { Text("Mês Atual") }, onClick = { periodoExpanded = false })
                        DropdownMenuItem(text = { Text("Último Mês") }, onClick = { periodoExpanded = false })
                        DropdownMenuItem(text = { Text("Últimos 3 Meses") }, onClick = { periodoExpanded = false })
                        DropdownMenuItem(text = { Text("Ano Atual") }, onClick = { periodoExpanded = false })
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
                    KPIColumn("Faturamento:", "R$ 142.000", Modifier.weight(1f))
                    VerticalDivider(modifier = Modifier.height(50.dp))
                    KPIColumn("Custos Totais:", "R$ 98.400", Modifier.weight(1f))
                    VerticalDivider(modifier = Modifier.height(50.dp))
                    KPIColumn("Lucro Líquido:", "R$ 43.600", Modifier.weight(1f), valueColor = Color(0xFF4CAF50))
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    KPIColumn("Margem Líquida:", "30.7%", Modifier.weight(1f))
                    KPIColumn("Obras Ativas:", "6", Modifier.weight(1f))
                    KPIColumn("Obras Finalizadas:", "2", Modifier.weight(1f))
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    KPIColumn("Média de Retrabalho:", "8%", Modifier.weight(1f))
                    VerticalDivider(modifier = Modifier.height(50.dp))
                    KPIColumn("Eficiência Geral:", "1.14x", Modifier.weight(1f))
                    VerticalDivider(modifier = Modifier.height(50.dp))
                    Column(modifier = Modifier.weight(1f)) {}
                }
            }

            // Outros indicadores podem ser expandidos posteriormente
            Text(
                text = "Outros indicadores em desenvolvimento...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
