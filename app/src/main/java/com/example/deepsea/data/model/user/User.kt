package com.example.deepsea.data.model.user

data class User (
    val id: Long,
    val username: String = "",
    val email: String? = null,
    val password: String = "",
    val lastLogin: String? = null,
    val profileData: UserProfile?
)