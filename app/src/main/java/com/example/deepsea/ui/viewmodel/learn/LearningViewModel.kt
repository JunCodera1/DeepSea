package com.example.deepsea.ui.viewmodel.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.repository.QuestionFactoryImpl
import com.example.deepsea.data.repository.VocabularyRepository
import com.example.deepsea.utils.JsonLogProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

// VocabularyItem data class không thay đổi
data class VocabularyItem(
    val id: Long,
    val native: String,  // Japanese word
    val romaji: String,  // Romanized pronunciation
    val english: String, // English translation
    val imageResId: Int  // Resource ID for the image
)

class LearningViewModel(
    private val repository: VocabularyRepository,
    private val lessonId: Long = 1 // Default lesson ID
) : ViewModel() {

    // Current word being learned
    private val _currentWord = MutableStateFlow<VocabularyItem?>(null)
    val currentWord: StateFlow<VocabularyItem?> = _currentWord

    // Options for the quiz
    private val _options = MutableStateFlow<List<VocabularyItem>>(emptyList())
    val options: StateFlow<List<VocabularyItem>> = _options

    // Hearts (lives) remaining
    private val _hearts = MutableStateFlow(5)
    val hearts: StateFlow<Int> = _hearts

    // Progress through the lesson
    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Lesson vocabulary items
    private val vocabularyItems = mutableListOf<VocabularyItem>()
    private var currentIndex = 0
    private var totalWords = 10 // Default, will update when loading lesson
    private val _isAnswerCorrect = MutableStateFlow<Boolean?>(null)
    val isAnswerCorrect: StateFlow<Boolean?> = _isAnswerCorrect

    init {
        loadLesson()
    }

    /**
     * Load vocabulary for the current lesson.
     * Now with support for processing direct JSON logs
     */
    private fun loadLesson() {
        viewModelScope.launch {
            try {
                // Phương pháp 1: Dùng API bình thường (mặc định)
                repository.getLessonVocabularyItems(lessonId).collect { words ->
                    processVocabularyItems(words)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading lesson from API, trying hardcoded JSON")

                // Phương pháp 2: Xử lý JSON hardcoded từ log
                val words = JsonLogProcessor.processHardcodedLogResponse()
                processVocabularyItems(words)
            }
        }
    }

    /**
     * Process vocabulary items after they are loaded
     */
    private fun processVocabularyItems(words: List<VocabularyItem>) {
        vocabularyItems.clear()
        vocabularyItems.addAll(words.shuffled()) // ← thêm shuffle ở đây
        totalWords = vocabularyItems.size
        if (vocabularyItems.isNotEmpty()) {
            loadNextWord()
        }
        _isLoading.value = false
    }


    /**
     * Process raw JSON from logs directly
     * This method can be called from outside to process JSON manually
     */
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

    /**
     * Load the next word to learn
     */
    fun loadNextWord() {
        viewModelScope.launch {
            if (currentIndex < vocabularyItems.size) {
                _currentWord.value = vocabularyItems[currentIndex]
                currentIndex++
                updateProgress()

                // Generate options for this word
                loadOptions()
            } else {
                // End of lesson reached
                // Could notify through a completed state
                Timber.d("End of lesson reached")
            }
        }
    }

    /**
     * Load options for the current quiz question
     */
    private fun loadOptions() {
        viewModelScope.launch {
            try {
                // If vocabulary list is empty, return
                if (vocabularyItems.isEmpty()) {
                    return@launch
                }

                // Get current word and create a list containing it
                val currentVocab = _currentWord.value ?: return@launch
                val allOptions = mutableListOf<VocabularyItem>()

                // Add current word to options
                allOptions.add(currentVocab)

                // Add other words from vocabulary list (except current word)
                // to have enough options (up to 4)
                val otherOptions = vocabularyItems
                    .filter { it.id != currentVocab.id }
                    .shuffled()
                    .take(3.coerceAtMost(vocabularyItems.size - 1))

                allOptions.addAll(otherOptions)

                // If we still don't have 4 options, get additional ones from repository
                if (allOptions.size < 4) {
                    val additionalOptions = repository.getVocabularyOptions(4 - allOptions.size)
                    allOptions.addAll(additionalOptions)
                }

                // Shuffle options
                _options.value = allOptions.shuffled()
            } catch (e: Exception) {
                Timber.e(e, "Error loading options: ${e.message}")
                // Fallback options if there's an error - at least include current word
                val currentWord = _currentWord.value
                if (currentWord != null) {
                    _options.value = listOf(currentWord)
                }
            }
        }
    }

    /**
     * Check if the selected answer is correct
     */
    fun isAnswerCorrect(selectedOption: String): Boolean {
        return selectedOption == currentWord.value?.english
    }

    /**
     * Check the answer and update state
     */
    fun checkAnswer(selectedOption: String) {
        if (isAnswerCorrect(selectedOption)) {
            _isAnswerCorrect.value = true
            // Load next word after a delay from UI
        } else {
            _isAnswerCorrect.value = false
            decreaseHearts()
        }
    }

    fun resetAnswerState() {
        _isAnswerCorrect.value = null
    }

    /**
     * Decrease the number of hearts (lives)
     */
    fun decreaseHearts() {
        if (_hearts.value > 0) {
            _hearts.value -= 1
        }

        if (_hearts.value <= 0) {
            // Game over logic
            Timber.d("Game over - no hearts remaining")
        }
    }

    /**
     * Update progress through the lesson
     */
    private fun updateProgress() {
        _progress.value = currentIndex.toFloat() / totalWords
    }

    /**
     * Factory for creating this ViewModel without Hilt
     */
    class Factory(private val lessonId: Long) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LearningViewModel::class.java)) {
                val questionFactory = QuestionFactoryImpl()
                val repository = VocabularyRepository(RetrofitClient.vocabularyApiService, questionFactory)

                return LearningViewModel(repository, lessonId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}