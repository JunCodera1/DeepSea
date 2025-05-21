package com.example.deepsea.ui.viewmodel.learn

import android.app.Application
import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.WordBuildingService
import com.example.deepsea.data.model.exercise.TranslationExercise
import com.example.deepsea.data.repository.MistakeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import java.util.UUID

class WordBuildingViewModel(
    private val apiService: WordBuildingService,
    private val mistakeRepository: MistakeRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _currentExercise = MutableStateFlow<TranslationExercise?>(null)
    val currentExercise: StateFlow<TranslationExercise?> = _currentExercise.asStateFlow()

    private val _userProgress = MutableStateFlow(0f)
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

    // Thêm để theo dõi độ chính xác
    private val _correctAnswers = MutableStateFlow(0)
    private val _totalQuestions = MutableStateFlow(0)
    private val _accuracy = MutableStateFlow(0f)
    val accuracy: StateFlow<Float> = _accuracy.asStateFlow()

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
                    Timber.tag("TTS").e("Japanese language not supported, falling back to default")
                    textToSpeech?.setLanguage(Locale.US)
                }
                ttsInitialized = true
                setupTTSListener()
                loadExercise()
            } else {
                Timber.tag("TTS").e("Initialization failed")
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
                Timber.tag("WordBuildingViewModel").d("Attempting to load exercise from API")
                val response = apiService.getTranslationExercise()
                _currentExercise.value = response
                _selectedWords.value = emptyList()
                _isAnswerCorrect.value = null
                _totalQuestions.value += 1 // Tăng số câu hỏi
                Timber.tag("WordBuildingViewModel")
                    .d("Successfully loaded exercise: ${response.sourceText}, Total questions: ${_totalQuestions.value}")
            } catch (e: Exception) {
                Timber.tag("WordBuildingViewModel").e("Error loading exercise: ${e.message}")
                _errorMessage.value = "Failed to load exercise: ${e.message}"
                _currentExercise.value = createFallbackExercise()
                _totalQuestions.value += 1
                Timber.tag("WordBuildingViewModel")
                    .d("Fallback exercise loaded: ${createFallbackExercise().sourceText}")
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
        Timber.tag("WordBuildingViewModel")
            .d("Checking answer: selected='$selectedSentence', correct='$correctAnswer', isCorrect=$isCorrect")

        if (isCorrect) {
            _correctAnswers.value += 1
            _userProgress.value = _correctAnswers.value.toFloat() / _totalQuestions.value
            Timber.tag("WordBuildingViewModel")
                .d("Answer correct, Correct answers: ${_correctAnswers.value}, Progress: ${_userProgress.value}")
        } else {
            _hearts.value = (_hearts.value - 1).coerceAtLeast(0)
            viewModelScope.launch {
                try {
                    val sharedPreferences = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    val userId = sharedPreferences.getLong("user_id", 1L)
                    mistakeRepository.saveMistake(
                        userId = userId,
                        word = _currentExercise.value?.sourceText ?: "",
                        correctAnswer = correctAnswer,
                        userAnswer = selectedSentence,
                        lessonId = null
                    )
                    Timber.tag("WordBuildingViewModel").d("Mistake saved for userId: $userId")
                } catch (e: Exception) {
                    Timber.tag("WordBuildingViewModel").e("Failed to save mistake: ${e.message}")
                    _errorMessage.value = "Failed to save mistake: ${e.message}"
                }
            }
        }

        // Cập nhật độ chính xác
        _accuracy.value = if (_totalQuestions.value > 0) {
            (_correctAnswers.value.toFloat() / _totalQuestions.value) * 100
        } else {
            0f
        }
        Timber.tag("WordBuildingViewModel").d("Accuracy updated: ${_accuracy.value}%")
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}