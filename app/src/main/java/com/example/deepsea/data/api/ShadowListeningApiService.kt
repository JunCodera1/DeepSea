package com.example.deepsea.data.api

import com.example.deepsea.data.model.review.ShadowListeningSession
import retrofit2.http.GET
import retrofit2.http.Path

interface ShadowListeningApiService {
    @GET("api/sessions-listening")
    suspend fun getSessions(): List<ShadowListeningSession>

    @GET("api/sessions-listening/{id}")
    suspend fun getSession(@Path("id") id: String): ShadowListeningSession
}