package com.example.deepsea.data.api

import com.example.deepsea.data.model.LanguageData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LanguageApi {
    @GET("api/languages")
    suspend fun getAvailableLanguages(): List<LanguageData>

    @POST("api/users/{userId}/languages")
    suspend fun addLanguageToUser(
        @Path("userId") userId: Long,
        @Body request: LanguageApiService.AddLanguageRequest
    ): LanguageApiService.ApiResponse
}