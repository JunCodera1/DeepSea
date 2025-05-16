package com.example.deepsea.data.dto

import java.time.LocalDate

data class UserProgressDto(
    val userId: Long,
    val currentSectionId: Long,
    val lastLogin: LocalDate,
    val currentUnitId: Long,
    val totalXp: Int,
    val dailyStreak: Int,
    val completedUnits: List<Long>
)