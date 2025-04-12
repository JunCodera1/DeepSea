package com.example.deepsea.ui.profile

data class UserProfileData(
    val name:String,
    val username:String,
    val joinDate:String,
    val courses: List<String>,
    val followers: Int,
    val following: Int,
    val dayStreak: Int,
    val totalXp: Int,
    val currentLeague: String,
    val topFinishes: Int,
    val isFriend: Boolean = false


    )

