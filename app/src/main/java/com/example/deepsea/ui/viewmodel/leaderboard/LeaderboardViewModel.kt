package com.example.deepsea.ui.viewmodel.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.UserProfileService
import com.example.deepsea.data.model.leaderboard.LeaderboardEntry
import com.example.deepsea.data.model.leaderboard.LeaderboardRankResponse
import com.example.deepsea.data.model.user.UserProfileData
import com.example.deepsea.ui.screens.feature.leaderboard.LeagueTier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LeaderboardViewModel(private val userProfileService: UserProfileService) : ViewModel() {

    // State flows for observable UI state
    private val _topLeaderboardState = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val topLeaderboardState: StateFlow<List<LeaderboardEntry>> = _topLeaderboardState.asStateFlow()

    private val _allUsersState = MutableStateFlow<List<UserProfileData>>(emptyList())
    val allUsersState: StateFlow<List<UserProfileData>> = _allUsersState.asStateFlow()

    // New state for filtered users by league
    private val _filteredUsersState = MutableStateFlow<List<Any>>(emptyList())
    val filteredUsersState: StateFlow<List<Any>> = _filteredUsersState.asStateFlow()

    private val _userRankState = MutableStateFlow<Map<Long, LeaderboardRankResponse>>(emptyMap())
    val userRankState: StateFlow<Map<Long, LeaderboardRankResponse>> = _userRankState.asStateFlow()

    // Current selected league
    private val _currentLeague = MutableStateFlow(LeagueTier.DIAMOND)
    val currentLeague: StateFlow<LeagueTier> = _currentLeague.asStateFlow()

    // Loading states
    private val _isLoadingTop = MutableStateFlow(false)
    val isLoadingTop: StateFlow<Boolean> = _isLoadingTop.asStateFlow()

    private val _isLoadingAll = MutableStateFlow(false)
    val isLoadingAll: StateFlow<Boolean> = _isLoadingAll.asStateFlow()

    private val _isLoadingRank = MutableStateFlow(false)
    val isLoadingRank: StateFlow<Boolean> = _isLoadingRank.asStateFlow()

    // Error states
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Original properties for compatibility
    private val _topLeaderboard = mutableListOf<LeaderboardEntry>()
    val topLeaderboard: List<LeaderboardEntry> get() = _topLeaderboard

    private val _allUsers = mutableListOf<UserProfileData>()
    val allUsers: List<UserProfileData> get() = _allUsers

    private val _userRank = mutableMapOf<Long, LeaderboardRankResponse>()
    val userRank: Map<Long, LeaderboardRankResponse> get() = _userRank

    // Fetch top leaderboard
    fun getTopLeaderboard() {
        viewModelScope.launch {
            _isLoadingTop.value = true
            _errorMessage.value = null

            try {
                val response: List<LeaderboardEntry> = userProfileService.getTopLeaderboard()
                _topLeaderboard.clear()
                _topLeaderboard.addAll(response)
                _topLeaderboardState.value = response
                // Also update filtered users for the current league
                filterUsersByLeague(_currentLeague.value)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load top leaderboard: ${e.message}"
            } finally {
                _isLoadingTop.value = false
            }
        }
    }

    // Fetch all users sorted by XP
    fun getAllUsersSortedByXp() {
        viewModelScope.launch {
            _isLoadingAll.value = true
            _errorMessage.value = null

            try {
                val response: List<UserProfileData> = userProfileService.getAllUsersSortedByXp()
                _allUsers.clear()
                _allUsers.addAll(response)
                _allUsersState.value = response
                // Also update filtered users for the current league
                filterUsersByLeague(_currentLeague.value)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load all users: ${e.message}"
            } finally {
                _isLoadingAll.value = false
            }
        }
    }

    // Fetch user rank by user ID
    fun getUserRank(userId: Long) {
        viewModelScope.launch {
            _isLoadingRank.value = true
            _errorMessage.value = null

            try {
                val response: LeaderboardRankResponse = userProfileService.getUserRank(userId)
                _userRank[userId] = response
                _userRankState.value = _userRank.toMap() // Create a new map to trigger state update
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load user rank: ${e.message}"
            } finally {
                _isLoadingRank.value = false
            }
        }
    }

    // Update the current league and filter users
    fun setCurrentLeague(league: LeagueTier) {
        _currentLeague.value = league
        filterUsersByLeague(league)
    }

    // Filter users by league
    private fun filterUsersByLeague(league: LeagueTier) {
        viewModelScope.launch {
            val xpRange = getXpRangeForLeague(league)

            val filteredEntries = when {
                _allUsers.isNotEmpty() -> {
                    _allUsers.filter { user ->
                        user.totalXp in xpRange
                    }
                }
                _topLeaderboard.isNotEmpty() -> {
                    _topLeaderboard.filter { entry ->
                        entry.totalXp in xpRange
                    }
                }
                else -> emptyList()
            }

            _filteredUsersState.value = filteredEntries
        }
    }

    // Determine XP range for a league
    private fun getXpRangeForLeague(league: LeagueTier): IntRange {
        return when (league) {
            LeagueTier.BRONZE -> 0..699
            LeagueTier.SILVER -> 700..1499
            LeagueTier.GOLD -> 1500..2499
            LeagueTier.PLATINUM -> 2500..3999
            LeagueTier.DIAMOND -> 4000..5999
            LeagueTier.MASTER -> 6000..7999
            LeagueTier.GRANDMASTER -> 8000..14999
            LeagueTier.CHALLENGE -> 15000..Int.MAX_VALUE
        }
    }

    // Refresh all data
    fun refreshAllData(currentUserId: Long) {
        getTopLeaderboard()
        getAllUsersSortedByXp()
        getUserRank(currentUserId)
    }
}