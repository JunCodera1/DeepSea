package com.example.deepsea.data.model

data class User (
    val id: Long,
    val username: String = "",
    val email: String? = null,
    val password: String = "",
    val profileData: UserProfileData?
)