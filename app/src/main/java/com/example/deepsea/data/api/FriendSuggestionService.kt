package com.example.deepsea.data.api

import com.example.deepsea.data.model.friend.FriendSuggestionResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for friend suggestion API
 */
interface FriendSuggestionService {
    /**
     * Get active friend suggestions for a user
     */
    @GET("friend-suggestions/user/{userId}")
    suspend fun getFriendSuggestions(
        @Path("userId") userId: Long?
    ): FriendSuggestionResponse

    /**
     * Generate new friend suggestions for a user
     */
    @POST("friend-suggestions/generate/{userId}")
    suspend fun generateFriendSuggestions(
        @Path("userId") userId: Long,
        @Query("maxSuggestions") maxSuggestions: Int = 5
    ): FriendSuggestionResponse

    /**
     * Follow a friend suggestion (add as friend)
     */
    @POST("friend-suggestions/{suggestionId}/follow")
    suspend fun followSuggestion(
        @Path("suggestionId") suggestionId: Long
    ): Map<String, Boolean>

    /**
     * Dismiss a friend suggestion
     */
    @POST("friend-suggestions/{suggestionId}/dismiss")
    suspend fun dismissSuggestion(
        @Path("suggestionId") suggestionId: Long
    ): Map<String, Boolean>
}