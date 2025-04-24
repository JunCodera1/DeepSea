package com.example.deepsea.data.api

import com.example.deepsea.data.model.FriendSuggestion
import com.example.deepsea.data.model.UserProfileData
import retrofit2.http.GET
import retrofit2.http.Path

interface UserProfileService {
    @GET("api/users/data/{id}/profile")
    suspend fun getUserProfileById(@Path("id") id: Long?): UserProfileData

    @GET("user/suggestions")
    suspend fun getFriendSuggestions(): List<FriendSuggestion>

    @GET("api/users/data/{userId}/profile")
    suspend fun getUserProfile(@Path("userId") userId: Long): Response<UserProfileData>
}
