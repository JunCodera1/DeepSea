package com.example.deepsea.ui.viewmodel.learn

import android.app.Application
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.WordBuildingService
import com.example.deepsea.data.model.exercise.TranslationExercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

class WordBuildingViewModel(
    private val apiService: WordBuildingService,
    application: Application
) : AndroidViewModel(application) {

    private val _currentExercise = MutableStateFlow<TranslationExercise?>(null)
    val currentExercise: StateFlow<TranslationExercise?> = _currentExercise.asStateFlow()

    private val _userProgress = MutableStateFlow(0.3f)
    val userProgress: StateFlow<Float> = _userProgress.asStateFlow()

    private val _hearts = MutableStateFlow(2)
    val hearts: StateFlow<Int> = _hearts.asStateFlow()

    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    private val _selectedWords = MutableStateFlow<List<String>>(emptyList())
    val selectedWords: StateFlow<List<String>> = _selectedWords.asStateFlow()

    private val _isAnswerCorrect = MutableStateFlow<Boolean?>(null)
    val isAnswerCorrect: StateFlow<Boolean?> = _isAnswerCorrect.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var textToSpeech: TextToSpeech? = null
    private var ttsInitialized = false
    private val speechRate = 1.0f
    private val slowSpeechRate = 0.7f

    init {
        initTextToSpeech()
    }

    fun resetState() {
        _selectedWords.value = emptyList()
        _isAnswerCorrect.value = null
        _currentExercise.value = null
        _errorMessage.value = null
    }

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
                loadExercise() // Load exercise after TTS is initialized
            } else {
                Log.e("TTS", "Initialization failed")
                _errorMessage.value = "Failed to initialize TTS"
            }
        }
    }

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

            override fun onError(utteranceId: String, errorCode: Int) {
                viewModelScope.launch {
                    _isAudioPlaying.value = false
                }
            }
        })
    }

    fun loadExercise() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                Log.d("ViewModel", "Attempting to load exercise from API")
                val response = apiService.getTranslationExercise()
                _currentExercise.value = response
                _selectedWords.value = emptyList()
                _isAnswerCorrect.value = null
                Log.d("ViewModel", "Successfully loaded exercise: ${response.sourceText}")
            } catch (e: Exception) {
                Log.e("ViewModel", "Error loading exercise: ${e.message}")
                _errorMessage.value = "Failed to load exercise: ${e.message}"
                _currentExercise.value = createFallbackExercise()
                Log.d("ViewModel", "Fallback exercise loaded: ${createFallbackExercise().sourceText}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun createFallbackExercise(): TranslationExercise {
        return TranslationExercise(
            id = "fallback_${System.currentTimeMillis()}",
            sourceText = "That's green tea",
            targetText = "それはお茶です。",
            sourceLanguage = "en",
            targetLanguage = "ja",
            wordOptions = listOf("それは", "お", "茶", "です", "。", "緑", "飲み物", "コーヒー")
        )
    }

    fun addWord(word: String) {
        val currentSelection = _selectedWords.value.toMutableList()
        currentSelection.add(word)
        _selectedWords.value = currentSelection
        playWordAudio(word)
    }

    fun removeWord(index: Int) {
        val currentSelection = _selectedWords.value.toMutableList()
        if (index in currentSelection.indices) {
            val wordToRemove = currentSelection[index]
            currentSelection.removeAt(index)
            _selectedWords.value = currentSelection
            playWordAudio(wordToRemove)
        }
    }

    fun clearSelection() {
        _selectedWords.value = emptyList()
    }

    fun playSentenceAudio() {
        if (ttsInitialized && !_isAudioPlaying.value) {
            textToSpeech?.setSpeechRate(speechRate)
            val utteranceId = UUID.randomUUID().toString()
            textToSpeech?.speak(
                _currentExercise.value?.targetText ?: "",
                TextToSpeech.QUEUE_FLUSH,
                null,
                utteranceId
            )
        }
    }

    fun playSlowSentenceAudio() {
        if (ttsInitialized && !_isAudioPlaying.value) {
            textToSpeech?.setSpeechRate(slowSpeechRate)
            val utteranceId = UUID.randomUUID().toString()
            textToSpeech?.speak(
                _currentExercise.value?.targetText ?: "",
                TextToSpeech.QUEUE_FLUSH,
                null,
                utteranceId
            )
        }
    }

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

    fun checkAnswer() {
        val selectedSentence = _selectedWords.value.joinToString("")
        val correctAnswer = _currentExercise.value?.targetText ?: ""
        val isCorrect = selectedSentence == correctAnswer
        _isAnswerCorrect.value = isCorrect

        if (isCorrect) {
            _userProgress.value = (_userProgress.value + 0.1f).coerceAtMost(1f)
        } else {
            _hearts.value = (_hearts.value - 1).coerceAtLeast(0)
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}