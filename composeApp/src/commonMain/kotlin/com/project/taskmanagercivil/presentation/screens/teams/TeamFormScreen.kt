package com.project.taskmanagercivil.presentation.screens.teams

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.TeamDepartment
import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.domain.models.Project

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamFormScreen(
    viewModel: TeamFormViewModel,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditMode) "Editar Time"
                        else "Novo Time"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    
                    item {
                        Text(
                            text = "Informações Básicas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.name,
                            onValueChange = { viewModel.onNameChange(it) },
                            label = { Text("Nome do Time *") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = uiState.validationErrors.containsKey("name"),
                            supportingText = {
                                uiState.validationErrors["name"]?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            singleLine = true
                        )
                    }

                    item {
                        DepartmentDropdownField(
                            label = "Departamento *",
                            selectedDepartment = uiState.department,
                            onDepartmentChange = { viewModel.onDepartmentChange(it) },
                            isError = uiState.validationErrors.containsKey("department"),
                            errorMessage = uiState.validationErrors["department"]
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.description,
                            onValueChange = { viewModel.onDescriptionChange(it) },
                            label = { Text("Descrição") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )
                    }

                    
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Líder do Time",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        LeaderDropdownField(
                            label = "Líder/Responsável",
                            selectedLeaderId = uiState.leaderId,
                            availableEmployees = uiState.availableEmployees,
                            onLeaderChange = { viewModel.onLeaderChange(it) }
                        )
                    }

                    
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Membros",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (uiState.availableEmployees.isEmpty()) {
                        item {
                            Text(
                                text = "Nenhum colaborador disponível",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(uiState.availableEmployees) { employee ->
                            EmployeeCheckboxItem(
                                employee = employee,
                                isSelected = employee.id in uiState.selectedMemberIds,
                                onSelectionChange = { isSelected ->
                                    viewModel.onMemberSelectionChange(employee.id, isSelected)
                                }
                            )
                        }
                    }

                    
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Projetos Atribuídos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (uiState.availableProjects.isEmpty()) {
                        item {
                            Text(
                                text = "Nenhum projeto disponível",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(uiState.availableProjects) { project ->
                            ProjectCheckboxItem(
                                project = project,
                                isSelected = project.id in uiState.selectedProjectIds,
                                onSelectionChange = { isSelected ->
                                    viewModel.onProjectSelectionChange(project.id, isSelected)
                                }
                            )
                        }
                    }

                    
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Checkbox(
                                checked = uiState.isActive,
                                onCheckedChange = { viewModel.onIsActiveChange(it) }
                            )
                            Text("Time ativo")
                        }
                    }

                    
                    item {
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier.weight(1f),
                                enabled = !uiState.isSaving
                            ) {
                                Text("Cancelar")
                            }

                            Button(
                                onClick = { viewModel.saveTeam(onSaveSuccess) },
                                modifier = Modifier.weight(1f),
                                enabled = !uiState.isSaving
                            ) {
                                if (uiState.isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text("Salvar")
                                }
                            }
                        }
                    }

                    
                    if (uiState.errorMessage != null) {
                        item {
                            Text(
                                text = uiState.errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DepartmentDropdownField(
    label: String,
    selectedDepartment: TeamDepartment?,
    onDepartmentChange: (TeamDepartment) -> Unit,
    isError: Boolean,
    errorMessage: String?
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedDepartment?.displayName ?: "",
            onValueChange = { },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            isError = isError,
            supportingText = {
                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            },
            trailingIcon = {
                TextButton(onClick = { expanded = true }) {
                    Text("Selecionar")
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TeamDepartment.entries.forEach { department ->
                DropdownMenuItem(
                    text = { Text(department.displayName) },
                    onClick = {
                        onDepartmentChange(department)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun LeaderDropdownField(
    label: String,
    selectedLeaderId: String?,
    availableEmployees: List<Employee>,
    onLeaderChange: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedEmployee = availableEmployees.find { it.id == selectedLeaderId }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedEmployee?.let { "${it.fullName} - ${it.role}" } ?: "",
            onValueChange = { },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (selectedLeaderId != null) {
                        TextButton(onClick = { onLeaderChange(null) }) {
                            Text("Limpar")
                        }
                    }
                    TextButton(onClick = { expanded = true }) {
                        Text("Selecionar")
                    }
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableEmployees.forEach { employee ->
                DropdownMenuItem(
                    text = {
                        Column {
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
                    },
                    onClick = {
                        onLeaderChange(employee.id)
                        expanded = false
                    }
                )
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

            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = employee.fullName.split(" ")
                        .take(2)
                        .joinToString("") { it.first().uppercase() },
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

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
