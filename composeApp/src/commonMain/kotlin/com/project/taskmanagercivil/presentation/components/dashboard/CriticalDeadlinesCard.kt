package com.project.taskmanagercivil.presentation.components.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.CriticalDeadline
import com.project.taskmanagercivil.presentation.components.PriorityChip
import com.project.taskmanagercivil.utils.formatDate

/**
 * Card com lista de prazos críticos
 */
@Composable
fun CriticalDeadlinesCard(
    criticalDeadlines: List<CriticalDeadline>,
    onDeadlineClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .clip(RoundedCornerShape(12.dp)),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Alertas",
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Prazos Críticos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (criticalDeadlines.isEmpty()) {
                Text(
                    text = "Nenhum prazo crítico no momento",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(criticalDeadlines) { deadline ->
                        CriticalDeadlineItem(
                            deadline = deadline,
                            onClick = { onDeadlineClick(deadline.taskId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CriticalDeadlineItem(
    deadline: CriticalDeadline,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = deadline.taskTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = deadline.projectName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PriorityChip(priority = deadline.priority)

                    Text(
                        text = "Prazo: ${formatDate(deadline.dueDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Indicador de dias restantes
            val daysRemainingColor = when {
                deadline.daysRemaining < 0 -> MaterialTheme.colorScheme.error
                deadline.daysRemaining <= 3 -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.primary
            }

            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(8.dp),
                color = daysRemainingColor.copy(alpha = 0.15f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (deadline.daysRemaining < 0) {
                            "${-deadline.daysRemaining}"
                        } else {
                            "${deadline.daysRemaining}"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = daysRemainingColor
                    )
                    Text(
                        text = when {
                            deadline.daysRemaining < 0 -> "atraso"
                            deadline.daysRemaining == 0 -> "hoje"
                            deadline.daysRemaining == 1 -> "dia"
                            else -> "dias"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = daysRemainingColor
                    )
                }
            }
        }
    }
}
