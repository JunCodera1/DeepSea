package com.example.deepsea.data.model.game

import com.fasterxml.jackson.annotation.JsonProperty


data class GameStartRequest(
    @JsonProperty("userId") val userId: Long,
    @JsonProperty("gameMode") val gameMode: String,
    @JsonProperty("language") val language: String
)