package com.example.deepsea.data.model.game

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class Match(
    @JsonProperty("id") val id: Long,
    @JsonProperty("player1Id") val player1Id: Long,
    @JsonProperty("player2Id") val player2Id: Long?,
    @JsonProperty("gameMode") val gameMode: String,
    @JsonProperty("status") val status: String,
    @JsonProperty("createdAt") val createdAt: String,
    @JsonProperty("completedAt") val completedAt: String?
)