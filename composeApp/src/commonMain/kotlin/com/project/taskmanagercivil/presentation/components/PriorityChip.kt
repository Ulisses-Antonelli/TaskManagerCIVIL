package com.project.taskmanagercivil.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.TaskPriority
import com.project.taskmanagercivil.presentation.theme.extendedColors

@Composable
fun PriorityChip(
    priority: TaskPriority,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (priority) {
        TaskPriority.LOW -> MaterialTheme.extendedColors.priorityLow
        TaskPriority.MEDIUM -> MaterialTheme.extendedColors.priorityMedium
        TaskPriority.HIGH -> MaterialTheme.extendedColors.priorityMedium
        TaskPriority.CRITICAL -> MaterialTheme.extendedColors.priorityCritical
    }

    Text(
        text = priority.label,
        style = MaterialTheme.typography.labelSmall,
        color = backgroundColor,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}