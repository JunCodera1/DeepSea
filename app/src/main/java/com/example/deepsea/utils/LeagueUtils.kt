package com.example.deepsea.utils

import com.example.deepsea.data.model.user.UserProfile
import com.example.deepsea.ui.screens.feature.leaderboard.LeagueTier

/**
 * Utility class to determine the league tier based on XP points
 */
object LeagueUtils {

    /**
     * Determines the appropriate LeagueTier based on the total XP of the user
     * @param totalXp The total experience points of the user
     * @return The corresponding LeagueTier
     */
    fun getLeagueTierForXp(totalXp: Int): LeagueTier {
        return when (totalXp) {
            in 0..699 -> LeagueTier.BRONZE
            in 700..1499 -> LeagueTier.SILVER
            in 1500..2499 -> LeagueTier.GOLD
            in 2500..3999 -> LeagueTier.PLATINUM
            in 4000..5999 -> LeagueTier.DIAMOND
            in 6000..7999 -> LeagueTier.MASTER
            in 8000..14999 -> LeagueTier.MASTER // Note: This range is also assigned to MASTER
            in 15000..35999 -> LeagueTier.GRANDMASTER
            else -> LeagueTier.CHALLENGE
        }
    }

    /**
     * Groups users by their league based on total XP
     * @param users List of user profiles
     * @return Map of league tiers to lists of users in that tier
     */
    fun groupUsersByLeague(users: List<UserProfile>): Map<LeagueTier, List<UserProfile>> {
        return users.groupBy { getLeagueTierForXp(it.totalXp) }
    }

    /**
     * Gets users for a specific league
     * @param users List of all user profiles
     * @param leagueTier The league tier to filter by
     * @return List of users in the specified league
     */
    fun getUsersForLeague(users: List<UserProfile>, leagueTier: LeagueTier): List<UserProfile> {
        return users.filter { getLeagueTierForXp(it.totalXp) == leagueTier }
    }
}