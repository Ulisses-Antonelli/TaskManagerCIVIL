package com.project.taskmanagercivil.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.TaskStatus
import com.project.taskmanagercivil.presentation.theme.extendedColors

@Composable
fun StatusBadge (
    status: TaskStatus,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (status) {
        TaskStatus.TODO -> MaterialTheme.extendedColors.statusTodo
        TaskStatus.IN_PROGRESS -> MaterialTheme.extendedColors.statusInProgress
        TaskStatus.IN_REVIEW -> MaterialTheme.extendedColors.statusInReview
        TaskStatus.COMPLETED -> MaterialTheme.extendedColors.statusCompleted
        TaskStatus.BLOCKED -> MaterialTheme.extendedColors.statusBlocked
    }

    Text(
        text = status.label,
        style = MaterialTheme.typography.labelSmall,
        color = Color.White,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}