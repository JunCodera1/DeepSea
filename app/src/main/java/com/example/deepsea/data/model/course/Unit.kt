package com.example.deepsea.data.model.course

data class Unit(
    val id: Long,
    val title: String,
    val description: String,
    val image: Int,
    val sectionId: Long,
    val colorHex: String,
    val colorDarkerHex: String,
    val progress: Float = 0f
)