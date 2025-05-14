package com.example.deepsea.ui.viewmodel.learn

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.deepsea.data.api.WordBuildingService

class WordBuildingViewModelFactory(
    private val apiService: WordBuildingService,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordBuildingViewModel::class.java)) {
            return WordBuildingViewModel(apiService, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}