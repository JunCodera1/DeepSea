package com.example.deepsea.ui.viewmodel.languageSelection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.model.language.LanguageOption
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

