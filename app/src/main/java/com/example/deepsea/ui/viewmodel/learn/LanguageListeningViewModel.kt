package com.example.deepsea.ui.viewmodel.learn

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.HearingService
import com.example.deepsea.data.model.exercise.HearingExercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class LanguageListeningViewModel(
    private val apiService: HearingService,
    private val context: Context
) : ViewModel() {

    private val _currentExercise = MutableStateFlow<HearingExercise?>(null)
    val currentExercise: StateFlow<HearingExercise?> = _currentExercise

    private val _userProgress = MutableStateFlow(0f)
    val userProgress: StateFlow<Float> = _userProgress

    private val _hearts = MutableStateFlow(3)
    val hearts: StateFlow<Int> = _hearts

    private val _isTtsPlaying = MutableStateFlow(false)
    val isTtsPlaying: StateFlow<Boolean> = _isTtsPlaying

    private val _selectedOption = MutableStateFlow<String?>(null)
    val selectedOption: StateFlow<String?> = _selectedOption

    private val _isAnswerCorrect = MutableStateFlow<Boolean?>(null)
    val isAnswerCorrect: StateFlow<Boolean?> = _isAnswerCorrect

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private var tts: TextToSpeech? = null

    init {
        initializeTts()
    }

    fun initializeTts() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.JAPAN
            } else {
                _errorMessage.value = "Failed to initialize TTS"
            }
        }
    }

    fun fetchRandomExercise(sectionId: Long, unitId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val exerciseDto = apiService.getRandomHearingExercise(unitId)
                _currentExercise.value = HearingExercise(
                    id = exerciseDto.id,
                    correctAnswer = exerciseDto.correctAnswer,
                    options = exerciseDto.options
                )
                _userProgress.value = 0f
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load exercise: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun playTts(text: String) {
        if (_isTtsPlaying.value) return
        _isTtsPlaying.value = true
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        viewModelScope.launch {
            while (tts?.isSpeaking == true) {
                kotlinx.coroutines.delay(100)
            }
            _isTtsPlaying.value = false
        }
    }

    fun checkAnswer(option: String) {
        _selectedOption.value = option
    }

    fun checkExercise() {
        val selected = _selectedOption.value
        val correctAnswer = _currentExercise.value?.correctAnswer
        if (selected != null && correctAnswer != null) {
            _isAnswerCorrect.value = selected == correctAnswer
            if (_isAnswerCorrect.value == true) {
                _userProgress.value = 1f // Since only 1 exercise, progress is 100% on correct answer
            } else {
                _hearts.value = (_hearts.value - 1).coerceAtLeast(0)
            }
        }
    }

    fun onExerciseCompleted() {
        _selectedOption.value = null
        _isAnswerCorrect.value = null
    }

    override fun onCleared() {
        tts?.stop()
        tts?.shutdown()
        super.onCleared()
    }

    // Factory to create instances of LanguageListeningViewModel
    class Factory(
        private val apiService: HearingService,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LanguageListeningViewModel::class.java)) {
                return LanguageListeningViewModel(apiService, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}