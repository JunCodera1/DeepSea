package com.example.deepsea.data.model.user

data class UpdateProgressRequest(
    val dailyStreak: Int,
    val lastLogin: String
)