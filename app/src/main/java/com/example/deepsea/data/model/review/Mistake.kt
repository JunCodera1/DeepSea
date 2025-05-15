package com.example.deepsea.data.model.review

data class Mistake(
    val id: Long,
    val word: String,
    val correctAnswer: String,
    val userAnswer: String,
    val createdAt: String,
    val reviewCount: Int,
    val lessonId: Long? = null,
    val reviewedAt: String? = null
)