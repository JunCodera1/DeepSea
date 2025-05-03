package com.example.deepsea.data.api

import com.example.deepsea.data.model.course.UserPathDto
import com.example.deepsea.data.model.course.language.LanguageOption
import com.example.deepsea.data.model.user.FriendSuggestion
import com.example.deepsea.data.model.course.language.LanguageOptionRequest
import com.example.deepsea.data.model.course.path.PathOption
import com.example.deepsea.data.model.course.path.PathOptionRequest
import com.example.deepsea.data.model.goal.DailyGoalOption
import com.example.deepsea.data.model.survey.SurveyOptionRequest
import com.example.deepsea.data.model.survey.SurveyOptionResponse
import com.example.deepsea.data.model.user.UserProfileData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserProfileService {
    @GET("api/users/data/{id}/profile")
    suspend fun getUserProfileById(@Path("id") id: Long?): UserProfileData

    @GET("user/suggestions")
    suspend fun getFriendSuggestions(): List<FriendSuggestion>

    @GET("api/users/data/{userId}/profile")
    suspend fun getUserProfile(@Path("userId") userId: Long): Response<UserProfileData>

    @POST("/api/users/update-survey")
    fun updateSurveyOption(@Body updateRequest: SurveyOptionRequest) : Response<SurveyOptionResponse>

    @POST("api/survey/save")
    suspend fun saveSurveySelections(@Body request: SurveyOptionRequest): UserProfileData

    @POST("api/language/save")
    suspend fun saveLanguageSelections(@Body request: LanguageOptionRequest): UserProfileData

    @POST("api/courses/path")
    suspend fun savePath(@Body request: PathOptionRequest): Response<Unit>

    @GET("/api/courses/{userId}")
    suspend fun getUserPaths(@Path("userId") userId: Long): List<UserPathDto>

    @PUT("users/{userId}/daily-goal")
    suspend fun updateDailyGoal(
        @Path("profileId") profileId: Long,
        @Body dailyGoal: DailyGoalOption
    ): Response<Unit>
}
