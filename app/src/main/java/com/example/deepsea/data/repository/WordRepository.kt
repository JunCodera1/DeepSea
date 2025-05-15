package com.example.deepsea.data.repository

import com.example.deepsea.data.api.WordApiService
import com.example.deepsea.data.model.review.Word

class WordRepository(private val wordApiService: WordApiService) {
    suspend fun getAllWords(): Result<List<Word>> = try {
        Result.success(wordApiService.getAllWords())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getWordsByTheme(theme: String): Result<List<Word>> = try {
        Result.success(wordApiService.getWordsByTheme(theme))
    } catch (e: Exception) {
        Result.failure(e)
    }
}