package com.project.taskmanagercivil.presentation.screens.tasks

import androidx.compose.foundation.layout.*
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

/**
 * Dialog para confirmar entrega de tarefa
 * Exibido quando tarefa é marcada como CONCLUÍDA
 */
@Composable
fun DeliveryConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: (description: String) -> Unit
) {
    var description by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .heightIn(min = 300.dp, max = 500.dp),
            shape = RoundedCornerShape(8.dp)
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
                        text = "Confirmar Entrega",
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

                // Conteúdo
                Text(
                    text = "Descreva o que foi entregue nesta conclusão:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição da Entrega *") },
                    placeholder = { Text("Ex: Projeto estrutural completo conforme especificação inicial") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    minLines = 4,
                    maxLines = 8
                )

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
                            if (description.isNotBlank()) {
                                onConfirm(description.trim())
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        enabled = description.isNotBlank()
                    ) {
                        Text("Confirmar Entrega")
                    }
                }
            }
        }
    }
}

/**
 * Dialog para solicitar motivo de revisão
 * Exibido quando tarefa CONCLUÍDA é marcada como EM REVISÃO
 */
@Composable
fun RevisionReasonDialog(
    onDismiss: () -> Unit,
    onConfirm: (description: String) -> Unit
) {
    var description by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .heightIn(min = 300.dp, max = 500.dp),
            shape = RoundedCornerShape(8.dp)
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
                        text = "Motivo da Revisão",
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

                // Conteúdo
                Text(
                    text = "Descreva as alterações necessárias:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição da Revisão *") },
                    placeholder = { Text("Ex: Ajustar cálculo de viga V3 conforme norma ABNT NBR 6118") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    minLines = 4,
                    maxLines = 8
                )

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
                            if (description.isNotBlank()) {
                                onConfirm(description.trim())
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        enabled = description.isNotBlank()
                    ) {
                        Text("Iniciar Revisão")
                    }
                }
            }
        }
    }
}
