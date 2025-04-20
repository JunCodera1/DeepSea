package com.example.deepsea.data.api

import com.example.deepsea.data.model.FriendSuggestion
import com.example.deepsea.data.model.UserProfileData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserProfileService {
    @GET("user/profile/id/{id}")
    suspend fun getUserProfileById(@Path("id") id: Int): UserProfileData

    @GET("user/suggestions")
    suspend fun getFriendSuggestions(): List<FriendSuggestion>

    @GET("api/users/data/{userId}/profile")
    suspend fun getUserProfile(@Path("userId") userId: Long): Response<UserProfileData>
}
