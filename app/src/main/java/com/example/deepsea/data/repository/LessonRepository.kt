package com.example.deepsea.data.repository

import com.example.deepsea.data.model.exercise.LessonResult
import retrofit2.Response

interface LessonRepository {
    suspend fun saveLessonResult(lessonResult: LessonResult): Response<Unit>
    suspend fun getLessonResult(lessonId: Long): LessonResult?
}