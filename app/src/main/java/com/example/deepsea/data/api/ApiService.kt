package com.example.deepsea.data.api


import com.example.deepsea.data.model.exercise.HearingExercise
import com.example.deepsea.data.model.auth.GoogleTokenRequest
import com.example.deepsea.data.model.exercise.TranslationExercise
import com.example.deepsea.data.model.user.LoginResponse
import com.example.deepsea.data.model.auth.LoginRequest
import com.example.deepsea.data.model.user.MessageResponse
import com.example.deepsea.data.model.auth.RegisterRequest
import com.example.deepsea.data.model.user.User
import com.example.deepsea.data.model.forgotPassword.EmailRequest
import com.example.deepsea.data.model.forgotPassword.PasswordResetResponse
import com.example.deepsea.data.model.forgotPassword.ResetRequest
import com.example.deepsea.data.model.forgotPassword.VerifyRequest
import retrofit2.Response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<MessageResponse>


    @GET("api/auth/users")
    suspend fun getAllUsers(): Call<List<User>>

    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body googleTokenRequest: GoogleTokenRequest): Response<LoginResponse>


    @GET("api/auth/home")
    suspend fun getDashboard(@Header("Authorization") token: String): Response<MessageResponse>

    @POST("api/password-reset/request")
    suspend fun requestPasswordReset(@Body request: EmailRequest): Response<PasswordResetResponse>

    @POST("api/password-reset/verify")
    suspend fun verifyCode(@Body request: VerifyRequest): Response<PasswordResetResponse>

    @POST("api/password-reset/reset")
    suspend fun resetPassword(@Body request: ResetRequest): Response<PasswordResetResponse>

    @GET("api/exercises/hearing")
    suspend fun getExercise(): Response<HearingExercise>

    @GET("api/exercises/translation")
    suspend fun getTranslationExercise(): Response<TranslationExercise>
}
