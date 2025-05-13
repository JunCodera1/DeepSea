package com.example.deepsea.data.api

import com.example.deepsea.data.model.exercise.HearingExercise
import com.example.deepsea.data.model.exercise.WordPair
import retrofit2.http.GET
import retrofit2.http.Path

interface LearningApiService {
    @GET("/api/learn/sections/{sectionId}/units/{unitId}/matching-pairs")
    suspend fun getMatchingPairs(
        @Path("sectionId") sectionId: Long,
        @Path("unitId") unitId: Long
    ): List<WordPair>

    @GET("api/language-learning/sections/{sectionId}/units/{unitId}/exercise")
    suspend fun getRandomExercise(
        @Path("sectionId") sectionId: Long,
        @Path("unitId") unitId: Long
    ): HearingExercise
}