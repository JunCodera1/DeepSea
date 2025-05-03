package com.example.deepsea.data.model.user

data class JwtResponse(
    val token: String,
    val id: Long,
    val profile_id: Long,
    val username: String,
    val email: String,
    val firstLogin: Boolean
)