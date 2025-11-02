package com.project.taskmanagercivil.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Dados de uma tarefa para indicadores financeiros
 */
data class FinancialTaskRow(
    val number: Int,
    val taskName: String,
    val projectName: String,
    val responsibleName: String,
    val estimatedDays: Int?,
    val actualDays: Int?,
    val estimatedCost: Double,
    val actualCost: Double?,
    val profitLoss: Double?,
    val revisions: Int,
    val status: String
)

/**
 * Tabela de tarefas com indicadores financeiros
 * Segue o mesmo padrão visual de EmployeeTasksTable
 */
@Composable
fun FinancialTasksTable(
    tasks: List<FinancialTaskRow>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Cabeçalhos da tabela
            FinancialTableHeader()

            Spacer(modifier = Modifier.height(8.dp))

            // Linhas da tabela
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(tasks) { index, row ->
                    FinancialTableRow(row = row)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legendas
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Legenda Lucro/Prejuízo: (+) Lucro   (-) Prejuízo",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Legenda Status: Em curso / Em revisão / Concluída / Atrasada",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FinancialTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FinancialHeaderCell("#", Modifier.width(40.dp))

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        FinancialHeaderCell("Tarefa", Modifier.weight(2f))

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        FinancialHeaderCell("Obra", Modifier.weight(1.2f))

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        FinancialHeaderCell("Resp.", Modifier.weight(1f))

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        FinancialHeaderCell("Prev. (Dias)", Modifier.weight(0.9f))

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        FinancialHeaderCell("Real (Dias)", Modifier.weight(0.9f))

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        FinancialHeaderCell("Prev. R$", Modifier.weight(1f))

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        FinancialHeaderCell("Real R$", Modifier.weight(1f))

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        FinancialHeaderCell("Lucro/Prej.", Modifier.weight(1f))

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        FinancialHeaderCell("Rev.", Modifier.width(50.dp))

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        FinancialHeaderCell("Status", Modifier.weight(1f))
    }
}

@Composable
private fun FinancialHeaderCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Composable
private fun FinancialTableRow(row: FinancialTaskRow) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // #
        Text(
            text = row.number.toString().padStart(2, '0'),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(40.dp)
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        // Tarefa
        Text(
            text = row.taskName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(2f)
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        // Obra
        Text(
            text = row.projectName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1.2f)
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        // Responsável
        Text(
            text = row.responsibleName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        // Prev. (Dias)
        Text(
            text = row.estimatedDays?.toString() ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.9f)
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        // Real (Dias)
        Text(
            text = row.actualDays?.toString() ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.9f)
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        // Prev. R$
        Text(
            text = formatCurrency(row.estimatedCost),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        // Real R$
        Text(
            text = row.actualCost?.let { formatCurrency(it) } ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        // Lucro/Prej.
        val profitLossColor = when {
            row.profitLoss == null -> MaterialTheme.colorScheme.onSurface
            row.profitLoss > 0 -> Color(0xFF4CAF50) // Verde para lucro
            row.profitLoss < 0 -> Color(0xFFF44336) // Vermelho para prejuízo
            else -> MaterialTheme.colorScheme.onSurface
        }
        Text(
            text = row.profitLoss?.let {
                (if (it > 0) "+" else "") + formatCurrency(it)
            } ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = profitLossColor,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        // Rev.
        Text(
            text = row.revisions.toString(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(50.dp)
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        // Status
        StatusBadge(status = row.status, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val (backgroundColor, textColor) = when (status) {
        "Concluída" -> Color(0xFF4CAF50) to Color.White
        "Em curso" -> Color(0xFF2196F3) to Color.White
        "Em revisão" -> Color(0xFFFF9800) to Color.White
        "Atrasada" -> Color(0xFFF44336) to Color.White
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private fun formatCurrency(value: Double): String {
    // Formatação simples para compatibilidade com KMP
    val intPart = value.toInt()
    val decimalPart = ((value - intPart) * 100).toInt().toString().padStart(2, '0')

    // Adiciona separador de milhares
    val intStr = intPart.toString()
    val formattedInt = if (intStr.length > 3) {
        intStr.reversed().chunked(3).joinToString(".").reversed()
    } else {
        intStr
    }

    return "$formattedInt,$decimalPart"
}
