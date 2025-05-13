package com.example.deepsea.data.model.user

import com.example.deepsea.data.model.course.language.LanguageOption
import com.example.deepsea.data.model.survey.SurveyOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


data class UserProfileData(
    val name: String,
    val username: String,
    val joinDate: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")),
    val courses: Set<LanguageOption> = emptySet(),
    val selectedSurveys: Set<SurveyOption> = emptySet(),
    val avatarUrl: String? = null, // Added avatar URL field
    val followers: Int,
    val following: Int,
    val dayStreak: Int,
    val totalXp: Int,
    val currentLeague: String,
    val topFinishes: Int,
    val friends: Set<Int?> = setOf(null)
)
