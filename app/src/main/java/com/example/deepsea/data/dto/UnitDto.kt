package com.example.deepsea.data.dto

data class UnitDto(
    val id: Long,
    val sectionId: Long,
    val title: String,
    val description: String,
    val color: String,
    val darkerColor: String,
    val image: Int?,               // This is the correct field name
    val orderIndex: Int,
    val starsRequired: Int,
    val lessonCount: Int
)
