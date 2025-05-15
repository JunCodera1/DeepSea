package com.example.deepsea.data.api

import com.example.deepsea.data.dto.LessonCompletionDto
import com.example.deepsea.data.dto.LessonProgressDto
import com.example.deepsea.data.model.exercise.LessonResult
import com.example.deepsea.data.model.question.QuizQuestion
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LessonApi {
    @POST("/api/v2/lessons/results")
    suspend fun saveLessonResult(@Body lessonResult: LessonResult): Response<Unit>

    @GET("api/v2/lessons/results/{id}")
    suspend fun getLessonResult(@Path("id") lessonId: Long): LessonResult

    @GET("api/v2/questions/random")
    suspend fun getRandomQuestion(name: String?): QuizQuestion

    @GET("api/v2/lessons/{lessonId}")
    suspend fun getLessonQuestions(
        @Path("lessonId") lessonId: Long,
        @Query("type") type: String? = null
    ): List<Map<String, QuizQuestion>>

    @POST("api/v2/lessons/{id}/complete")
    suspend fun completeLesson(
        @Path("id") lessonId: Long,
        @Query("userId") userId: Long,
        @Body completionData: LessonCompletionDto
    ): LessonProgressDto
}