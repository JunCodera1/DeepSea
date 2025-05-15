package com.example.deepsea.data.dto

data class LessonProgressDto(
    val lessonId: Long, // ID of the completed lesson
    val completed: Boolean, // Whether the lesson is marked as completed
    val starsEarned: Int, // Number of stars earned (e.g., 1-3)
    val xpEarned: Int, // XP earned for the lesson
    val nextLessonId: Long?, // ID of the next lesson (nullable if none)
    val timeTaken: String? // Time taken to complete the lesson in "mm:ss" format
)