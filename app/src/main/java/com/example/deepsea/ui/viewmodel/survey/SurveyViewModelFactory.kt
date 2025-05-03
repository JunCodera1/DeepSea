package com.example.deepsea.ui.viewmodel.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.deepsea.data.repository.UserProfileRepository

class SurveyViewModelFactory(private val userRepository: UserProfileRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SurveySelectionViewModel::class.java)) {
            return SurveySelectionViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}