package com.example.deepsea.ui.viewmodel.learn

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.deepsea.data.api.HearingService

class LanguageListeningViewModelFactory(
    private val apiService: HearingService,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LanguageListeningViewModel::class.java)) {
            return LanguageListeningViewModel(apiService, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}