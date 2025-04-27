package com.example.deepsea.ui.viewmodel.surveySelection

import androidx.lifecycle.ViewModel
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

