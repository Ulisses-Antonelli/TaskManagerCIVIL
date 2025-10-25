package com.project.taskmanagercivil.presentation.screens.projects

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.project.taskmanagercivil.domain.models.Project
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectFormModal(
    project: Project? = null, // null = criar novo projeto, non-null = editar
    onDismiss: () -> Unit,
    onSave: (Project) -> Unit
) {
    // Estados do formulário
    var projectName by remember { mutableStateOf(project?.name ?: "") }
    var description by remember { mutableStateOf(project?.description ?: "") }
    var client by remember { mutableStateOf(project?.client ?: "") }
    var location by remember { mutableStateOf(project?.location ?: "") }
    var budget by remember { mutableStateOf(project?.budget?.toString() ?: "") }

    // Estados para pickers de data
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // Estados para datas
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    var startDate by remember { mutableStateOf(project?.startDate ?: today) }
    var endDate by remember { mutableStateOf(project?.endDate ?: today.let { LocalDate(it.year, it.monthNumber, it.dayOfMonth + 30) }) }

    // Validação
    val isValid = projectName.isNotBlank() &&
                  client.isNotBlank() &&
                  location.isNotBlank() &&
                  budget.isNotBlank()

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
                        text = if (project == null) "Nova Obra" else "Editar Obra",
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
                    // Nome do Projeto
                    item {
                        Column {
                            Text(
                                text = "Nome da Obra *",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = projectName,
                                onValueChange = { projectName = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Ex: Edifício Residencial Centro") },
                                singleLine = true
                            )
                        }
                    }

                    // Descrição
                    item {
                        Column {
                            Text(
                                text = "Descrição",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                placeholder = { Text("Descreva os detalhes da obra...") },
                                maxLines = 5
                            )
                        }
                    }

                    // Cliente
                    item {
                        Column {
                            Text(
                                text = "Cliente *",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = client,
                                onValueChange = { client = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Nome do cliente") },
                                singleLine = true
                            )
                        }
                    }

                    // Localização
                    item {
                        Column {
                            Text(
                                text = "Localização *",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = location,
                                onValueChange = { location = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Ex: São Paulo, SP") },
                                singleLine = true
                            )
                        }
                    }

                    // Orçamento
                    item {
                        Column {
                            Text(
                                text = "Orçamento (R$) *",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = budget,
                                onValueChange = {
                                    // Permite apenas números e ponto decimal
                                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                        budget = it
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Ex: 500000.00") },
                                singleLine = true,
                                leadingIcon = {
                                    Text("R$", style = MaterialTheme.typography.bodyLarge)
                                }
                            )
                        }
                    }

                    // Data de Início
                    item {
                        Column {
                            Text(
                                text = "Data de Início",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showStartDatePicker = true },
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
                                        text = "${startDate.dayOfMonth}/${startDate.monthNumber}/${startDate.year}",
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

                    // Data de Término
                    item {
                        Column {
                            Text(
                                text = "Prazo de Entrega",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showEndDatePicker = true },
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
                                        text = "${endDate.dayOfMonth}/${endDate.monthNumber}/${endDate.year}",
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
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Botões de ação
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (isValid) {
                                val newProject = Project(
                                    id = project?.id ?: Random.nextInt(1000, 9999).toString(),
                                    name = projectName,
                                    description = description,
                                    client = client,
                                    location = location,
                                    budget = budget.toDoubleOrNull() ?: 0.0,
                                    startDate = startDate,
                                    endDate = endDate
                                )
                                onSave(newProject)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = isValid
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (project == null) "Criar Obra" else "Salvar Alterações")
                    }
                }
            }
        }
    }

    // Date Pickers (simplificados - em produção usar DatePicker real)
    if (showStartDatePicker) {
        SimpleDatePickerDialog(
            initialDate = startDate,
            onDismiss = { showStartDatePicker = false },
            onDateSelected = {
                startDate = it
                showStartDatePicker = false
            }
        )
    }

    if (showEndDatePicker) {
        SimpleDatePickerDialog(
            initialDate = endDate,
            onDismiss = { showEndDatePicker = false },
            onDateSelected = {
                endDate = it
                showEndDatePicker = false
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
                // Ano
                OutlinedTextField(
                    value = selectedYear.toString(),
                    onValueChange = {
                        it.toIntOrNull()?.let { year -> selectedYear = year }
                    },
                    label = { Text("Ano") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Mês
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

                // Dia
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
