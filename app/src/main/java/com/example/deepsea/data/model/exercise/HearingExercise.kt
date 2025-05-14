package com.example.deepsea.data.model.exercise

data class HearingExercise(
    val id: String,
    val correctAnswer: String,
    val options: List<String>
)