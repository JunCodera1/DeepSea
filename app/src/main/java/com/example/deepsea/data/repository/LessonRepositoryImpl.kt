package com.example.deepsea.data.repository

import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.exercise.LessonResult
import retrofit2.Response


class LessonRepositoryImpl : LessonRepository {

    private val lessonApi = RetrofitClient.lessonApi
    override suspend fun saveLessonResult(lessonResult: LessonResult): Response<Unit>{
        return try {
            val result = lessonApi.saveLessonResult(lessonResult)
            println("Saved LessonResult: $lessonResult, ID: $result")
            result
        } catch (e: Exception) {
            println("Failed to save LessonResult: ${e.message}")
            throw e
        }
    }

    override suspend fun getLessonResult(lessonId: Long): LessonResult? {
        return lessonApi.getLessonResult(lessonId)
    }
}