package com.project.taskmanagercivil.presentation.screens.teams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.models.Team
import com.project.taskmanagercivil.domain.models.TeamDepartment
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamFormModal(
    team: Team? = null,
    availableEmployees: List<Employee> = emptyList(),
    availableProjects: List<Project> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (Team) -> Unit
) {
    // Estados do formulário
    var teamName by remember { mutableStateOf(team?.name ?: "") }
    var description by remember { mutableStateOf(team?.description ?: "") }
    var department by remember { mutableStateOf(team?.department ?: TeamDepartment.ARCHITECTURE) }
    var leaderId by remember { mutableStateOf(team?.leaderId) }
    var isActive by remember { mutableStateOf(team?.isActive ?: true) }

    // Estados para membros e projetos selecionados
    var selectedMemberIds by remember { mutableStateOf(team?.memberIds?.toSet() ?: emptySet()) }
    var selectedProjectIds by remember { mutableStateOf(team?.projectIds?.toSet() ?: emptySet()) }

    // Estado para dropdown de departamento
    var departmentExpanded by remember { mutableStateOf(false) }
    var leaderExpanded by remember { mutableStateOf(false) }

    // Validação
    val isValid = teamName.isNotBlank()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (team == null) "Novo Time" else "Editar Time",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar"
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Formulário
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Informações Básicas
                    item {
                        Text(
                            text = "Informações Básicas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = teamName,
                            onValueChange = { teamName = it },
                            label = { Text("Nome do Time *") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Ex: Equipe de Hidráulica") },
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descrição") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Descreva o propósito do time...") },
                            minLines = 2,
                            maxLines = 4
                        )
                    }

                    // Departamento
                    item {
                        ExposedDropdownMenuBox(
                            expanded = departmentExpanded,
                            onExpandedChange = { departmentExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = department.displayName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Departamento *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = departmentExpanded) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = departmentExpanded,
                                onDismissRequest = { departmentExpanded = false }
                            ) {
                                TeamDepartment.entries.forEach { dept ->
                                    DropdownMenuItem(
                                        text = { Text(dept.displayName) },
                                        onClick = {
                                            department = dept
                                            departmentExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Líder do Time
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Líder do Time",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        ExposedDropdownMenuBox(
                            expanded = leaderExpanded,
                            onExpandedChange = { leaderExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = leaderId?.let { id ->
                                    availableEmployees.find { it.id == id }?.fullName ?: "Nenhum"
                                } ?: "Nenhum",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Líder") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = leaderExpanded) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = leaderExpanded,
                                onDismissRequest = { leaderExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Nenhum") },
                                    onClick = {
                                        leaderId = null
                                        leaderExpanded = false
                                    }
                                )
                                availableEmployees.forEach { employee ->
                                    DropdownMenuItem(
                                        text = { Text(employee.fullName) },
                                        onClick = {
                                            leaderId = employee.id
                                            leaderExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Checkbox(
                                checked = isActive,
                                onCheckedChange = { isActive = it }
                            )
                            Text("Time ativo")
                        }
                    }

                    // Membros
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Membros do Time",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (availableEmployees.isEmpty()) {
                        item {
                            Text(
                                text = "Nenhum colaborador disponível",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(availableEmployees) { employee ->
                            EmployeeCheckboxItem(
                                employee = employee,
                                isSelected = employee.id in selectedMemberIds,
                                onSelectionChange = { isSelected ->
                                    selectedMemberIds = if (isSelected) {
                                        selectedMemberIds + employee.id
                                    } else {
                                        selectedMemberIds - employee.id
                                    }
                                }
                            )
                        }
                    }

                    // Projetos
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Projetos Atribuídos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (availableProjects.isEmpty()) {
                        item {
                            Text(
                                text = "Nenhum projeto disponível",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(availableProjects) { project ->
                            ProjectCheckboxItem(
                                project = project,
                                isSelected = project.id in selectedProjectIds,
                                onSelectionChange = { isSelected ->
                                    selectedProjectIds = if (isSelected) {
                                        selectedProjectIds + project.id
                                    } else {
                                        selectedProjectIds - project.id
                                    }
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Botões de ação
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (isValid) {
                                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                                val newTeam = Team(
                                    id = team?.id ?: "", // ID vazio para novo time
                                    name = teamName,
                                    department = department,
                                    description = description,
                                    leaderId = leaderId,
                                    memberIds = selectedMemberIds.toList(),
                                    projectIds = selectedProjectIds.toList(),
                                    createdDate = team?.createdDate ?: today,
                                    isActive = isActive
                                )
                                onSave(newTeam)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        enabled = isValid
                    ) {
                        Text(if (team == null) "Criar Time" else "Salvar Alterações")
                    }
                }
            }
        }
    }
}

@Composable
private fun EmployeeCheckboxItem(
    employee: Employee,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChange
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = employee.fullName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = employee.role,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProjectCheckboxItem(
    project: Project,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChange
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = project.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
