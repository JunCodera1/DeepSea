package com.example.deepsea.data.api

import com.example.deepsea.data.model.exercise.WordPair
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface LearningApiService {
    @GET("/api/learn/sections/{sectionId}/units/{unitId}/matching-pairs")
    suspend fun getMatchingPairs(
        @Path("sectionId") sectionId: Long,
        @Path("unitId") unitId: Long
    ): List<WordPair>
}