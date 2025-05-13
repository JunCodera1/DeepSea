package com.example.deepsea.data.model.game

import com.fasterxml.jackson.annotation.JsonProperty

data class GameAnswerRequest(
    @JsonProperty("matchId") val matchId: Long,
    @JsonProperty("questionId") val questionId: Long,
    @JsonProperty("userId") val userId: Long,
    @JsonProperty("selectedAnswer") val selectedAnswer: Int
)