package com.example.deepsea.data.model.mistake

data class MistakeRequest(
    val userId: Long,
    val word: String,
    val correctAnswer: String,
    val userAnswer: String,
    val lessonId: Long? = null
)