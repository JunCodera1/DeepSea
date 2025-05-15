package com.example.deepsea.ui.viewmodel.learn

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.example.deepsea.data.api.MistakeApiService
import com.example.deepsea.data.api.WordBuildingService
import com.example.deepsea.data.repository.MistakeRepository

class WordBuildingViewModelFactory(
    private val apiService: WordBuildingService,
    private val mistakeRepository: MistakeRepository,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordBuildingViewModel::class.java)) {
            return WordBuildingViewModel(apiService, mistakeRepository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}