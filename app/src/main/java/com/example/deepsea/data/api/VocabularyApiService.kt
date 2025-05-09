package com.example.deepsea.data.api

import com.example.deepsea.data.model.leaderboard.LeaderboardEntry
import com.example.deepsea.data.model.question.QuizQuestion
import com.example.deepsea.ui.viewmodel.learn.VocabularyItem
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface VocabularyApiService {
    @GET("api/lessons/{lessonId}/questions")
    suspend fun getLessonQuestions(
        @Path("lessonId") lessonId: Long,
        @Query("type") type: String? = null
    ): List<QuizQuestion>

    @GET("api/vocabulary/{wordId}")
    suspend fun getVocabularyItem(@Path("wordId") wordId: Long): QuizQuestion

    @GET("api/vocabulary/random")
    suspend fun getRandomVocabularyItem(): QuizQuestion

    @GET("api/vocabulary/options")
    suspend fun getVocabularyOptions(@Query("size") size: Int = 4): List<QuizQuestion>

    @GET("api/leaderboard/top")
    suspend fun getTopLeaderboard(): List<LeaderboardEntry>

}