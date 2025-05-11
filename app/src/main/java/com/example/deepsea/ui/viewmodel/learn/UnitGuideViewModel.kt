package com.example.deepsea.ui.viewmodel.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.dto.UnitGuideDto
import com.example.deepsea.data.repository.UnitGuideRepository
import com.example.deepsea.utils.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UnitGuideViewModel(
    private val repository: UnitGuideRepository
) : ViewModel() {

    private val _guideState = MutableStateFlow<GuideUiState>(GuideUiState.Initial)
    val guideState: StateFlow<GuideUiState> = _guideState

    fun loadUnitGuide(unitId: Long) {
        viewModelScope.launch {
            repository.getUnitGuide(unitId).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _guideState.value = GuideUiState.Loading
                    }
                    is NetworkResult.Success -> {
                        _guideState.value = GuideUiState.Success(result.data)
                    }
                    is NetworkResult.Error -> {
                        _guideState.value = GuideUiState.Error(result.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    fun playAudio(audioUrl: String) {
        // Implement audio playback logic here
        // This could connect to a media player service
    }
}
sealed class GuideUiState {
    object Initial : GuideUiState()
    object Loading : GuideUiState()
    data class Success(val data: UnitGuideDto) : GuideUiState()
    data class Error(val message: String) : GuideUiState()
}

class UnitGuideViewModelFactory(
    private val repository: UnitGuideRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UnitGuideViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UnitGuideViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
