package com.project.taskmanagercivil.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.domain.models.TaskStatus
import com.project.taskmanagercivil.domain.models.Team
import com.project.taskmanagercivil.presentation.theme.extendedColors
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * Dados agregados de um colaborador e suas tarefas
 */
data class EmployeeTaskRow(
    val employeeId: String,
    val employeeName: String,
    val taskId: String,
    val taskTitle: String,
    val taskStatus: TaskStatus,
    val teamName: String,
    val startDate: String,
    val dueDate: String,
    val daysOverdue: Int,
    val progress: Float
)

/**
 * Componente de tabela mostrando colaboradores e suas tarefas
 * Com navegação contextual para colaborador e time
 */
@Composable
fun EmployeeTasksTable(
    tasks: List<Task>,
    availableTeams: List<Team> = emptyList(),
    onEmployeeClick: (String) -> Unit = {},
    onTeamClick: (String) -> Unit = {},
    onEditTaskAssignment: (String, String, String) -> Unit = { _, _, _ -> }, // taskId, employeeId, teamId
    modifier: Modifier = Modifier
) {
    // Agrupa tarefas por colaborador
    val employeeTaskRows = tasks.map { task ->
        // Busca o time do colaborador
        val employeeTeam = availableTeams.find { team ->
            team.memberIds.contains(task.assignedTo.id)
        }

        EmployeeTaskRow(
            employeeId = task.assignedTo.id,
            employeeName = task.assignedTo.name,
            taskId = task.id,
            taskTitle = task.title,
            taskStatus = task.status,
            teamName = employeeTeam?.name ?: "Sem time", // Nome do departamento/time
            startDate = task.startDate.toString(),
            dueDate = task.dueDate.toString(),
            daysOverdue = calculateDaysOverdue(task),
            progress = task.progress
        )
    }.sortedBy { it.employeeName } // Agrupa por colaborador

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Colaboradores e Tarefas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Cabeçalhos da tabela
            TableHeader()

            Spacer(modifier = Modifier.height(8.dp))

            // Linhas da tabela
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(employeeTaskRows) { row ->
                    TableRow(
                        row = row,
                        onEmployeeClick = onEmployeeClick,
                        onTeamClick = onTeamClick,
                        onEditClick = { onEditTaskAssignment(row.taskId, row.employeeId, row.employeeId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell("Colaborador", Modifier.weight(1f))

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        HeaderCell("Tarefa", Modifier.weight(2f))

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        HeaderCell("Status", Modifier.weight(1f))

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        HeaderCell("Time", Modifier.weight(1f))

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        HeaderCell("Início", Modifier.weight(0.8f))

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        HeaderCell("Entrega", Modifier.weight(0.8f))

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        HeaderCell("Atraso", Modifier.weight(0.6f))

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        HeaderCell("Progresso", Modifier.weight(0.5f))

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        HeaderCell("Ações", Modifier.width(48.dp))
    }
}

@Composable
private fun HeaderCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@Composable
private fun TableRow(
    row: EmployeeTaskRow,
    onEmployeeClick: (String) -> Unit,
    onTeamClick: (String) -> Unit,
    onEditClick: () -> Unit
) {
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
        // Colaborador (clicável)
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onEmployeeClick(row.employeeId) }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Avatar placeholder
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = row.employeeName.take(1).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Text(
                    text = row.employeeName,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        // Tarefa
        Box(
            modifier = Modifier.weight(2f),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = row.taskTitle,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        // Status
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            StatusChip(
                status = row.taskStatus,
                modifier = Modifier.fillMaxWidth()
            )
        }

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        // Time (clicável)
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = row.teamName,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onTeamClick(row.employeeId) } // Em produção seria teamId
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        // Data Início
        Box(
            modifier = Modifier.weight(0.8f),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = row.startDate.take(10),
                style = MaterialTheme.typography.bodySmall
            )
        }

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        // Data Entrega
        Box(
            modifier = Modifier.weight(0.8f),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = row.dueDate.take(10),
                style = MaterialTheme.typography.bodySmall
            )
        }

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        // Dias de Atraso
        Box(
            modifier = Modifier.weight(0.6f),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = if (row.daysOverdue > 0) "+${row.daysOverdue}" else "0",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (row.daysOverdue > 0) FontWeight.Bold else FontWeight.Normal,
                color = if (row.daysOverdue > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        // Progresso
        Box(
            modifier = Modifier.weight(0.5f),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "${row.progress.toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        // Ações
        Box(
            modifier = Modifier.width(48.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar colaborador/equipe",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun StatusChip(
    status: TaskStatus,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.extendedColors
    val statusColor = when (status) {
        TaskStatus.TODO -> colors.statusTodo
        TaskStatus.IN_PROGRESS -> colors.statusInProgress
        TaskStatus.IN_REVIEW -> colors.statusInReview
        TaskStatus.COMPLETED -> colors.statusCompleted
        TaskStatus.BLOCKED -> colors.statusBlocked
        TaskStatus.INATIVA -> Color(0xFF757575) // Cinza
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = statusColor.copy(alpha = 0.15f)
    ) {
        Text(
            text = status.label,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = statusColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Calcula dias de atraso baseado na data de entrega
 */
private fun calculateDaysOverdue(task: Task): Int {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val daysUntilDue = (task.dueDate.toEpochDays() - today.toEpochDays()).toInt()

    // Retorna dias de atraso (negativo = atrasado, positivo = no prazo)
    return if (daysUntilDue < 0 && task.status != TaskStatus.COMPLETED) {
        -daysUntilDue
    } else {
        0
    }
}
