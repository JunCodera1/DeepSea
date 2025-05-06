package com.example.deepsea.ui.viewmodel.learn

import android.app.Application
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.audio.HearingExercise
import com.example.deepsea.utils.AudioPlaybackManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

class LanguageListeningViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentExercise = MutableStateFlow(
        HearingExercise(
            id = "1",
            audio = "audio_url",
            correctAnswer = "ください",
            options = listOf("ください", "おちゃ", "ごはん", "と")
        )
    )
    val currentExercise: StateFlow<HearingExercise> = _currentExercise.asStateFlow()

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

    private var mediaPlayer: MediaPlayer? = null
    private val apiService = RetrofitClient.authApi
    private val audioPlaybackManager = AudioPlaybackManager(application.applicationContext)

    // Text-to-Speech variables
    private var textToSpeech: TextToSpeech? = null
    private var ttsInitialized = false
    private val speechRate = 1.0f // Normal speed
    private val slowSpeechRate = 0.7f // Slower speed for turtle mode

    init {
        loadExercise()
        initTextToSpeech()
    }

    /**
     * Initialize the Text-to-Speech engine
     */
    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(getApplication()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // For Japanese language, use Locale.JAPANESE
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
                if (utteranceId.startsWith("spelling_")) {
                    viewModelScope.launch {
                        _isSpellingPlaying.value = true
                    }
                } else {
                    viewModelScope.launch {
                        _isAudioPlaying.value = true
                    }
                }
            }

            override fun onDone(utteranceId: String) {
                if (utteranceId.startsWith("spelling_")) {
                    viewModelScope.launch {
                        _isSpellingPlaying.value = false
                    }
                } else {
                    viewModelScope.launch {
                        _isAudioPlaying.value = false
                    }
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String) {
                // Handle errors
                viewModelScope.launch {
                    _isAudioPlaying.value = false
                    _isSpellingPlaying.value = false
                }
            }

            // Add this for newer Android versions
            override fun onError(utteranceId: String, errorCode: Int) {
                super.onError(utteranceId, errorCode)
                viewModelScope.launch {
                    _isAudioPlaying.value = false
                    _isSpellingPlaying.value = false
                }
            }
        })
    }

    fun loadExercise() {
        viewModelScope.launch {
            try {
                val response = apiService.getExercise()
                if (response.isSuccessful && response.body() != null) {
                    _currentExercise.value = response.body()!!
                    _selectedOption.value = null
                    _isAnswerCorrect.value = null
                }
            } catch (e: Exception) {
                // Handle network errors
                Log.e("ViewModel", "Error loading exercise: ${e.message}")
                e.printStackTrace()
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
                _currentExercise.value.correctAnswer,
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
                _currentExercise.value.correctAnswer,
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
        viewModelScope.launch {
            _isAudioPlaying.value = true
            try {
                audioPlaybackManager.playAudioFromUrl(_currentExercise.value.audio)
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
     * Play word pronunciation character by character
     */
    fun playWordSpelling(word: String) {
        viewModelScope.launch {
            _isSpellingPlaying.value = true

            try {
                // Try to get audio from API
                val response = apiService.getWordAudio(word)
                if (response.isSuccessful && response.body() != null) {
                    val audioResponse = response.body()!!

                    // Get spelling information
                    val spellingResponse = apiService.getSpelling(word)
                    if (spellingResponse.isSuccessful && spellingResponse.body() != null) {
                        val spelling = spellingResponse.body()!!

                        // Play each character
                        for (audioUrl in spelling.audioUrls) {
                            audioPlaybackManager.playAudioFromUrl(audioUrl)
                            delay(800) // Wait between syllables
                        }

                        // Play the whole word
                        delay(500)
                        audioPlaybackManager.playAudioFromUrl(audioResponse.audioUrl)
                    }
                } else {
                    // Fallback to TTS for spelling if API fails
                    playWordWithTTS(word)
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error playing spelling: ${e.message}")
                e.printStackTrace()
                // Fallback to TTS
                playWordWithTTS(word)
            } finally {
                _isSpellingPlaying.value = false
            }
        }
    }

    /**
     * Fallback method to play word using TTS
     */
    private fun playWordWithTTS(word: String) {
        if (ttsInitialized) {
            textToSpeech?.setSpeechRate(speechRate)
            val utteranceId = "spelling_${UUID.randomUUID()}"
            textToSpeech?.speak(
                word,
                TextToSpeech.QUEUE_FLUSH,
                null,
                utteranceId
            )
        }
    }

    /**
     * Select an answer option
     */
    fun checkAnswer(answer: String) {
        _selectedOption.value = answer

        // Play the selected word pronunciation
        playWordSpelling(answer)
    }

    /**
     * Check if the selected answer is correct and update progress
     */
    fun checkExercise() {
        val currentAnswer = _selectedOption.value
        if (currentAnswer == currentExercise.value.correctAnswer) {
            // Correct answer
            _isAnswerCorrect.value = true
            _userProgress.value += 0.1f // Increase progress

            // Add a delay before loading next exercise
            viewModelScope.launch {
                delay(1500) // Show success feedback for 1.5 seconds
                loadNextExercise()
            }
        } else {
            // Wrong answer
            _isAnswerCorrect.value = false
            _hearts.value -= 1
        }
    }

    /**
     * Load the next exercise
     */
    private fun loadNextExercise() {
        _selectedOption.value = null
        _isAnswerCorrect.value = null
        loadExercise()
    }

    override fun onCleared() {
        super.onCleared()
        audioPlaybackManager.releaseMediaPlayer()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}