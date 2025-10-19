package com.project.taskmanagercivil.domain.repository

import com.project.taskmanagercivil.domain.models.DashboardData

interface DashboardRepository {
    suspend fun getDashboardData(): DashboardData
}
