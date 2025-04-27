package com.example.deepsea.data.repository

import com.example.deepsea.data.api.UserProfileService
import com.example.deepsea.data.model.language.LanguageOption
import com.example.deepsea.data.model.language.LanguageOptionRequest
import com.example.deepsea.data.model.survey.SurveyOption
import com.example.deepsea.data.model.survey.SurveyOptionRequest
import com.example.deepsea.data.model.user.UserProfileData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserProfileRepository @Inject constructor(
    private val userProfileService: UserProfileService
) {
    suspend fun getUserProfile(userId: Long): Result<UserProfileData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userProfileService.getUserProfile(userId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch user profile: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    suspend fun updateUserSurveySelections(
        userId:Long? ,
        surveySelections: Set<SurveyOption>
    ): UserProfileData {
        return withContext(Dispatchers.IO) {
            val request = SurveyOptionRequest(userId = userId, selectedOptions =  surveySelections)
            userProfileService.saveSurveySelections(request)
        }
    }

    suspend fun updateUserLanguageSelections(
        userId:Long? ,
        languageSelections: Set<LanguageOption>
    ): UserProfileData {
        return withContext(Dispatchers.IO) {
            val request = LanguageOptionRequest(userId = userId, selectedOptions =  languageSelections)
            userProfileService.saveLanguageSelections(request)
        }
    }
}