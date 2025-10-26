package com.project.taskmanagercivil.presentation.screens.employees

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.utils.FormatUtils
import kotlinx.datetime.*
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeFormModal(
    employee: Employee? = null,
    availableProjects: List<Project> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (Employee, List<String>) -> Unit
) {
    // Estados do formulário
    var fullName by remember { mutableStateOf(employee?.fullName ?: "") }
    var role by remember { mutableStateOf(employee?.role ?: "") }
    var email by remember { mutableStateOf(employee?.email ?: "") }
    var phone by remember { mutableStateOf(employee?.phone ?: "") }
    var cpf by remember { mutableStateOf(employee?.cpf ?: "") }
    var isActive by remember { mutableStateOf(employee?.isActive ?: true) }

    // Estados para pickers de data
    var showHireDatePicker by remember { mutableStateOf(false) }
    var showTerminationDatePicker by remember { mutableStateOf(false) }

    // Estados para datas
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    var hireDate by remember { mutableStateOf(employee?.hireDate ?: today) }
    var terminationDate by remember { mutableStateOf(employee?.terminationDate) }

    // Estados para projetos selecionados
    var selectedProjectIds by remember { mutableStateOf(employee?.projectIds?.toSet() ?: emptySet()) }

    // Validação
    val isValid = fullName.isNotBlank() &&
                  role.isNotBlank() &&
                  email.isNotBlank()

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
                        text = if (employee == null) "Novo Colaborador" else "Editar Colaborador",
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
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Nome Completo *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = role,
                            onValueChange = { role = it },
                            label = { Text("Cargo/Função *") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Ex: Engenheiro Civil") },
                            singleLine = true
                        )
                    }

                    // Informações de Contato
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Informações de Contato",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email *") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("exemplo@email.com") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Telefone") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("(11) 98765-4321") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = cpf,
                            onValueChange = { cpf = it },
                            label = { Text("CPF") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("000.000.000-00") },
                            singleLine = true
                        )
                    }

                    // Dados de Admissão
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Dados de Admissão",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        Column {
                            Text(
                                text = "Data de Admissão *",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showHireDatePicker = true },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = FormatUtils.formatDate(hireDate),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Column {
                            Text(
                                text = "Data de Demissão",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showTerminationDatePicker = true },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = terminationDate?.let { FormatUtils.formatDate(it) } ?: "Não definida",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null
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
                            Text("Colaborador ativo")
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
                                val newEmployee = Employee(
                                    id = employee?.id ?: Random.nextInt(1000, 9999).toString(),
                                    fullName = fullName,
                                    role = role,
                                    email = email,
                                    phone = phone.ifBlank { null },
                                    cpf = cpf.ifBlank { null },
                                    hireDate = hireDate,
                                    terminationDate = terminationDate,
                                    isActive = isActive,
                                    projectIds = selectedProjectIds.toList()
                                )
                                onSave(newEmployee, selectedProjectIds.toList())
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        enabled = isValid
                    ) {
                        Text(if (employee == null) "Criar Colaborador" else "Salvar Alterações")
                    }
                }
            }
        }
    }

    // Date Pickers
    if (showHireDatePicker) {
        SimpleDatePickerDialog(
            initialDate = hireDate,
            onDismiss = { showHireDatePicker = false },
            onDateSelected = {
                hireDate = it
                showHireDatePicker = false
            }
        )
    }

    if (showTerminationDatePicker) {
        SimpleDatePickerDialog(
            initialDate = terminationDate ?: today,
            onDismiss = { showTerminationDatePicker = false },
            onDateSelected = {
                terminationDate = it
                showTerminationDatePicker = false
            }
        )
    }
}

@Composable
private fun SimpleDatePickerDialog(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    var selectedYear by remember { mutableStateOf(initialDate.year) }
    var selectedMonth by remember { mutableStateOf(initialDate.monthNumber) }
    var selectedDay by remember { mutableStateOf(initialDate.dayOfMonth) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecionar Data") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = selectedYear.toString(),
                    onValueChange = {
                        it.toIntOrNull()?.let { year -> selectedYear = year }
                    },
                    label = { Text("Ano") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = selectedMonth.toString(),
                    onValueChange = {
                        it.toIntOrNull()?.let { month ->
                            if (month in 1..12) selectedMonth = month
                        }
                    },
                    label = { Text("Mês") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = selectedDay.toString(),
                    onValueChange = {
                        it.toIntOrNull()?.let { day ->
                            if (day in 1..31) selectedDay = day
                        }
                    },
                    label = { Text("Dia") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    try {
                        val date = LocalDate(selectedYear, selectedMonth, selectedDay)
                        onDateSelected(date)
                    } catch (e: Exception) {
                        // Data inválida, não faz nada
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
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
