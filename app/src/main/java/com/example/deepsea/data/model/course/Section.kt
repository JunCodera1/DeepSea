package com.example.deepsea.data.model.course

data class Section(
    val id: Long,
    val title: String,
    val description: String,
    val level: String,
    val image: Int,
    val colorHex: String,
    val colorDarkerHex: String
)