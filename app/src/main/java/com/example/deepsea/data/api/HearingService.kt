package com.example.deepsea.data.api

import com.example.deepsea.data.dto.HearingExerciseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface HearingService {
    @GET("api/hearing-exercises/unit/{unitId}/random")
    suspend fun getRandomHearingExercise(@Path("unitId") unitId: Long): HearingExerciseDto
}