package com.example.deepsea.data.api

import com.example.deepsea.data.model.review.Word
import retrofit2.http.GET
import retrofit2.http.Query

interface WordApiService {
    @GET("api/words")
    suspend fun getAllWords(): List<Word>

    @GET("api/words/by-theme")
    suspend fun getWordsByTheme(@Query("theme") theme: String): List<Word>
}