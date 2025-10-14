package com.project.taskmanagercivil.domain.models

import kotlinx.datetime.LocalDate

data class Project(
    val id: String,
    val name: String,
    val description: String,
    val client: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val budget: Double,
    val location: String
)
