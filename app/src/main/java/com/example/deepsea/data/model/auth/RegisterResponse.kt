package com.example.deepsea.data.model.auth

data class RegisterResponse (
    val id: Long,
    val name:String,
    val username: String,
    val password: String,
    val email: String,
    val avatarUrl: String
)