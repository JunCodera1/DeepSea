package com.example.deepsea.data.repository

import com.example.deepsea.data.model.exercise.LessonResult

interface LessonRepository {
    suspend fun saveLessonResult(lessonResult: LessonResult): Long
    suspend fun getLessonResult(lessonId: Long): LessonResult?
}