package com.example.deepsea.data.repository

import com.example.deepsea.data.api.RetrofitClient.authApi
import com.example.deepsea.data.model.forgotPassword.EmailRequest
import com.example.deepsea.data.model.forgotPassword.PasswordResetResponse
import com.example.deepsea.data.model.forgotPassword.ResetRequest
import com.example.deepsea.data.model.forgotPassword.VerifyRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PasswordResetRepository {
    private val apiService = authApi

    suspend fun requestPasswordReset(email: String): Result<PasswordResetResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.requestPasswordReset(EmailRequest(email))
                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to request password reset"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun verifyCode(email: String, code: String): Result<PasswordResetResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val verificationCode = code
                val response = apiService.verifyCode(VerifyRequest(email, verificationCode))
                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to verify code"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun resetPassword(email: String, code: String, newPassword: String): Result<PasswordResetResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val verificationCode = code
                val response = apiService.resetPassword(ResetRequest(email, verificationCode, newPassword))
                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to reset password"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}