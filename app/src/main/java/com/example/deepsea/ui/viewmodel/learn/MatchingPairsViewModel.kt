package com.example.deepsea.ui.screens.feature.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class WordPair(
    val english: String,
    val japanese: String,
    val pronunciation: String,
    val isSelected: Boolean = false,
    val isMatched: Boolean = false
)

class MatchingPairsViewModel : ViewModel() {
    private val _englishWords = MutableStateFlow<List<WordPair>>(emptyList())
    val englishWords: StateFlow<List<WordPair>> = _englishWords

    private val _japaneseWords = MutableStateFlow<List<WordPair>>(emptyList())
    val japaneseWords: StateFlow<List<WordPair>> = _japaneseWords

    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private val _hearts = MutableStateFlow(3)
    val hearts: StateFlow<Int> = _hearts

    private val _showFeedback = MutableStateFlow(false)
    val showFeedback: StateFlow<Boolean> = _showFeedback

    private val _isCorrectMatch = MutableStateFlow(false)
    val isCorrectMatch: StateFlow<Boolean> = _isCorrectMatch

    private val _selectedEnglishWord = MutableStateFlow<WordPair?>(null)
    val selectedEnglishWord: StateFlow<WordPair?> = _selectedEnglishWord

    private val _selectedJapaneseWord = MutableStateFlow<WordPair?>(null)
    val selectedJapaneseWord: StateFlow<WordPair?> = _selectedJapaneseWord

    init {
        // Initialize with sample word pairs
        val initialPairs = listOf(
            WordPair("Hello", "こんにちは", "Konnichiwa"),
            WordPair("Thank you", "ありがとう", "Arigatou"),
            WordPair("Goodbye", "じゃあね", "Jaa ne"),
            WordPair("Please", "お願い", "Onegai")
        )
        _englishWords.value = initialPairs.shuffled()
        _japaneseWords.value = initialPairs.shuffled()
        updateProgress()
    }

    fun selectWord(isEnglish: Boolean, pair: WordPair) {
        viewModelScope.launch {
            if (isEnglish) {
                if (_selectedEnglishWord.value == pair) {
                    _selectedEnglishWord.value = null
                    _englishWords.value = _englishWords.value.map {
                        if (it == pair) it.copy(isSelected = false) else it
                    }
                } else {
                    _selectedEnglishWord.value = pair
                    _englishWords.value = _englishWords.value.map {
                        if (it == pair) it.copy(isSelected = true) else it.copy(isSelected = false)
                    }
                }
            } else {
                if (_selectedJapaneseWord.value == pair) {
                    _selectedJapaneseWord.value = null
                    _japaneseWords.value = _japaneseWords.value.map {
                        if (it == pair) it.copy(isSelected = false) else it
                    }
                } else {
                    _selectedJapaneseWord.value = pair
                    _japaneseWords.value = _japaneseWords.value.map {
                        if (it == pair) it.copy(isSelected = true) else it.copy(isSelected = false)
                    }
                }
            }
        }
    }

    fun checkMatch() {
        val englishWord = _selectedEnglishWord.value
        val japaneseWord = _selectedJapaneseWord.value

        if (englishWord != null && japaneseWord != null) {
            val isCorrect = englishWord.english == japaneseWord.english // Check if they belong to the same pair
            _showFeedback.value = true
            _isCorrectMatch.value = isCorrect

            if (isCorrect) {
                _englishWords.value = _englishWords.value.map {
                    if (it == englishWord) it.copy(isMatched = true, isSelected = false) else it
                }
                _japaneseWords.value = _japaneseWords.value.map {
                    if (it == japaneseWord) it.copy(isMatched = true, isSelected = false) else it
                }
                _selectedEnglishWord.value = null
                _selectedJapaneseWord.value = null
                updateProgress()
            } else {
                _hearts.value = (_hearts.value - 1).coerceAtLeast(0)
                _englishWords.value = _englishWords.value.map { it.copy(isSelected = false) }
                _japaneseWords.value = _japaneseWords.value.map { it.copy(isSelected = false) }
                _selectedEnglishWord.value = null
                _selectedJapaneseWord.value = null
            }
        }
    }

    fun dismissFeedback() {
        _showFeedback.value = false
    }

    fun setAudioPlayingState(isPlaying: Boolean) {
        _isAudioPlaying.value = isPlaying
    }

    fun isGameCompleted(): Boolean {
        return _englishWords.value.all { it.isMatched } && _japaneseWords.value.all { it.isMatched }
    }

    fun resetGame() {
        val initialPairs = _englishWords.value.map { it.copy(isSelected = false, isMatched = false) }
        _englishWords.value = initialPairs.shuffled()
        _japaneseWords.value = initialPairs.shuffled()
        _selectedEnglishWord.value = null
        _selectedJapaneseWord.value = null
        _progress.value = 0f
        _hearts.value = 3
        _showFeedback.value = false
    }

    private fun updateProgress() {
        val matchedCount = _englishWords.value.count { it.isMatched }
        _progress.value = if (_englishWords.value.isNotEmpty()) {
            matchedCount.toFloat() / _englishWords.value.size
        } else 0f
    }
}