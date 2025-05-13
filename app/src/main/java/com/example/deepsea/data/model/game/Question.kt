package com.example.deepsea.data.model.game

import com.fasterxml.jackson.annotation.JsonProperty

data class Question(
    @JsonProperty("id") val id: Long,
    @JsonProperty("text") val text: String,
    @JsonProperty("options") val options: List<String>, // Changed from JSON string to List<String>
    @JsonProperty("correctAnswer") val correctAnswer: Int,
    @JsonProperty("gameMode") val gameMode: String,
    @JsonProperty("language") val language: String
)