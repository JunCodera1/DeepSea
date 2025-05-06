package com.example.deepsea.data.model.audio

data class HearingExercise(
    val id: String,
    val audio: String,
    val correctAnswer: String,
    val options: List<String>
)