package com.project.taskmanagercivil.presentation.components.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.TaskStatus
import com.project.taskmanagercivil.presentation.theme.extendedColors

/**
 * Card de resumo de status de obras
 */
@Composable
fun StatusSummaryCard(
    status: TaskStatus,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = MaterialTheme.extendedColors
    val backgroundColor = when (status) {
        TaskStatus.TODO -> extendedColors.statusTodo
        TaskStatus.IN_PROGRESS -> extendedColors.statusInProgress
        TaskStatus.IN_REVIEW -> extendedColors.statusInReview
        TaskStatus.COMPLETED -> extendedColors.statusCompleted
        TaskStatus.BLOCKED -> extendedColors.statusBlocked
        TaskStatus.INATIVA -> Color(0xFF757575) // Cinza para inativa
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Indicador de cor
            Surface(
                modifier = Modifier
                    .size(48.dp),
                shape = RoundedCornerShape(24.dp),
                color = backgroundColor.copy(alpha = 0.2f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = backgroundColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Label do status
            Text(
                text = status.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (count == 1) "obra" else "obras",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
