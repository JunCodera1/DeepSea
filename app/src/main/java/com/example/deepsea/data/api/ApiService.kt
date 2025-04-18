package com.example.deepsea.data.api


import com.example.deepsea.data.model.JwtResponse
import com.example.deepsea.data.model.LoginRequest
import com.example.deepsea.data.model.MessageResponse
import com.example.deepsea.data.model.RegisterRequest
import com.example.deepsea.data.model.UploadResponse
import com.example.deepsea.data.model.User
import com.example.deepsea.data.model.UserProfileData
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

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


}
