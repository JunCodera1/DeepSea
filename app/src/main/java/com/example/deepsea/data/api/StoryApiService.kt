package com.example.deepsea.data.api

import com.example.deepsea.data.model.review.Story
import retrofit2.http.GET

interface StoryApiService {
    @GET("api/stories")
    suspend fun getAllStories(): List<Story>
}