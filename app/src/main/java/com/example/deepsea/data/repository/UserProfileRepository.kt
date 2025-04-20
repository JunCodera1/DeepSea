// 2. Repository
package com.example.deepsea.data.repository

import com.example.deepsea.data.api.UserProfileService
import com.example.deepsea.data.model.UserProfileData
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
}