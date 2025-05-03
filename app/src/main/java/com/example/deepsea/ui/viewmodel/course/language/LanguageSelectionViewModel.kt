package com.example.deepsea.ui.viewmodel.course.language

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.model.course.language.LanguageOption
import com.example.deepsea.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.toMutableSet
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.io.IOException


class LanguageSelectionViewModel(
    private val userRepository: UserProfileRepository
) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow<Set<LanguageOption>>(emptySet())
    val selectedLanguages: StateFlow<Set<LanguageOption>> = _selectedLanguage.asStateFlow()
    private val _availableLanguages = mutableStateListOf<LanguageOption>()
    val availableLanguages: List<LanguageOption> = _availableLanguages

    // Error state
    private val _errorMessageState = mutableStateOf<String?>(null)
    val errorMessageState: State<String?> = _errorMessageState

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
    fun fetchAvailableLanguages() {
        viewModelScope.launch {
            try {
                // In a real app, you would fetch this from the API
                // For now, we'll use a sample list of languages
                val languages = getDefaultLanguages()
                _availableLanguages.clear()
                _availableLanguages.addAll(languages)
                _errorMessageState.value = null
            } catch (e: IOException) {
                // Handle network errors
                _errorMessageState.value = "Network error: Unable to fetch languages"
                e.printStackTrace()
            } catch (e: Exception) {
                // Handle other errors
                _errorMessageState.value = "Error: ${e.message}"
                e.printStackTrace()
            }
        }
    }
    private fun getDefaultLanguages(): List<LanguageOption> {
        // You would replace this with actual data from your backend
        return listOf(
            LanguageOption.ENGLISH,
            LanguageOption.SPANISH,
            LanguageOption.JAPANESE,
            LanguageOption.ITALY,
            LanguageOption.FRENCH,
            LanguageOption.GERMANY
        )
    }
}

