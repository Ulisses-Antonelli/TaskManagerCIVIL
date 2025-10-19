package com.project.taskmanagercivil.utils

import kotlinx.datetime.LocalDate
import kotlin.math.roundToInt

/**
 * Utilitários para formatação multiplataforma
 */
object FormatUtils {
    fun formatCurrency(value: Double): String {
        val millions = value / 1_000_000
        return when {
            millions >= 1 -> {
                val formatted = (millions * 100).roundToInt() / 100.0
                "R$ $formatted M"
            }
            else -> {
                val thousands = (value / 1000).roundToInt()
                "R$ $thousands mil"
            }
        }
    }

    fun formatDate(date: LocalDate): String {
        val day = date.dayOfMonth.toString().padStart(2, '0')
        val month = date.monthNumber.toString().padStart(2, '0')
        val year = date.year
        return "$day/$month/$year"
    }
}

// Funções de extensão para manter compatibilidade com código existente
fun formatCurrency(value: Double): String = FormatUtils.formatCurrency(value)
fun formatDate(date: LocalDate): String = FormatUtils.formatDate(date)
