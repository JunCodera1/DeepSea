package com.example.deepsea.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.model.UserProfileData
import com.example.deepsea.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadUserProfile(userId: Long) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                userProfileRepository.getUserProfile(userId).fold(
                    onSuccess = { profile ->
                        _uiState.value = ProfileUiState.Success(profile)
                    },
                    onFailure = { error ->
                        _uiState.value = ProfileUiState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

// UI State để quản lý các trạng thái khác nhau
sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(val data: UserProfileData) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}