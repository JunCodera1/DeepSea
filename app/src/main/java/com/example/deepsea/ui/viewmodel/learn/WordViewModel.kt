package com.example.deepsea.ui.viewmodel.learn

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.model.review.Word
import com.example.deepsea.data.repository.WordRepository
import kotlinx.coroutines.launch

class WordViewModel(private val wordRepository: WordRepository) : ViewModel() {
    var wordsState by mutableStateOf<WordsState>(WordsState.Loading)
        private set

    fun loadWords(theme: String = "All") {
        viewModelScope.launch {
            wordsState = WordsState.Loading
            val result = if (theme == "All") {
                wordRepository.getAllWords()
            } else {
                wordRepository.getWordsByTheme(theme)
            }
            wordsState = when {
                result.isSuccess -> WordsState.Success(result.getOrNull() ?: emptyList())
                else -> WordsState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
}

sealed class WordsState {
    object Loading : WordsState()
    data class Success(val words: List<Word>) : WordsState()
    data class Error(val message: String) : WordsState()
}