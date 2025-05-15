package com.example.deepsea.ui.viewmodel.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.deepsea.data.repository.WordRepository

class WordViewModelFactory(private val wordRepository: WordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WordViewModel(wordRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}