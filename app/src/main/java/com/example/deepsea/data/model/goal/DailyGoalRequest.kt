package com.example.deepsea.data.model.goal

data class DailyGoalRequest(
    val userId: Long,
    val goal: DailyGoalOption
)
