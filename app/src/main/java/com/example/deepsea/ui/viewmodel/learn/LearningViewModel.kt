package com.example.deepsea.ui.viewmodel.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.repository.QuestionFactoryImpl
import com.example.deepsea.data.repository.VocabularyRepository
import com.example.deepsea.data.api.VocabularyApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    private val _currentWord = MutableStateFlow<VocabularyItem>(
        VocabularyItem(0, "", "", "", 0)
    )
    val currentWord: StateFlow<VocabularyItem> = _currentWord

    // Options for the quiz
    private val _options = MutableStateFlow<List<VocabularyItem>>(emptyList())
    val options: StateFlow<List<VocabularyItem>> = _options

    // Hearts (lives) remaining
    private val _hearts = MutableStateFlow(5)
    val hearts: StateFlow<Int> = _hearts

    // Progress through the lesson
    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

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
     * Load vocabulary for the current lesson
     */
    private fun loadLesson() {
        viewModelScope.launch {
            try {
                repository.getLessonVocabularyItems(lessonId).collect { words ->
                    vocabularyItems.clear()
                    vocabularyItems.addAll(words)
                    totalWords = vocabularyItems.size
                    if (vocabularyItems.isNotEmpty()) {
                        loadNextWord()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error state - could add error state flow
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
            }
        }
    }

    /**
     * Load options for the current quiz question
     */
    private fun loadOptions() {
        viewModelScope.launch {
            try {
                // Get 3 random options + the correct one
                val randomOptions = repository.getVocabularyOptions(3).toMutableList()

                // Add the current word as the correct option
                val allOptions = randomOptions.toMutableList()
                if (!allOptions.contains(_currentWord.value)) {
                    allOptions.add(_currentWord.value)
                }

                // Shuffle options
                allOptions.shuffle()

                _options.value = allOptions
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error
            }
        }
    }

    /**
     * Check if the selected answer is correct
     */
    fun isAnswerCorrect(selectedOption: String): Boolean {
        return selectedOption == currentWord.value.english
    }

    /**
     * Check the answer and update state
     */
    fun checkAnswer(selectedOption: String) {
        if (isAnswerCorrect(selectedOption)) {
            _isAnswerCorrect.value = true
            // Load next word sau một delay từ UI
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