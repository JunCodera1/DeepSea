package com.example.deepsea.ui.viewmodel.learn

import android.app.Application
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.exercise.TranslationExercise
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

class WordBuildingViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentExercise = MutableStateFlow(
        TranslationExercise(
            id = "1",
            sourceText = "That's green tea",
            targetText = "それはお茶です。",
            sourceLanguage = "en",
            targetLanguage = "ja",
            wordOptions = listOf("それは", "お", "茶", "です", "。", "緑", "飲み物", "コーヒー")
        )
    )
    val currentExercise: StateFlow<TranslationExercise> = _currentExercise.asStateFlow()

    private val _userProgress = MutableStateFlow(0.3f) // 30% progress
    val userProgress: StateFlow<Float> = _userProgress.asStateFlow()

    private val _hearts = MutableStateFlow(2)
    val hearts: StateFlow<Int> = _hearts.asStateFlow()

    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    private val _selectedWords = MutableStateFlow<List<String>>(emptyList())
    val selectedWords: StateFlow<List<String>> = _selectedWords.asStateFlow()

    private val _isAnswerCorrect = MutableStateFlow<Boolean?>(null)
    val isAnswerCorrect: StateFlow<Boolean?> = _isAnswerCorrect.asStateFlow()

    private val apiService = RetrofitClient.authApi

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
                viewModelScope.launch {
                    _isAudioPlaying.value = true
                }
            }

            override fun onDone(utteranceId: String) {
                viewModelScope.launch {
                    _isAudioPlaying.value = false
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String) {
                viewModelScope.launch {
                    _isAudioPlaying.value = false
                }
            }

            // Add this for newer Android versions
            override fun onError(utteranceId: String, errorCode: Int) {
                super.onError(utteranceId, errorCode)
                viewModelScope.launch {
                    _isAudioPlaying.value = false
                }
            }
        })
    }

    fun loadExercise() {
        viewModelScope.launch {
            try {
                val response = apiService.getTranslationExercise()
                if (response.isSuccessful && response.body() != null) {
                    _currentExercise.value = response.body()!!
                    _selectedWords.value = emptyList()
                    _isAnswerCorrect.value = null
                }
            } catch (e: Exception) {
                // Handle network errors
                Log.e("ViewModel", "Error loading exercise: ${e.message}")
                // Use default exercise (already set in init)
            }
        }
    }

    /**
     * Add a word to the current selection
     */
    fun addWord(word: String) {
        val currentSelection = _selectedWords.value.toMutableList()
        currentSelection.add(word)
        _selectedWords.value = currentSelection

        // Play the word pronunciation
        playWordAudio(word)
    }

    /**
     * Remove a word from the current selection
     */
    fun removeWord(index: Int) {
        val currentSelection = _selectedWords.value.toMutableList()
        if (index in currentSelection.indices) {
            val wordToRemove = currentSelection[index]
            currentSelection.removeAt(index)
            _selectedWords.value = currentSelection

            // Play the word pronunciation when removed
            playWordAudio(wordToRemove)
        }
    }

    /**
     * Clear all selected words
     */
    fun clearSelection() {
        _selectedWords.value = emptyList()
    }

    /**
     * Play audio for the complete sentence
     */
    fun playSentenceAudio() {
        if (ttsInitialized && !_isAudioPlaying.value) {
            textToSpeech?.setSpeechRate(speechRate)
            val utteranceId = UUID.randomUUID().toString()
            textToSpeech?.speak(
                _currentExercise.value.targetText,
                TextToSpeech.QUEUE_FLUSH,
                null,
                utteranceId
            )
        }
    }

    /**
     * Play the slow version of the sentence audio
     */
    fun playSlowSentenceAudio() {
        if (ttsInitialized && !_isAudioPlaying.value) {
            textToSpeech?.setSpeechRate(slowSpeechRate)
            val utteranceId = UUID.randomUUID().toString()
            textToSpeech?.speak(
                _currentExercise.value.targetText,
                TextToSpeech.QUEUE_FLUSH,
                null,
                utteranceId
            )
        }
    }

    /**
     * Play audio for a specific word
     */
    fun playWordAudio(word: String) {
        if (ttsInitialized && !_isAudioPlaying.value) {
            textToSpeech?.setSpeechRate(speechRate)
            val utteranceId = UUID.randomUUID().toString()
            textToSpeech?.speak(
                word,
                TextToSpeech.QUEUE_FLUSH,
                null,
                utteranceId
            )
        }
    }

    /**
     * Check if the user's answer is correct
     */
    fun checkAnswer() {
        val selectedSentence = _selectedWords.value.joinToString("")
        val correctAnswer = _currentExercise.value.targetText

        // Compare with the expected answer
        val isCorrect = selectedSentence == correctAnswer
        _isAnswerCorrect.value = isCorrect

        if (isCorrect) {
            // Increase progress
            _userProgress.value += 0.1f

            // Add a delay before loading next exercise
            viewModelScope.launch {
                delay(1500) // Show success feedback for 1.5 seconds
                loadNextExercise()
            }
        } else {
            // Decrease hearts for wrong answer
            _hearts.value -= 1
        }
    }

    /**
     * Load the next exercise
     */
    private fun loadNextExercise() {
        _selectedWords.value = emptyList()
        _isAnswerCorrect.value = null
        loadExercise()
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}