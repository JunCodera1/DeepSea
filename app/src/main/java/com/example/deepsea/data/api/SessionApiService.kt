package com.example.deepsea.data.api

import retrofit2.http.Body
import retrofit2.http.POST

interface SessionApiService {
    @POST("api/sessions")
    suspend fun saveSession(@Body session: SessionData): SessionData
}