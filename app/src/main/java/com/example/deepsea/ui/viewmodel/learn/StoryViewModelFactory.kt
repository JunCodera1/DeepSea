package com.example.deepsea.ui.viewmodel.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.deepsea.data.repository.StoryRepository

class StoryViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}