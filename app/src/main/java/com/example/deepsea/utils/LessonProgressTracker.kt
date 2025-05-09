package com.example.deepsea.utils

class LessonProgressTracker {
    private var completedLessons = mutableSetOf<String>()
    private val totalLessons = 4 // Total number of different lesson types

    fun markLessonCompleted(lessonType: String) {
        completedLessons.add(lessonType)
    }

    fun areAllLessonsCompleted(): Boolean {
        return completedLessons.size >= totalLessons
    }

    fun reset() {
        completedLessons.clear()
    }
}