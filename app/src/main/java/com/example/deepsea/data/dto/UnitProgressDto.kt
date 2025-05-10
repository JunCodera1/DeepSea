package com.example.deepsea.data.dto

data class UnitProgressDto(
    val unitId: Long,
    val userId: Long,
    val progress: Float,
    val starsCollected: Int,
    val totalStars: Int
)