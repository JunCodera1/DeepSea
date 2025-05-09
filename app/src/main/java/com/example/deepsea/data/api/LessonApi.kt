package com.example.deepsea.data.api

import com.example.deepsea.data.model.exercise.LessonResult
import com.example.deepsea.data.model.question.QuizQuestion
import com.example.deepsea.ui.screens.feature.game.Question
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LessonApi {
    @POST("lessons/results")
    suspend fun saveLessonResult(@Body lessonResult: LessonResult): Long

    @GET("lessons/results/{id}")
    suspend fun getLessonResult(@Path("id") lessonId: Long): LessonResult

    @GET("questions/random")
    suspend fun getRandomQuestion(name: String?): QuizQuestion

    @GET("lessons/{lessonId}")
    suspend fun getLessonQuestions(
        @Path("lessonId") lessonId: Long,
        @Query("type") type: String? = null
    ): List<Map<String, QuizQuestion>>

}