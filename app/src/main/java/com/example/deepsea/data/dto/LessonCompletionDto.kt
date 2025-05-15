package com.example.deepsea.data.dto

data class LessonCompletionDto(
    val score: Int, // Accuracy or performance score (e.g., 0-100)
    val timeTaken: String // Time taken to complete the lesson in "mm:ss" format
)