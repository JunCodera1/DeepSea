package com.example.deepsea.data.model.user

data class LoginResponse(
    val token: String,
    val id: Long,
    val profile_id: Long,
    val username: String,
    val email: String,
    val firstLogin: Boolean
)