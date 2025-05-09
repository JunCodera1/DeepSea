package com.example.deepsea.data.model.leaderboard

data class LeaderboardEntry(
    val username: String,
    val name: String,
    val totalXp: Int,
    val currentLeague: String,
    val topFinishes: Int,
    val dayStreak: Int
)
