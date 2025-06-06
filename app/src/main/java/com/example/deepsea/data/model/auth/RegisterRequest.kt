package com.example.deepsea.data.model.auth

data class RegisterRequest(
    val name: String,
    val username: String,
    val password: String,
    val email: String,
    val avatarUrl: String?
)
