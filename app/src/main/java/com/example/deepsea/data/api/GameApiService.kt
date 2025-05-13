package com.example.deepsea.data.api

import com.example.deepsea.data.model.game.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// GameApiService.kt
interface GameApiService {
    @POST("/api/game/start")
    suspend fun startMatch(@Body request: GameStartRequest): Response<Match>

    @POST("/api/game/answer")
    suspend fun submitAnswer(@Body request: GameAnswerRequest): Response<AnswerResponse>

    @GET("/api/game/questions/{matchId}")
    suspend fun getMatchQuestions(@Path("matchId") matchId: Long): Response<List<Question>>
}