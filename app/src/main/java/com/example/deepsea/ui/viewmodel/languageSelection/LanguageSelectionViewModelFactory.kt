package com.example.deepsea.ui.viewmodel.languageSelection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.deepsea.data.repository.UserProfileRepository

class LanguageSelectionViewModelFactory(private val userRepository: UserProfileRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LanguageSelectionViewModel::class.java)) {
            return LanguageSelectionViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}