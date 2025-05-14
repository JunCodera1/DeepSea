package com.example.deepsea.data.api

import com.example.deepsea.data.model.exercise.TranslationExercise
import retrofit2.http.GET

interface WordBuildingService {
    @GET("api/translation-exercises/random")
    suspend fun getTranslationExercise(): TranslationExercise
}