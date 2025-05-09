package com.example.deepsea.ui.viewmodel.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.UserProfileService
import com.example.deepsea.data.model.leaderboard.LeaderboardEntry
import com.example.deepsea.data.model.leaderboard.LeaderboardRankResponse
import com.example.deepsea.data.model.user.UserProfileData
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

    private val _userRankState = MutableStateFlow<Map<Long, LeaderboardRankResponse>>(emptyMap())
    val userRankState: StateFlow<Map<Long, LeaderboardRankResponse>> = _userRankState.asStateFlow()

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

    // Refresh all data
    fun refreshAllData(currentUserId: Long) {
        getTopLeaderboard()
        getAllUsersSortedByXp()
        getUserRank(currentUserId)
    }
}