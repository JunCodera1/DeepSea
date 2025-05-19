package com.example.deepsea.data.api

import com.example.deepsea.data.model.course.path.UserPathDto
import com.example.deepsea.data.model.course.language.LanguageOptionRequest
import com.example.deepsea.data.model.course.path.PathOptionRequest
import com.example.deepsea.data.model.daily.DayStreakRequest
import com.example.deepsea.data.model.goal.DailyGoalRequest
import com.example.deepsea.data.model.survey.SurveyOptionRequest
import com.example.deepsea.data.model.survey.SurveyOptionResponse
import com.example.deepsea.data.model.user.FriendSuggestion
import com.example.deepsea.data.model.user.UserProfile
import com.example.deepsea.data.model.leaderboard.LeaderboardEntry
import com.example.deepsea.data.model.leaderboard.LeaderboardRankResponse
import com.example.deepsea.data.model.user.UpdateProgressRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserProfileService {
    @GET("api/users/data/{id}/profile")
    suspend fun getUserProfileById(@Path("id") id: Long?): UserProfile

    @GET("user/suggestions")
    suspend fun getFriendSuggestions(): List<FriendSuggestion>

    @GET("api/users/data/{userId}/profile")
    suspend fun getUserProfile(@Path("userId") userId: Long): Response<UserProfile>

    @POST("/api/users/update-survey")
    fun updateSurveyOption(@Body updateRequest: SurveyOptionRequest): Response<SurveyOptionResponse>

    @POST("api/survey/save")
    suspend fun saveSurveySelections(@Body request: SurveyOptionRequest): UserProfile

    @POST("api/language/save")
    suspend fun saveLanguageSelections(@Body request: LanguageOptionRequest): UserProfile

    @POST("api/courses/path")
    suspend fun savePath(@Body request: PathOptionRequest): Response<Unit>

    @GET("/api/courses/{userId}")
    suspend fun getUserPaths(@Path("userId") userId: Long): List<UserPathDto>

    @PUT("/api/users/{userId}/daily-goal")
    suspend fun updateDailyGoal(
        @Path("userId") profileId: Long?,
        @Body dailyGoal: DailyGoalRequest
    ): Response<Unit>

    @GET("api/leaderboard/top")
    suspend fun getTopLeaderboard(): List<LeaderboardEntry>

    @GET("api/leaderboard/all")
    suspend fun getAllUsersSortedByXp(): List<UserProfile>

    @GET("api/leaderboard/rank")
    suspend fun getUserRank(@Query("userId") userId: Long): LeaderboardRankResponse

    @POST("/api/users/{userId}/add-xp")
    suspend fun addXp(
        @Path("userId") userId: Long,
        @Query("amount") amount: Int
    ): UserProfile

    @PUT("/api/users/{userId}/progress")
    suspend fun updateProgress(
        @Path("userId") userId: Long,
        @Body request: UpdateProgressRequest
    ): Response<Unit>

    @PUT("api/users/{userId}/streak")
    suspend fun updateDayStreak(
        @Path("userId") userId: Long,
        @Body dayStreakRequest: DayStreakRequest
    ): Response<String>
}
