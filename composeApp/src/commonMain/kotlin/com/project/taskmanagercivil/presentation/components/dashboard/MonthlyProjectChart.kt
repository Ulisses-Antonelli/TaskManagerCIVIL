package com.project.taskmanagercivil.presentation.components.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.MonthlyProjectData
import com.project.taskmanagercivil.presentation.theme.extendedColors

/**
 * Card com gráfico de colunas sobrepostas mostrando projetos por mês
 */
@Composable
fun MonthlyProjectChart(
    monthlyData: List<MonthlyProjectData>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Projetos por Mês",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Distribuição de status ao longo do tempo",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Legenda
            ChartLegend()

            Spacer(modifier = Modifier.height(16.dp))

            // Gráfico
            StackedBarChart(
                data = monthlyData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }
    }
}

@Composable
private fun ChartLegend(
    modifier: Modifier = Modifier
) {
    val extendedColors = MaterialTheme.extendedColors

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem("A Fazer", extendedColors.statusTodo)
        LegendItem("Em Andamento", extendedColors.statusInProgress)
        LegendItem("Em Revisão", extendedColors.statusInReview)
        LegendItem("Concluída", extendedColors.statusCompleted)
        LegendItem("Bloqueada", extendedColors.statusBlocked)
    }
}

@Composable
private fun LegendItem(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            modifier = Modifier.size(12.dp),
            shape = RoundedCornerShape(2.dp),
            color = color
        ) {}

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StackedBarChart(
    data: List<MonthlyProjectData>,
    modifier: Modifier = Modifier
) {
    val extendedColors = MaterialTheme.extendedColors
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    if (data.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Sem dados disponíveis",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    // Encontra o valor máximo para escalar o gráfico
    val maxValue = data.maxOfOrNull { it.total } ?: 1

    Column(modifier = modifier) {
        // Área do gráfico
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val barWidth = (canvasWidth / data.size) * 0.7f
                val spacing = (canvasWidth / data.size) * 0.3f

                data.forEachIndexed { index, monthData ->
                    val x = index * (barWidth + spacing) + spacing / 2

                    // Calcula as alturas das seções
                    val scale = canvasHeight / maxValue.toFloat()
                    var currentY = canvasHeight

                    // Desenha cada segmento da coluna (de baixo para cima)
                    val segments = listOf(
                        monthData.todoCount to extendedColors.statusTodo,
                        monthData.inProgressCount to extendedColors.statusInProgress,
                        monthData.inReviewCount to extendedColors.statusInReview,
                        monthData.completedCount to extendedColors.statusCompleted,
                        monthData.blockedCount to extendedColors.statusBlocked
                    )

                    segments.forEach { (count, color) ->
                        if (count > 0) {
                            val segmentHeight = count * scale
                            drawRoundRect(
                                color = color,
                                topLeft = Offset(x, currentY - segmentHeight),
                                size = Size(barWidth, segmentHeight),
                                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                            )
                            currentY -= segmentHeight
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Labels dos meses
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { monthData ->
                Text(
                    text = monthData.month.split("/")[0], // Apenas o mês (Jan, Fev, etc)
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
