package com.example.deepsea.data.repository

import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.exercise.LessonResult


class LessonRepositoryImpl : LessonRepository {

    private val lessonApi = RetrofitClient.lessonApi
    override suspend fun saveLessonResult(lessonResult: LessonResult): Long {
        return lessonApi.saveLessonResult(lessonResult)
    }

    override suspend fun getLessonResult(lessonId: Long): LessonResult? {
        return lessonApi.getLessonResult(lessonId)
    }
}