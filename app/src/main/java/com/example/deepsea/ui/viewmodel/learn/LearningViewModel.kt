package com.example.deepsea.ui.viewmodel.learn

import android.app.Application
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.question.QuizQuestion
import com.example.deepsea.data.model.user.User
import com.example.deepsea.data.repository.MistakeRepository
import com.example.deepsea.data.repository.QuestionFactoryImpl
import com.example.deepsea.data.repository.UserProfileRepository
import com.example.deepsea.data.repository.VocabularyRepository
import com.example.deepsea.utils.JsonLogProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import java.util.UUID

data class VocabularyItem(
    val id: Long,
    val native: String,  // Japanese word
    val romaji: String,  // Romanized pronunciation
    val english: String, // English translation
    val imageResId: Int  // Resource ID for the image
)

class LearningViewModel(
    private val repository: VocabularyRepository,
    private val lessonId: Long = 1,
    private val userProfileRepository: UserProfileRepository,
    private val mistakeRepository: MistakeRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _currentWord = MutableStateFlow<VocabularyItem?>(null)
    val currentWord: StateFlow<VocabularyItem?> = _currentWord

    private val _options = MutableStateFlow<List<VocabularyItem>>(emptyList())
    val options: StateFlow<List<VocabularyItem>> = _options

    private val _hearts = MutableStateFlow(5)
    var hearts: StateFlow<Int> = _hearts

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isAnswerCorrect = MutableStateFlow<Boolean?>(null)
    val isAnswerCorrect: StateFlow<Boolean?> = _isAnswerCorrect

    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    private var textToSpeech: TextToSpeech? = null
    private var ttsInitialized = false

    private val _question = MutableStateFlow<QuizQuestion?>(null)
    val question: StateFlow<QuizQuestion?> = _question

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val vocabularyItems = mutableListOf<VocabularyItem>()
    private var currentIndex = 0
    private var totalWords = 10

    // Current user
    private val _currentUser = MutableStateFlow<User?>(null)

    // Thêm thuộc tính và functions mới để lưu trữ lỗi
    private val _isSavingMistake = MutableStateFlow(false)
    val isSavingMistake: StateFlow<Boolean> = _isSavingMistake.asStateFlow()

    private val _mistakeSaved = MutableStateFlow<Boolean?>(null)
    val mistakeSaved: StateFlow<Boolean?> = _mistakeSaved.asStateFlow()

    init {
        initTextToSpeech()
        loadLesson()
        loadCurrentUser() // Add this function to load the current user
    }

    // Add this function to load the current user
    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                userProfileRepository.getUserProfile(1) // Replace with actual user ID source
                    .onSuccess { profileData ->
                        _currentUser.value = User(
                            id = 1, // Replace with actual user ID source
                            username = profileData.username,
                            profileData = profileData
                        )
                    }
            } catch (e: Exception) {
                Timber.e(e, "Error loading current user")
            }
        }
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
            } else {
                Log.e("TTS", "Initialization failed")
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

    fun playWordAudio(word: String) {
        if (ttsInitialized && !_isAudioPlaying.value) {
            textToSpeech?.setSpeechRate(1.0f)
            val utteranceId = UUID.randomUUID().toString()
            textToSpeech?.speak(
                word,
                TextToSpeech.QUEUE_FLUSH,
                null,
                utteranceId
            )
        }
    }

    private fun loadLesson() {
        viewModelScope.launch {
            try {
                repository.getLessonVocabularyItems(lessonId).collect { words ->
                    processVocabularyItems(words)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading lesson from API, trying hardcoded JSON")
                val words = JsonLogProcessor.processHardcodedLogResponse()
                processVocabularyItems(words)
            }
        }
    }

    private fun processVocabularyItems(words: List<VocabularyItem>) {
        vocabularyItems.clear()
        vocabularyItems.addAll(words.shuffled())
        totalWords = vocabularyItems.size
        if (vocabularyItems.isNotEmpty()) {
            loadNextWord()
        }
        _isLoading.value = false
    }

    fun processRawJson(jsonString: String) {
        viewModelScope.launch {
            try {
                val words = repository.processRawQuizResponse(jsonString)
                processVocabularyItems(words)
            } catch (e: Exception) {
                Timber.e(e, "Error processing raw JSON")
                _isLoading.value = false
            }
        }
    }

    fun loadNextWord() {
        viewModelScope.launch {
            if (currentIndex < vocabularyItems.size) {
                _currentWord.value = vocabularyItems[currentIndex]
                currentIndex++
                updateProgress()
                loadOptions()
            } else {
                Timber.d("End of lesson reached")
            }
        }
    }

    private fun loadOptions() {
        viewModelScope.launch {
            try {
                if (vocabularyItems.isEmpty()) {
                    return@launch
                }
                val currentVocab = _currentWord.value ?: return@launch
                val allOptions = mutableListOf<VocabularyItem>()
                allOptions.add(currentVocab)
                val otherOptions = vocabularyItems
                    .filter { it.id != currentVocab.id }
                    .shuffled()
                    .take(3.coerceAtMost(vocabularyItems.size - 1))
                allOptions.addAll(otherOptions)
                if (allOptions.size < 4) {
                    val additionalOptions = repository.getVocabularyOptions(4 - allOptions.size)
                    allOptions.addAll(additionalOptions)
                }
                _options.value = allOptions.shuffled()
            } catch (e: Exception) {
                Timber.e(e, "Error loading options: ${e.message}")
                val currentWord = _currentWord.value
                if (currentWord != null) {
                    _options.value = listOf(currentWord)
                }
            }
        }
    }

    fun isAnswerCorrect(selectedOption: String): Boolean {
        return selectedOption == currentWord.value?.english
    }

    fun checkAnswer(selectedOption: String) {
        val current = currentWord.value ?: return
        if (isAnswerCorrect(selectedOption)) {
            _isAnswerCorrect.value = true
        } else {
            saveUserMistake(current.native, current.english, selectedOption)
            _isAnswerCorrect.value = false
            decreaseHearts()
        }
    }

    fun loadQuestionById(id: Long) {
        viewModelScope.launch {
            try {
                val result = repository.getQuestionById(id)
                _question.value = result
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.localizedMessage}"
            }
        }
    }

    private fun saveUserMistake(word: String, correctAnswer: String, userAnswer: String) {
        viewModelScope.launch {
            try {
                _isSavingMistake.value = true

                val currentUser = _currentUser.value
                if (currentUser != null) {
                    mistakeRepository.saveMistake(
                        userId = currentUser.id,
                        word = word,
                        correctAnswer = correctAnswer,
                        userAnswer = userAnswer,
                        lessonId = lessonId
                    )
                    _mistakeSaved.value = true
                } else {
                    _mistakeSaved.value = false
                }
            } catch (e: Exception) {
                _mistakeSaved.value = false
            } finally {
                _isSavingMistake.value = false
            }
        }
    }

    fun loadRandomQuestion() {
        viewModelScope.launch {
            try {
                val result = repository.getRandomQuestion()
                _question.value = result
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.localizedMessage}"
            }
        }
    }

    fun resetAnswerState() {
        _isAnswerCorrect.value = null
    }

    private fun decreaseHearts() {
        if (_hearts.value > 0) {
            _hearts.value -= 1
        }
        if (_hearts.value <= 0) {
            Timber.d("Game over - no hearts remaining")
        }
    }

    private fun updateProgress() {
        _progress.value = currentIndex.toFloat() / totalWords
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }

    class Factory(
        private val application: Application,
        private val lessonId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LearningViewModel::class.java)) {
                val questionFactory = QuestionFactoryImpl()
                val repository = VocabularyRepository(RetrofitClient.vocabularyApiService, questionFactory)
                val userProfileRepository = UserProfileRepository(RetrofitClient.userProfileService)
                val mistakeRepository = MistakeRepository(RetrofitClient.mistakeApiService)

                return LearningViewModel(
                    repository = repository,
                    lessonId = lessonId,
                    userProfileRepository = userProfileRepository,
                    mistakeRepository = mistakeRepository,
                    application = application
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}