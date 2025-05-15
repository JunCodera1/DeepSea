package com.example.deepsea.ui.viewmodel.review

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.review.Mistake
import com.example.deepsea.data.model.user.User
import com.example.deepsea.data.repository.MistakeRepository
import com.example.deepsea.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class MistakesViewModel(
    application: Application,
    private val mistakeRepository: MistakeRepository,
    private val userProfileRepository: UserProfileRepository
) : AndroidViewModel(application) {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _mistakes = MutableStateFlow<List<Mistake>>(emptyList())
    val mistakes: StateFlow<List<Mistake>> = _mistakes.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Current user state
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                // For now, we're using a fixed user ID (1)
                // In a real app, this would come from authentication
                val userId = 1L

                userProfileRepository.getUserProfile(userId)
                    .onSuccess { profileData ->
                        _currentUser.value = User(
                            id = userId,
                            username = profileData.username,
                            profileData = profileData
                        )
                        // Now that we have a user, load the mistakes
                        loadMistakes()
                    }
                    .onFailure { error ->
                        _errorMessage.value = "Failed to load user: ${error.message}"
                    }
            } catch (e: Exception) {
                Timber.e(e, "Error loading current user")
                _errorMessage.value = "Error loading user: ${e.message}"
            }
        }
    }

    fun loadMistakes() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val user = _currentUser.value
                if (user != null) {
                    val fetchedMistakes = mistakeRepository.getMistakesByUserId(user.id)
                    _mistakes.value = fetchedMistakes
                } else {
                    _errorMessage.value = "No user logged in"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load mistakes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markMistakeAsReviewed(mistakeId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                mistakeRepository.markMistakeAsReviewed(mistakeId)
                // Refresh the list after marking as reviewed
                loadMistakes()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to mark mistake as reviewed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMistake(mistakeId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                mistakeRepository.deleteMistake(mistakeId)
                // Refresh the list after deletion
                loadMistakes()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete mistake: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    class Factory(
        private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MistakesViewModel::class.java)) {
                val mistakeRepository = MistakeRepository(RetrofitClient.mistakeApiService)
                val userRepository = UserProfileRepository(RetrofitClient.userProfileService)
                return MistakesViewModel(application, mistakeRepository, userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}