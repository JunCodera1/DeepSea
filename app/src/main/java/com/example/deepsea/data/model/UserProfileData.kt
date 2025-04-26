package com.example.deepsea.data.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


data class UserProfileData(
    val name: String,
    val username: String,
    val joinDate: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")),
    val courses: Set<LanguageOption> = emptySet(),
    val selectedSurveys: Set<SurveyOption> = emptySet(),
    val followers: Int,
    val following: Int,
    val dayStreak: Int,
    val totalXp: Int,
    val currentLeague: String,
    val topFinishes: Int,
    val friends: Set<Int?> = setOf(null)
)
