package com.example.deepsea.data.api

import com.example.deepsea.data.model.mistake.Mistake
import com.example.deepsea.data.model.mistake.MistakeRequest
import retrofit2.http.*

interface MistakeApiService {
    @POST("api/mistakes")
    suspend fun createMistake(@Body mistakeRequest: MistakeRequest): Mistake

    @GET("api/mistakes/user/{userId}")
    suspend fun getMistakesByUser(@Path("userId") userId: Long): List<Mistake>

    @PUT("api/mistakes/{mistakeId}/review")
    suspend fun markAsReviewed(@Path("mistakeId") mistakeId: Long): Mistake

    @DELETE("api/mistakes/{mistakeId}")
    suspend fun deleteMistake(@Path("mistakeId") mistakeId: Long)
}