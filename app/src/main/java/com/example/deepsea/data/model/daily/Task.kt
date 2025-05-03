package com.example.deepsea.data.model.daily

data class Task(
    val name: String,
    val progress: Int,
    val target: Int,
    val category: TaskCategory
)