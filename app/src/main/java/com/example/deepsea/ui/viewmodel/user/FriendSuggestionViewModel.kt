package com.example.deepsea.ui.viewmodel.user

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.FriendSuggestionService
import com.example.deepsea.data.model.friend.FriendSuggestion
import com.example.deepsea.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class FriendSuggestionViewModel(
    private val friendSuggestionService: FriendSuggestionService,
    private val sessionManager: SessionManager
) : ViewModel() {
    // UI state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Friend suggestions list
    private val _suggestions = mutableStateListOf<FriendSuggestion>()
    val suggestions: List<FriendSuggestion> = _suggestions

    // Track if we have fetched suggestions
    private val _hasFetchedSuggestions = mutableStateOf(false)
    val hasFetchedSuggestions = _hasFetchedSuggestions

    /**
     * Load friend suggestions for the current user
     */
    fun loadFriendSuggestions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val userId = sessionManager.userId.firstOrNull()

                if (userId == null) {
                    _error.value = "User ID not available"
                    _isLoading.value = false
                    return@launch
                }

                val response = friendSuggestionService.getFriendSuggestions(userId)
                _suggestions.clear()
                _suggestions.addAll(response.suggestions)
                _hasFetchedSuggestions.value = true

            } catch (e: Exception) {
                Log.e("FriendSuggestionVM", "Error loading friend suggestions", e)
                _error.value = "Failed to load friend suggestions: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Generate new suggestions for the current user
     */
    fun generateFriendSuggestions(maxSuggestions: Int = 5) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val userId = sessionManager.userId.firstOrNull() ?: run {
                    _error.value = "User not logged in"
                    _isLoading.value = false
                    return@launch
                }

                val response = friendSuggestionService.generateFriendSuggestions(userId, maxSuggestions)
                _suggestions.clear()
                _suggestions.addAll(response.suggestions)
                _hasFetchedSuggestions.value = true

            } catch (e: Exception) {
                Log.e("FriendSuggestionVM", "Error generating friend suggestions", e)
                _error.value = "Failed to generate friend suggestions: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Follow a suggestion (add as friend)
     */
    fun followSuggestion(suggestionId: Long) {
        viewModelScope.launch {
            try {
                val result = friendSuggestionService.followSuggestion(suggestionId)
                if (result["success"] == true) {
                    // Remove from the suggestions list
                    _suggestions.removeIf { it.id == suggestionId }
                }
            } catch (e: Exception) {
                Log.e("FriendSuggestionVM", "Error following suggestion", e)
                _error.value = "Failed to add friend: ${e.localizedMessage}"
            }
        }
    }

    /**
     * Dismiss a suggestion
     */
    fun dismissSuggestion(suggestionId: Long) {
        viewModelScope.launch {
            try {
                val result = friendSuggestionService.dismissSuggestion(suggestionId)
                if (result["success"] == true) {
                    // Remove from the suggestions list
                    _suggestions.removeIf { it.id == suggestionId }
                }
            } catch (e: Exception) {
                Log.e("FriendSuggestionVM", "Error dismissing suggestion", e)
                _error.value = "Failed to dismiss suggestion: ${e.localizedMessage}"
            }
        }
    }

    /**
     * Clear any error messages
     */
    fun clearError() {
        _error.value = null
    }
}

/**
 * Factory for creating FriendSuggestionViewModel with dependencies
 */
class FriendSuggestionViewModelFactory(
    private val friendSuggestionService: FriendSuggestionService,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendSuggestionViewModel::class.java)) {
            return FriendSuggestionViewModel(friendSuggestionService, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}