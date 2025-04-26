package com.example.deepsea.data.api

import com.example.deepsea.data.model.LanguageData

object LanguageApiService {
    private val api = RetrofitClient.retrofit.create(LanguageApi::class.java)

    suspend fun getAvailableLanguages(): List<LanguageData> {
        return api.getAvailableLanguages()
    }

    suspend fun addLanguageToUser(userId: Long, languageCode: String): Boolean {
        return api.addLanguageToUser(userId, AddLanguageRequest(languageCode)).isSuccess
    }

    data class AddLanguageRequest(val languageCode: String)
    data class ApiResponse(val isSuccess: Boolean, val message: String)
}

