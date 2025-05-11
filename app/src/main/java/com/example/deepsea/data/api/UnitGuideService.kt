package com.example.deepsea.data.api

import com.example.deepsea.data.dto.UnitGuideDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UnitGuideService {
    @GET("api/units/{unitId}/guide")
    suspend fun getUnitGuide(@Path("unitId") unitId: Long): Response<UnitGuideDto>
}