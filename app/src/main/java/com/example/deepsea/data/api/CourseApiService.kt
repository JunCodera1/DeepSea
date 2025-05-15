package com.example.deepsea.data.api

import com.example.deepsea.data.dto.SectionDto
import com.example.deepsea.data.dto.UnitDto
import com.example.deepsea.data.dto.UnitProgressDto
import com.example.deepsea.data.dto.UserProgressDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CourseApiService {
    @GET("api/sections")
    suspend fun getAllSections(): Response<List<SectionDto>>

    @GET("api/sections/{id}")
    suspend fun getSectionById(@Path("id") id: Long): Response<SectionDto>

    @GET("api/units/section/{sectionId}")
    suspend fun getUnitsBySection(@Path("sectionId") sectionId: Long): Response<List<UnitDto>>

    @GET("api/units/{id}")
    suspend fun getUnitById(@Path("id") id: Long): Response<UnitDto>

    @GET("api/units/{id}/progress")
    suspend fun getUnitProgress(
        @Path("id") id: Long,
        @Query("userId") userId: Long
    ): Response<UnitProgressDto>

    @GET("api/progress/user/{userId}")
    suspend fun getUserProgress(@Path("userId") userId: Long): Response<UserProgressDto>

    @POST("api/user/progress")
    suspend fun updateUserProgress(@Body progress: UserProgressDto): Response<Unit>

}