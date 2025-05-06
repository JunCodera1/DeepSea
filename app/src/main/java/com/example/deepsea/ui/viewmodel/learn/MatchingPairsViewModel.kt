package com.example.deepsea.ui.screens.feature.learn

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.model.exercise.MatchingPair
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale



// ViewModel to manage game state
class MatchingPairsViewModel : ViewModel() {
    // Initial pairs data - updated to match screenshots
    private val pairsData = listOf(
        MatchingPair("1", "person", "ひと", "hi to"),
        MatchingPair("2", "bye", "さようなら", "sa yo u na ra"),
        MatchingPair("3", "cake", "ケーキ", "kee ki"),
        MatchingPair("4", "pizza", "ピザ", "pi za"),
        MatchingPair("5", "water", "みず", "mi zu"),
        MatchingPair("6", "japanese", "じゃあね", "ja a ne"),
        MatchingPair("7", "hello", "こんにちは", "ko n ni chi wa"),
        MatchingPair("8", "yes", "はい", "ha i"),
        MatchingPair("9", "ramen", "ラーメン", "raa me n")
    )

    // Game state
    private val _englishWords = MutableStateFlow<List<MatchingPair>>(emptyList())
    val englishWords: StateFlow<List<MatchingPair>> = _englishWords.asStateFlow()

    private val _japaneseWords = MutableStateFlow<List<MatchingPair>>(emptyList())
    val japaneseWords: StateFlow<List<MatchingPair>> = _japaneseWords.asStateFlow()

    private val _firstSelection = MutableStateFlow<MatchingPair?>(null)
    val firstSelection: StateFlow<MatchingPair?> = _firstSelection.asStateFlow()

    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    private val _showFeedback = MutableStateFlow(false)
    val showFeedback: StateFlow<Boolean> = _showFeedback.asStateFlow()

    private val _isCorrectMatch = MutableStateFlow(false)
    val isCorrectMatch: StateFlow<Boolean> = _isCorrectMatch.asStateFlow()

    private val _correctAnswerForFeedback = MutableStateFlow<String>("")
    val correctAnswerForFeedback: StateFlow<String> = _correctAnswerForFeedback.asStateFlow()

    private val _progress = MutableStateFlow(0.2f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _hearts = MutableStateFlow(2)
    val hearts: StateFlow<Int> = _hearts.asStateFlow()

    private val _matchedPairs = MutableStateFlow(0)
    private val totalPairs = pairsData.size / 2

    // Text-to-speech instance
    private var textToSpeech: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null

    init {
        resetGame()
    }

    fun initializeTTS(context: Context) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.JAPANESE
            }
        }
        mediaPlayer = MediaPlayer()
    }

    fun resetGame() {
        // Create copies of pairs for both columns
        val allPairs = pairsData.shuffled()
        val selectedPairs = allPairs.take(6)  // Take first 6 pairs  for the game

        _englishWords.value = selectedPairs.map { it.copy(isSelected = false, isMatched = false) }
        _japaneseWords.value = selectedPairs.shuffled().map { it.copy(isSelected = false, isMatched = false) }

        _firstSelection.value = null
        _showFeedback.value = false
        _matchedPairs.value = 0
        _progress.value = 0.2f
        _hearts.value = 2
    }

    fun selectWord(isEnglish: Boolean, selectedPair: MatchingPair) {
        // Skip if audio is playing or word is already matched
        if (_isAudioPlaying.value || selectedPair.isMatched) {
            return
        }

        // Play pronunciation when selecting Japanese words
        if (!isEnglish) {
            playPronunciation(selectedPair.pronunciation)
        }

        if (_firstSelection.value == null) {
            // First selection
            _firstSelection.value = selectedPair

            // Update selection state
            if (isEnglish) {
                _englishWords.value = _englishWords.value.map {
                    if (it.id == selectedPair.id) it.copy(isSelected = true) else it.copy(isSelected = false)
                }
            } else {
                _japaneseWords.value = _japaneseWords.value.map {
                    if (it.id == selectedPair.id) it.copy(isSelected = true) else it.copy(isSelected = false)
                }
            }
        } else {
            // Second selection - check match
            val firstSelectionId = _firstSelection.value!!.id
            val secondSelectionId = selectedPair.id

            // Update second selection appearance
            if (isEnglish) {
                _englishWords.value = _englishWords.value.map {
                    if (it.id == selectedPair.id) it.copy(isSelected = true) else it
                }
            } else {
                _japaneseWords.value = _japaneseWords.value.map {
                    if (it.id == selectedPair.id) it.copy(isSelected = true) else it
                }
            }

            // Check for match
            val isMatch = firstSelectionId == secondSelectionId

            // Delay to show the second selection before showing feedback
            viewModelScope.launch {
                delay(300)

                if (isMatch) {
                    // Handle correct match
                    _isCorrectMatch.value = true
                    _showFeedback.value = true
                    _matchedPairs.value += 1
                    _progress.value = 0.2f + (_matchedPairs.value.toFloat() / totalPairs.toFloat()) * 0.8f

                    // Play success sound
                    playSuccessSound()

                    // Mark words as matched
                    _englishWords.value = _englishWords.value.map {
                        if (it.id == firstSelectionId) it.copy(isMatched = true, isSelected = false) else it
                    }
                    _japaneseWords.value = _japaneseWords.value.map {
                        if (it.id == firstSelectionId) it.copy(isMatched = true, isSelected = false) else it
                    }
                } else {
                    // Handle incorrect match
                    _isCorrectMatch.value = false
                    _showFeedback.value = true
                    _hearts.value = (_hearts.value - 1).coerceAtLeast(0)

                    // Find the correct answer for feedback
                    _correctAnswerForFeedback.value = if (isEnglish) {
                        _japaneseWords.value.find { it.id == selectedPair.id }?.japanese ?: ""
                    } else {
                        _englishWords.value.find { it.id == selectedPair.id }?.english ?: ""
                    }
                }

                // Reset selections after showing feedback
                _firstSelection.value = null
            }
        }
    }

    fun dismissFeedback() {
        _showFeedback.value = false

        // Reset selection status
        _englishWords.value = _englishWords.value.map {
            if (!it.isMatched) it.copy(isSelected = false) else it
        }
        _japaneseWords.value = _japaneseWords.value.map {
            if (!it.isMatched) it.copy(isSelected = false) else it
        }
    }

    fun playPronunciation(pronunciation: String) {
        // Set audio playing state
        _isAudioPlaying.value = true

        // Speak the pronunciation
        textToSpeech?.speak(pronunciation, TextToSpeech.QUEUE_FLUSH, null, "pronunciation")

        // Simulate audio completion after delay
        viewModelScope.launch {
            delay(1000)
            _isAudioPlaying.value = false
        }
    }

    private fun playSuccessSound() {
        // In a real app, this would play a success sound effect
        // For now, we just simulate with a delay
        viewModelScope.launch {
            delay(300)
        }
    }

    fun setAudioPlayingState(isPlaying: Boolean) {
        _isAudioPlaying.value = isPlaying
    }

    fun isGameCompleted(): Boolean {
        return _matchedPairs.value == totalPairs
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        mediaPlayer?.release()
    }
}