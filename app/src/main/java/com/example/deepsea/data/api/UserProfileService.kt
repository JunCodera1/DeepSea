package com.example.deepsea.data.api

import com.example.deepsea.data.model.course.path.UserPathDto
import com.example.deepsea.data.model.course.language.LanguageOptionRequest
import com.example.deepsea.data.model.course.path.PathOptionRequest
import com.example.deepsea.data.model.goal.DailyGoalRequest
import com.example.deepsea.data.model.survey.SurveyOptionRequest
import com.example.deepsea.data.model.survey.SurveyOptionResponse
import com.example.deepsea.data.model.user.FriendSuggestion
import com.example.deepsea.data.model.user.UserProfileData
import com.example.deepsea.data.model.leaderboard.LeaderboardEntry
import com.example.deepsea.data.model.leaderboard.LeaderboardRankResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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

    @PUT("/api/users/{userId}/daily-goal")
    suspend fun updateDailyGoal(
        @Path("userId") profileId: Long?,
        @Body dailyGoal: DailyGoalRequest
    ): Response<Unit>
//
    @GET("api/leaderboard/top")
    suspend fun getTopLeaderboard(): List<LeaderboardEntry>

    @GET("api/leaderboard/all")
    suspend fun getAllUsersSortedByXp(): List<UserProfileData>

    @GET("api/leaderboard/rank")
    suspend fun getUserRank(@Query("userId") userId: Long): LeaderboardRankResponse

    @POST("/api/users/{userId}/add-xp")
    suspend fun addXp(
        @Path("userId") userId: Long,
        @Query("amount") amount: Int
    ): UserProfileData
}
