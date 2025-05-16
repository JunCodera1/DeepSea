package com.example.deepsea.data.model.daily

import java.time.LocalDate

data class UpdateProgressRequest(
    val dailyStreak: Int,
    val lastLogin: LocalDate
)