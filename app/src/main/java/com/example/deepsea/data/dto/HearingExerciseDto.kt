package com.example.deepsea.data.dto

data class HearingExerciseDto(
    val id: String,
    val correctAnswer: String,
    val options: List<String>
)