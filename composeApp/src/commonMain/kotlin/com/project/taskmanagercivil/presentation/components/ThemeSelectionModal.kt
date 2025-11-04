package com.project.taskmanagercivil.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.project.taskmanagercivil.domain.models.AppTheme

/**
 * Modal para seleção de tema da aplicação
 */
@Composable
fun ThemeSelectionModal(
    currentTheme: AppTheme,
    onDismiss: () -> Unit,
    onThemeSelected: (AppTheme) -> Unit
) {
    var selectedTheme by remember { mutableStateOf(currentTheme) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .width(600.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Configurações de Tema",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Divider()

                // Descrição
                Text(
                    text = "Escolha uma paleta de cores para personalizar a aparência da aplicação:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Lista de temas
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tema Padrão
                    ThemeOption(
                        theme = AppTheme.DEFAULT,
                        isSelected = selectedTheme == AppTheme.DEFAULT,
                        colors = listOf(
                            Color(0xFF1976D2),  // primary
                            Color(0xFF388E3C),  // secondary
                            Color(0xFFF57C00),  // tertiary
                            Color(0xFFBBDEFB),  // primaryContainer
                            Color(0xFFC8E6C9)   // secondaryContainer
                        ),
                        onClick = { selectedTheme = AppTheme.DEFAULT }
                    )

                    // Paleta 1 - Verde-Azul Suave
                    ThemeOption(
                        theme = AppTheme.PALETTE_1,
                        isSelected = selectedTheme == AppTheme.PALETTE_1,
                        colors = listOf(
                            Color(0xFFb9d8c2),  // Verde claro
                            Color(0xFF6B9CA8),  // Azul saturado
                            Color(0xFF7B8FA0),  // Azul acinzentado
                            Color(0xFF4a5043),  // Cinza escuro
                            Color(0xFFE5B940)   // Amarelo saturado
                        ),
                        onClick = { selectedTheme = AppTheme.PALETTE_1 }
                    )

                    // Paleta Azul Marinho e Laranja
                    ThemeOption(
                        theme = AppTheme.NAVY_ORANGE,
                        isSelected = selectedTheme == AppTheme.NAVY_ORANGE,
                        colors = listOf(
                            Color(0xFF14213d),  // Azul marinho
                            Color(0xFFfca311),  // Laranja vibrante
                            Color(0xFFe5e5e5),  // Cinza claro
                            Color(0xFFffffff),  // Branco
                            Color(0xFF2A4A6F)   // Azul médio
                        ),
                        onClick = { selectedTheme = AppTheme.NAVY_ORANGE }
                    )

                    // Tema Escuro
                    ThemeOption(
                        theme = AppTheme.DARK,
                        isSelected = selectedTheme == AppTheme.DARK,
                        colors = listOf(
                            Color(0xFF7DB8CC),  // Azul claro vibrante
                            Color(0xFF88C9A8),  // Verde claro vibrante
                            Color(0xFFFFB874),  // Laranja suave
                            Color(0xFF1A1C1E),  // Background escuro
                            Color(0xFF2D2F31)   // Surface escuro
                        ),
                        onClick = { selectedTheme = AppTheme.DARK }
                    )
                }

                Divider()

                // Botões de ação
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onThemeSelected(selectedTheme)
                            onDismiss()
                        }
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Aplicar Tema")
                    }
                }
            }
        }
    }
}

/**
 * Componente que representa uma opção de tema com preview das cores
 */
@Composable
private fun ThemeOption(
    theme: AppTheme,
    isSelected: Boolean,
    colors: List<Color>,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }

    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nome do tema
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = theme.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (isSelected) {
                Text(
                    text = "Tema atual",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Preview das cores
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color, RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                )
            }

            // Ícone de seleção
            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Selecionado",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
