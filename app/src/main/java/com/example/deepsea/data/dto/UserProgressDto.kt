package com.example.deepsea.data.dto

data class UserProgressDto(
    val userId: Long,
    val currentSectionId: Long,
    val currentUnitId: Long,
    val totalXp: Int,
    val dailyStreak: Int,
    val completedUnits: List<Long>
)