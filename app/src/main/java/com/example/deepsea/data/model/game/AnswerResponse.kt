package com.example.deepsea.data.model.game

import com.fasterxml.jackson.annotation.JsonProperty

data class AnswerResponse(
    @JsonProperty("isCorrect") val isCorrect: Boolean,
    @JsonProperty("matchCompleted") val matchCompleted: Boolean,
    @JsonProperty("xpEarned") val xpEarned: Int
)