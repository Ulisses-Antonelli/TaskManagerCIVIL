package com.project.taskmanagercivil.presentation.screens.employees

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.utils.FormatUtils
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeFormScreen(
    viewModel: EmployeeFormViewModel,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditMode) "Editar Colaborador"
                        else "Novo Colaborador"
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
                    // Informações Básicas
                    item {
                        Text(
                            text = "Informações Básicas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.fullName,
                            onValueChange = { viewModel.onFullNameChange(it) },
                            label = { Text("Nome Completo *") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = uiState.validationErrors.containsKey("fullName"),
                            supportingText = {
                                uiState.validationErrors["fullName"]?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.role,
                            onValueChange = { viewModel.onRoleChange(it) },
                            label = { Text("Cargo/Função *") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = uiState.validationErrors.containsKey("role"),
                            supportingText = {
                                uiState.validationErrors["role"]?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            singleLine = true
                        )
                    }

                    
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Informações de Contato",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = { viewModel.onEmailChange(it) },
                            label = { Text("Email *") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = uiState.validationErrors.containsKey("email"),
                            supportingText = {
                                uiState.validationErrors["email"]?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.phone,
                            onValueChange = { viewModel.onPhoneChange(it) },
                            label = { Text("Telefone") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.cpf,
                            onValueChange = { viewModel.onCpfChange(it) },
                            label = { Text("CPF") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    // Dados de Admissão
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Dados de Admissão",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        DatePickerField(
                            label = "Data de Admissão *",
                            date = uiState.hireDate,
                            onDateChange = { viewModel.onHireDateChange(it) },
                            isError = uiState.validationErrors.containsKey("hireDate"),
                            errorMessage = uiState.validationErrors["hireDate"]
                        )
                    }

                    item {
                        DatePickerField(
                            label = "Data de Demissão",
                            date = uiState.terminationDate,
                            onDateChange = { viewModel.onTerminationDateChange(it) },
                            isError = false,
                            errorMessage = null
                        )
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Checkbox(
                                checked = uiState.isActive,
                                onCheckedChange = { viewModel.onIsActiveChange(it) }
                            )
                            Text("Colaborador ativo")
                        }
                    }

                    // Projetos
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

                    // Botões de ação
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
                                onClick = { viewModel.saveEmployee(onSaveSuccess) },
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

                    // Mensagem de erro
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(
    label: String,
    date: LocalDate?,
    onDateChange: (LocalDate?) -> Unit,
    isError: Boolean,
    errorMessage: String?
) {
    var showDatePicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = date?.let { FormatUtils.formatDate(it) } ?: "",
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
            TextButton(onClick = { showDatePicker = true }) {
                Text("Selecionar")
            }
        }
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date?.let {
                // Converter LocalDate para milissegundos
                val instant = kotlinx.datetime.Instant.parse("${it}T00:00:00Z")
                instant.toEpochMilliseconds()
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            // Converter milissegundos para LocalDate
                            val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
                            val localDate = instant.toString().substringBefore('T')
                            val parts = localDate.split('-')
                            onDateChange(
                                LocalDate(
                                    parts[0].toInt(),
                                    parts[1].toInt(),
                                    parts[2].toInt()
                                )
                            )
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
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
