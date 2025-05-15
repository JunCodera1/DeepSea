package com.example.deepsea.data.repository

import com.example.deepsea.data.api.MistakeApiService
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.review.Mistake
import com.example.deepsea.data.model.review.MistakeRequest

import retrofit2.HttpException
import java.io.IOException

class MistakeRepository(private val mistakeApiService: MistakeApiService = createMistakeApiService()) {

    suspend fun saveMistake(
        userId: Long,
        word: String,
        correctAnswer: String,
        userAnswer: String,
        lessonId: Long? = null
    ): Mistake {
        val mistakeRequest = MistakeRequest(
            userId = userId,
            word = word,
            correctAnswer = correctAnswer,
            userAnswer = userAnswer,
            lessonId = lessonId
        )

        try {
            return mistakeApiService.createMistake(mistakeRequest)
        } catch (e: IOException) {
            throw Exception("Network error occurred", e)
        } catch (e: HttpException) {
            throw Exception("HTTP error: ${e.code()}", e)
        } catch (e: Exception) {
            throw Exception("Failed to save mistake: ${e.message}", e)
        }
    }

    suspend fun getMistakesByUserId(userId: Long): List<Mistake> {
        try {
            return mistakeApiService.getMistakesByUser(userId)
        } catch (e: IOException) {
            throw Exception("Network error occurred", e)
        } catch (e: HttpException) {
            throw Exception("HTTP error: ${e.code()}", e)
        } catch (e: Exception) {
            throw Exception("Failed to get mistakes: ${e.message}", e)
        }
    }

    suspend fun markMistakeAsReviewed(mistakeId: Long): Mistake {
        try {
            return mistakeApiService.markAsReviewed(mistakeId)
        } catch (e: IOException) {
            throw Exception("Network error occurred", e)
        } catch (e: HttpException) {
            throw Exception("HTTP error: ${e.code()}", e)
        } catch (e: Exception) {
            throw Exception("Failed to mark mistake as reviewed: ${e.message}", e)
        }
    }

    suspend fun deleteMistake(mistakeId: Long) {
        try {
            mistakeApiService.deleteMistake(mistakeId)
        } catch (e: IOException) {
            throw Exception("Network error occurred", e)
        } catch (e: HttpException) {
            throw Exception("HTTP error: ${e.code()}", e)
        } catch (e: Exception) {
            throw Exception("Failed to delete mistake: ${e.message}", e)
        }
    }
}

// Đối tượng API Service
private fun createMistakeApiService(): MistakeApiService {
    return RetrofitClient.mistakeApiService
}