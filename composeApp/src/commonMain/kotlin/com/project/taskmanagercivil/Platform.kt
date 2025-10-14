package com.project.taskmanagercivil

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform