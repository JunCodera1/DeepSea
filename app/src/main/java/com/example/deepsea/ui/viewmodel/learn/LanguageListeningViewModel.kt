package com.example.deepsea.ui.viewmodel.learn

import android.app.Application
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.exercise.HearingExercise
import com.example.deepsea.utils.AudioPlaybackManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import java.util.UUID

class LanguageListeningViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentExercise = MutableStateFlow<HearingExercise?>(null)
    val currentExercise: StateFlow<HearingExercise?> = _currentExercise.asStateFlow()

    private val _userProgress = MutableStateFlow(0.2f) // 20% progress
    val userProgress: StateFlow<Float> = _userProgress.asStateFlow()

    private val _hearts = MutableStateFlow(5)
    val hearts: StateFlow<Int> = _hearts.asStateFlow()

    private val _selectedOption = MutableStateFlow<String?>(null)
    val selectedOption: StateFlow<String?> = _selectedOption.asStateFlow()

    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    private val _isSpellingPlaying = MutableStateFlow(false)
    val isSpellingPlaying: StateFlow<Boolean> = _isSpellingPlaying.asStateFlow()

    private val _isAnswerCorrect = MutableStateFlow<Boolean?>(null)
    val isAnswerCorrect: StateFlow<Boolean?> = _isAnswerCorrect.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val apiService = RetrofitClient.learningApiService
    private val audioPlaybackManager = AudioPlaybackManager(application.applicationContext)

    // Text-to-Speech variables
    private var textToSpeech: TextToSpeech? = null
    private var ttsInitialized = false
    private val speechRate = 1.0f // Normal speed
    private val slowSpeechRate = 0.7f // Slower speed for turtle mode

    init {
        initTextToSpeech()
        fetchRandomExercise(sectionId = 1, unitId = 1) // Start with Section 1, Unit 1
    }

    /**
     * Initialize the Text-to-Speech engine
     */
    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(getApplication()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.JAPANESE)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Japanese language not supported, falling back to default")
                    textToSpeech?.setLanguage(Locale.US)
                }
                ttsInitialized = true
                setupTTSListener()
            } else {
                Log.e("TTS", "Initialization failed")
            }
        }
    }

    /**
     * Setup TTS utterance progress listener to track speech progress
     */
    private fun setupTTSListener() {
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {
                viewModelScope.launch {
                    if (utteranceId.startsWith("spelling_")) {
                        _isSpellingPlaying.value = true
                    } else {
                        _isAudioPlaying.value = true
                    }
                }
            }

            override fun onDone(utteranceId: String) {
                viewModelScope.launch {
                    if (utteranceId.startsWith("spelling_")) {
                        _isSpellingPlaying.value = false
                    } else {
                        _isAudioPlaying.value = false
                    }
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String) {
                viewModelScope.launch {
                    _isAudioPlaying.value = false
                    _isSpellingPlaying.value = false
                }
            }

            override fun onError(utteranceId: String, errorCode: Int) {
                super.onError(utteranceId, errorCode)
                viewModelScope.launch {
                    _isAudioPlaying.value = false
                    _isSpellingPlaying.value = false
                }
            }
        })
    }

    /**
     * Fetch a random exercise for the given section and unit
     */
    fun fetchRandomExercise(sectionId: Long, unitId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val exercise = apiService.getRandomExercise(sectionId, unitId)
                _currentExercise.value = exercise
                _selectedOption.value = null
                _isAnswerCorrect.value = null
            } catch (e: IOException) {
                _errorMessage.value = "Network error. Please check your connection."
                Log.e("ViewModel", "Network error: ${e.message}")
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load exercise. Please try again."
                Log.e("ViewModel", "Error loading exercise: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Play the correct answer using TTS at normal speed
     */
    fun playAudio() {
        if (ttsInitialized && !_isAudioPlaying.value) {
            textToSpeech?.setSpeechRate(speechRate)
            val utteranceId = UUID.randomUUID().toString()
            textToSpeech?.speak(
                _currentExercise.value?.correctAnswer,
                TextToSpeech.QUEUE_FLUSH,
                null,
                utteranceId
            )
        }
    }

    /**
     * Play the correct answer at a slower speed
     */
    fun playSlowAudio() {
        if (ttsInitialized && !_isAudioPlaying.value) {
            textToSpeech?.setSpeechRate(slowSpeechRate)
            val utteranceId = UUID.randomUUID().toString()
            textToSpeech?.speak(
                _currentExercise.value?.correctAnswer,
                TextToSpeech.QUEUE_FLUSH,
                null,
                utteranceId
            )
        }
    }

    /**
     * Play audio from the exercise's audio URL
     */
    fun playExerciseAudio() {
        val audioUrl = _currentExercise.value?.audio ?: return
        viewModelScope.launch {
            _isAudioPlaying.value = true
            try {
                audioPlaybackManager.playAudioFromUrl(audioUrl)
            } catch (e: Exception) {
                Log.e("ViewModel", "Error playing audio: ${e.message}")
                // Fallback to TTS if audio URL fails
                playAudio()
            } finally {
                _isAudioPlaying.value = false
            }
        }
    }

    /**
     * Play word pronunciation using TTS (fallback since no API for spelling)
     */
    fun playWordSpelling(word: String) {
        if (ttsInitialized && !_isSpellingPlaying.value) {
            viewModelScope.launch {
                _isSpellingPlaying.value = true
                try {
                    textToSpeech?.setSpeechRate(speechRate)
                    val utteranceId = "spelling_${UUID.randomUUID()}"
                    // Split word into characters for Japanese (optional for better pronunciation)
                    word.forEach { char ->
                        textToSpeech?.speak(
                            char.toString(),
                            TextToSpeech.QUEUE_ADD,
                            null,
                            utteranceId
                        )
                        delay(800) // Pause between characters
                    }
                    // Play whole word
                    textToSpeech?.speak(
                        word,
                        TextToSpeech.QUEUE_ADD,
                        null,
                        utteranceId
                    )
                } catch (e: Exception) {
                    Log.e("ViewModel", "Error playing spelling: ${e.message}")
                } finally {
                    // TTS listener handles _isSpellingPlaying update
                }
            }
        }
    }

    /**
     * Select an answer option
     */
    fun checkAnswer(answer: String) {
        _selectedOption.value = answer
        playWordSpelling(answer)
    }

    /**
     * Check if the selected answer is correct and update progress
     */
    fun checkExercise() {
        val currentExercise = _currentExercise.value ?: return
        val currentAnswer = _selectedOption.value ?: return
        if (currentAnswer == currentExercise.correctAnswer) {
            // Correct answer
            _isAnswerCorrect.value = true
            _userProgress.value = (_userProgress.value + 0.1f).coerceAtMost(1f)
            viewModelScope.launch {
                delay(1500) // Show success feedback for 1.5 seconds
                loadNextExercise()
            }
        } else {
            // Wrong answer
            _isAnswerCorrect.value = false
            _hearts.value = (_hearts.value - 1).coerceAtLeast(0)
        }
    }

    /**
     * Load the next exercise (same section and unit for simplicity)
     */
    private fun loadNextExercise() {
        _selectedOption.value = null
        _isAnswerCorrect.value = null
        // Fetch another exercise from the same section/unit (adjust as needed)
        val currentSectionId = 1L // Placeholder; track actual sectionId
        val currentUnitId = 1L   // Placeholder; track actual unitId
        fetchRandomExercise(currentSectionId, currentUnitId)
    }

    override fun onCleared() {
        super.onCleared()
        audioPlaybackManager.releaseMediaPlayer()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}