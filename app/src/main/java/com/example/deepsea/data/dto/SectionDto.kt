package com.example.deepsea.data.dto

data class SectionDto(
    val id: Long,
    val title: String,
    val description: String,
    val level: String?,
    val color: String,
    val darkerColor: String,
    val imageResId: Int?           // Keep as nullable
)