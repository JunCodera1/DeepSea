package com.example.deepsea.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.model.survey.SurveyOption
import com.example.deepsea.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.toMutableSet

class SurveySelectionViewModel(
    private val userRepository: UserProfileRepository
) : ViewModel() {

    private val _selectedSurveys = MutableStateFlow<Set<SurveyOption>>(emptySet())
    val selectedSurveys: StateFlow<Set<SurveyOption>> = _selectedSurveys.asStateFlow()

    fun toggleSurveySelection(option: SurveyOption) {
        val currentSelections = _selectedSurveys.value.toMutableSet()
        if (currentSelections.contains(option)) {
            currentSelections.remove(option)
        } else {
            currentSelections.add(option)
        }
        _selectedSurveys.value = currentSelections
    }

    fun saveSurveySelections(userId: Long?) {
        viewModelScope.launch {
            userRepository.updateUserSurveySelections(
                userId = userId,
                surveySelections = _selectedSurveys.value
            )
        }
    }
}

class SurveyViewModelFactory(private val userRepository: UserProfileRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SurveySelectionViewModel::class.java)) {
            return SurveySelectionViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}