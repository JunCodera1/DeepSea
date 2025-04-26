package com.example.deepsea.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.model.LanguageOption
import com.example.deepsea.data.model.SurveyOption
import com.example.deepsea.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.toMutableSet

class LanguageSelectionViewModel(
    private val userRepository: UserProfileRepository
) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow<Set<LanguageOption>>(emptySet())
    val selectedLanguages: StateFlow<Set<LanguageOption>> = _selectedLanguage.asStateFlow()

    fun toggleLanguageSelection(option: LanguageOption) {
        val currentSelections = _selectedLanguage.value.toMutableSet()
        if (currentSelections.contains(option)) {
            currentSelections.remove(option)
        } else {
            currentSelections.add(option)
        }
        _selectedLanguage.value = currentSelections
    }

    fun saveLanguageSelections(userId: Long?) {
        viewModelScope.launch {
            userRepository.updateUserLanguageSelections(
                userId = userId,
                languageSelections = _selectedLanguage.value
            )
        }
    }
}

class LanguageSelectionViewModelFactory(private val userRepository: UserProfileRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LanguageSelectionViewModel::class.java)) {
            return LanguageSelectionViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}