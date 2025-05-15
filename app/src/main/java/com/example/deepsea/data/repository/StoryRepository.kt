package com.example.deepsea.data.repository

import com.example.deepsea.data.api.StoryApiService
import com.example.deepsea.data.model.review.Story

class StoryRepository(private val storyApiService: StoryApiService) {
    suspend fun getAllStories(): Result<List<Story>> = try {
        Result.success(storyApiService.getAllStories())
    } catch (e: Exception) {
        Result.failure(e)
    }
}