package com.example.deepsea.data.model.friend

import com.example.deepsea.data.model.course.language.LanguageOption

/**
 * Data model for friend suggestions
 */
data class FriendSuggestion(
    val id: Long,
    val suggestedUserId: Long,
    val suggestedUserName: String,
    val suggestedUsername: String,
    val suggestedUserAvatarUrl: String? = null,
    val similarityScore: Double,
    val suggestionReason: String,
    val sharedLanguages: List<LanguageOption> = emptyList(),
    val suggestedUserStats: UserStats
)

/**
 * User stats for friend suggestion display
 */
data class UserStats(
    val totalXp: Int,
    val dayStreak: Int,
    val currentLeague: String
)

/**
 * Response wrapper for friend suggestions API
 */
data class FriendSuggestionResponse(
    val suggestions: List<FriendSuggestion>,
    val count: Int
)