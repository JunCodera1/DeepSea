package com.example.deepsea.data.model.review

data class MistakeRequest(
    val userId: Long,
    val word: String,
    val correctAnswer: String,
    val userAnswer: String,
    val lessonId: Long? = null
)