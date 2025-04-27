package com.example.deepsea.data.api


import com.example.deepsea.data.model.user.JwtResponse
import com.example.deepsea.data.model.auth.LoginRequest
import com.example.deepsea.data.model.user.MessageResponse
import com.example.deepsea.data.model.auth.RegisterRequest
import com.example.deepsea.data.model.user.UploadResponse
import com.example.deepsea.data.model.user.User
import com.example.deepsea.data.model.forgotPassword.EmailRequest
import com.example.deepsea.data.model.forgotPassword.PasswordResetResponse
import com.example.deepsea.data.model.forgotPassword.ResetRequest
import com.example.deepsea.data.model.forgotPassword.VerifyRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<JwtResponse>

    @POST("api/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<MessageResponse>


    @GET("api/auth/users")
    suspend fun getAllUsers(): Call<List<User>>

    @GET("api/auth/home")
    suspend fun getDashboard(@Header("Authorization") token: String): Response<MessageResponse>
    @Multipart
    @POST("api/upload/avatar")
    suspend fun uploadAvatar(@Part avatar: MultipartBody.Part): Response<UploadResponse>

    @POST("api/password-reset/request")
    suspend fun requestPasswordReset(@Body request: EmailRequest): Response<PasswordResetResponse>

    @POST("api/password-reset/verify")
    suspend fun verifyCode(@Body request: VerifyRequest): Response<PasswordResetResponse>

    @POST("api/password-reset/reset")
    suspend fun resetPassword(@Body request: ResetRequest): Response<PasswordResetResponse>
}
