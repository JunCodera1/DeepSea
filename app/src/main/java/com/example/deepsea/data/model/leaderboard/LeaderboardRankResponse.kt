package com.example.deepsea.data.model.leaderboard

data class LeaderboardRankResponse(
    val userId: Long,
    val username: String,
    val rank: Int,
    val totalXp: Int
)
