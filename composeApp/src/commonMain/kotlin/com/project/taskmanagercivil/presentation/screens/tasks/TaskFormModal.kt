package com.project.taskmanagercivil.presentation.screens.tasks

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.domain.models.TaskPriority
import com.project.taskmanagercivil.domain.models.TaskRevision
import com.project.taskmanagercivil.domain.models.TaskStatus
import com.project.taskmanagercivil.domain.models.User
import kotlinx.datetime.LocalDate
import kotlin.random.Random

// Data class para itens do checklist
data class ChecklistItemData(
    val id: String = Random.nextInt().toString(),
    val text: String,
    val isCompleted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormModal(
    task: Task? = null, // null = criar nova tarefa, non-null = editar
    initialStatus: TaskStatus? = null, // Status inicial quando criar
    projects: List<Project>,
    users: List<User>,
    onDismiss: () -> Unit,
    onSave: (Task, List<ChecklistItemData>) -> Unit
) {
    // Estados do formulário
    var selectedProject by remember { mutableStateOf(task?.project ?: projects.firstOrNull()) }
    var taskName by remember { mutableStateOf(task?.title ?: "") }
    var selectedUser by remember { mutableStateOf(task?.assignedTo ?: users.firstOrNull()) }
    var selectedStatus by remember { mutableStateOf(task?.status ?: initialStatus ?: TaskStatus.TODO) }
    var selectedPriority by remember { mutableStateOf(task?.priority ?: TaskPriority.MEDIUM) }
    var description by remember { mutableStateOf(task?.description ?: "") }

    // Estados para pickers
    var showProjectPicker by remember { mutableStateOf(false) }
    var showUserPicker by remember { mutableStateOf(false) }
    var showStatusPicker by remember { mutableStateOf(false) }
    var showPriorityPicker by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showDueDatePicker by remember { mutableStateOf(false) }

    // Estados para datas (mockado - futuramente usar DatePicker)
    var startDate by remember { mutableStateOf(task?.startDate ?: LocalDate(2024, 1, 15)) }
    var dueDate by remember { mutableStateOf(task?.dueDate ?: LocalDate(2024, 2, 15)) }

    // Checklist items
    val checklistItems = remember {
        mutableStateListOf<ChecklistItemData>().apply {
            // Se editando, carregar checklist existente (mockado por enquanto)
            if (task != null) {
                add(ChecklistItemData(text = "Item de checklist 1", isCompleted = true))
                add(ChecklistItemData(text = "Item de checklist 2", isCompleted = false))
            }
        }
    }
    var newChecklistItem by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.95f),
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
                        text = if (task == null) "Nova Tarefa" else "Editar Tarefa",
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
                    // Obra
                    item {
                        Column {
                            Text(
                                text = "Obra *",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showProjectPicker = true },
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
                                        text = selectedProject?.name ?: "Selecione uma obra",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }

                    // Nome da Tarefa
                    item {
                        Column {
                            Text(
                                text = "Nome da Tarefa *",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = taskName,
                                onValueChange = { taskName = it },
                                placeholder = { Text("Digite o nome da tarefa") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }

                    // Colaborador
                    item {
                        Column {
                            Text(
                                text = "Colaborador *",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showUserPicker = true },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = selectedUser?.name ?: "Selecione um colaborador",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        if (selectedUser != null) {
                                            Text(
                                                text = selectedUser!!.role,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }

                    // Endereço (somente leitura, vem da obra)
                    item {
                        Column {
                            Text(
                                text = "Endereço",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = selectedProject?.location ?: "Selecione uma obra primeiro",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Status
                    item {
                        Column {
                            Text(
                                text = "Status",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showStatusPicker = true },
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
                                        text = selectedStatus.label,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }

                    // Prioridade
                    item {
                        Column {
                            Text(
                                text = "Prioridade",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showPriorityPicker = true },
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
                                        text = selectedPriority.label,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }

                    // Datas (mockado - futuramente usar DatePicker real)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
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
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${startDate.dayOfMonth.toString().padStart(2, '0')}/${startDate.monthNumber.toString().padStart(2, '0')}/${startDate.year}",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Previsão de Término",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showDueDatePicker = true },
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${dueDate.dayOfMonth.toString().padStart(2, '0')}/${dueDate.monthNumber.toString().padStart(2, '0')}/${dueDate.year}",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Descrição
                    item {
                        Column {
                            Text(
                                text = "Descrição da Tarefa",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                placeholder = { Text("Descreva os detalhes da tarefa...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                maxLines = 5
                            )
                        }
                    }

                    // Checklist
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Checklist",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${checklistItems.count { it.isCompleted }}/${checklistItems.size} concluídos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Campo para adicionar novo item
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newChecklistItem,
                                    onValueChange = { newChecklistItem = it },
                                    placeholder = { Text("Novo item do checklist") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                IconButton(
                                    onClick = {
                                        if (newChecklistItem.isNotBlank()) {
                                            checklistItems.add(ChecklistItemData(text = newChecklistItem))
                                            newChecklistItem = ""
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Adicionar item",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Lista de itens do checklist
                            checklistItems.forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Checkbox(
                                        checked = item.isCompleted,
                                        onCheckedChange = { checked ->
                                            checklistItems[index] = item.copy(isCompleted = checked)
                                        }
                                    )
                                    Text(
                                        text = item.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { checklistItems.removeAt(index) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remover item",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botões
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
                            if (selectedProject != null && selectedUser != null && taskName.isNotBlank()) {
                                // Se for nova tarefa, cria revisão inicial (Rev. 00)
                                val revisions = if (task == null) {
                                    listOf(
                                        TaskRevision(
                                            revisionNumber = 0,
                                            author = selectedUser!!.name,
                                            description = "Emissão inicial",
                                            startDate = startDate,
                                            deliveryDate = dueDate
                                        )
                                    )
                                } else {
                                    task.revisions // Mantém revisões existentes ao editar
                                }

                                val newTask = Task(
                                    id = task?.id ?: "", // String vazia para novas tarefas, ViewModel gera o ID
                                    title = taskName,
                                    description = description,
                                    status = selectedStatus,
                                    priority = selectedPriority,
                                    assignedTo = selectedUser!!,
                                    project = selectedProject!!,
                                    startDate = startDate,
                                    dueDate = dueDate,
                                    progress = if (checklistItems.isEmpty()) 0f else {
                                        (checklistItems.count { it.isCompleted }.toFloat() / checklistItems.size) * 100f
                                    },
                                    tags = emptyList(),
                                    dependencies = emptyList(),
                                    revisions = revisions
                                )
                                onSave(newTask, checklistItems.toList())
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        enabled = selectedProject != null && selectedUser != null && taskName.isNotBlank()
                    ) {
                        Text(if (task == null) "Criar Tarefa" else "Salvar")
                    }
                }
            }
        }
    }

    // Dialogs de seleção
    if (showProjectPicker) {
        SelectionDialog(
            title = "Selecionar Obra",
            items = projects,
            itemText = { it.name },
            onItemClick = {
                selectedProject = it
                showProjectPicker = false
            },
            onDismiss = { showProjectPicker = false }
        )
    }

    if (showUserPicker) {
        SelectionDialog(
            title = "Selecionar Colaborador",
            items = users,
            itemText = { "${it.name} - ${it.role}" },
            onItemClick = {
                selectedUser = it
                showUserPicker = false
            },
            onDismiss = { showUserPicker = false }
        )
    }

    if (showStatusPicker) {
        SelectionDialog(
            title = "Selecionar Status",
            items = TaskStatus.entries.toList(),
            itemText = { it.label },
            onItemClick = {
                selectedStatus = it
                showStatusPicker = false
            },
            onDismiss = { showStatusPicker = false }
        )
    }

    if (showPriorityPicker) {
        SelectionDialog(
            title = "Selecionar Prioridade",
            items = TaskPriority.entries.toList(),
            itemText = { it.label },
            onItemClick = {
                selectedPriority = it
                showPriorityPicker = false
            },
            onDismiss = { showPriorityPicker = false }
        )
    }

    // Date Pickers
    if (showStartDatePicker) {
        DatePickerDialog(
            title = "Data de Início",
            initialDate = startDate,
            onDateSelected = {
                startDate = it
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showDueDatePicker) {
        DatePickerDialog(
            title = "Previsão de Término",
            initialDate = dueDate,
            onDateSelected = {
                dueDate = it
                showDueDatePicker = false
            },
            onDismiss = { showDueDatePicker = false }
        )
    }
}

@Composable
private fun <T> SelectionDialog(
    title: String,
    items: List<T>,
    itemText: (T) -> String,
    onItemClick: (T) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(items.size) { index ->
                    val item = items[index]
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onItemClick(item) },
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = itemText(item),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun DatePickerDialog(
    title: String,
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var day by remember { mutableStateOf(initialDate.dayOfMonth.toString()) }
    var month by remember { mutableStateOf(initialDate.monthNumber.toString()) }
    var year by remember { mutableStateOf(initialDate.year.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(8.dp),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = day,
                        onValueChange = { if (it.length <= 2 && (it.toIntOrNull() != null || it.isEmpty())) day = it },
                        label = { Text("Dia") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = month,
                        onValueChange = { if (it.length <= 2 && (it.toIntOrNull() != null || it.isEmpty())) month = it },
                        label = { Text("Mês") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = year,
                        onValueChange = { if (it.length <= 4 && (it.toIntOrNull() != null || it.isEmpty())) year = it },
                        label = { Text("Ano") },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true
                    )
                }
                Text(
                    text = "Formato: DD / MM / AAAA",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    try {
                        val dayInt = day.toIntOrNull() ?: initialDate.dayOfMonth
                        val monthInt = month.toIntOrNull() ?: initialDate.monthNumber
                        val yearInt = year.toIntOrNull() ?: initialDate.year

                        // Validação básica
                        if (dayInt in 1..31 && monthInt in 1..12 && yearInt in 2000..2100) {
                            val newDate = LocalDate(yearInt, monthInt, dayInt)
                            onDateSelected(newDate)
                        } else {
                            onDismiss()
                        }
                    } catch (e: Exception) {
                        onDismiss()
                    }
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
