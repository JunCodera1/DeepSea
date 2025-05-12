package com.example.deepsea.ui.screens.feature.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.exercise.WordPair
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MatchingPairsViewModel : ViewModel() {
    private val _englishWords = MutableStateFlow<List<WordPair>>(emptyList())
    val englishWords: StateFlow<List<WordPair>> = _englishWords.asStateFlow()

    private val _japaneseWords = MutableStateFlow<List<WordPair>>(emptyList())
    val japaneseWords: StateFlow<List<WordPair>> = _japaneseWords.asStateFlow()

    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _hearts = MutableStateFlow(3)
    val hearts: StateFlow<Int> = _hearts.asStateFlow()

    private val _showFeedback = MutableStateFlow(false)
    val showFeedback: StateFlow<Boolean> = _showFeedback.asStateFlow()

    private val _isCorrectMatch = MutableStateFlow(false)
    val isCorrectMatch: StateFlow<Boolean> = _isCorrectMatch.asStateFlow()

    private val _selectedEnglishWord = MutableStateFlow<WordPair?>(null)
    val selectedEnglishWord: StateFlow<WordPair?> = _selectedEnglishWord.asStateFlow()

    private val _selectedJapaneseWord = MutableStateFlow<WordPair?>(null)
    val selectedJapaneseWord: StateFlow<WordPair?> = _selectedJapaneseWord.asStateFlow()

    init {
        fetchWordPairs(sectionId = 1, unitId = 1) // Example: Fetch for Section 1, Unit 1
    }

    private fun fetchWordPairs(sectionId: Long, unitId: Long) {
        viewModelScope.launch {
            try {
                val wordPairs = RetrofitClient.learningApiService.getMatchingPairs(sectionId, unitId)
                _englishWords.value = wordPairs
                _japaneseWords.value = wordPairs.shuffled() // Shuffle Japanese words for display
                _progress.value = 0f // Reset progress
            } catch (e: Exception) {
                // Handle network errors (e.g., show error message)
                e.printStackTrace()
            }
        }
    }

    fun selectWord(isEnglish: Boolean, wordPair: WordPair) {
        if (wordPair.isMatched) return

        if (isEnglish) {
            _selectedEnglishWord.value = wordPair
            _englishWords.value = _englishWords.value.map {
                it.copy(isSelected = it.id == wordPair.id)
            }
        } else {
            _selectedJapaneseWord.value = wordPair
            _japaneseWords.value = _japaneseWords.value.map {
                it.copy(isSelected = it.id == wordPair.id)
            }
        }
    }

    fun checkMatch() {
        val english = _selectedEnglishWord.value
        val japanese = _selectedJapaneseWord.value

        if (english != null && japanese != null) {
            val isCorrect = english.id == japanese.id
            _isCorrectMatch.value = isCorrect
            _showFeedback.value = true

            if (isCorrect) {
                _englishWords.value = _englishWords.value.map {
                    if (it.id == english.id) it.copy(isMatched = true, isSelected = false) else it
                }
                _japaneseWords.value = _japaneseWords.value.map {
                    if (it.id == japanese.id) it.copy(isMatched = true, isSelected = false) else it
                }
                _progress.value = _englishWords.value.count { it.isMatched }.toFloat() / _englishWords.value.size
            } else {
                _hearts.value = (_hearts.value - 1).coerceAtLeast(0)
            }

            _selectedEnglishWord.value = null
            _selectedJapaneseWord.value = null
        }
    }

    fun setAudioPlayingState(isPlaying: Boolean) {
        _isAudioPlaying.value = isPlaying
    }

    fun dismissFeedback() {
        _showFeedback.value = false
    }

    fun isGameCompleted(): Boolean {
        return _englishWords.value.all { it.isMatched }
    }
}