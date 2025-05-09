package com.example.deepsea.data.repository

import com.example.deepsea.data.api.VocabularyApiService
import com.example.deepsea.data.model.question.ImageSelectionQuestion
import com.example.deepsea.data.model.question.QuestionType
import com.example.deepsea.data.model.question.QuizQuestion
import com.example.deepsea.data.model.question.VocabularyOptionUi
import com.example.deepsea.ui.viewmodel.learn.VocabularyItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class VocabularyRepository(
    private val apiService: VocabularyApiService,
    private val questionFactory: QuestionFactory
) {
    /**
     * Get vocabulary items for a lesson as a Flow
     */
    fun getLessonVocabularyItems(lessonId: Long): Flow<List<VocabularyItem>> = flow {
        val questions = apiService.getLessonQuestions(lessonId,
            QuestionType.IMAGE_SELECTION.toString()
        )
        val vocabularyItems = questions.map { quizQuestion ->
            mapToVocabularyItem(quizQuestion)
        }
        emit(vocabularyItems)
    }

    /**
     * Get a random vocabulary item
     */
    suspend fun getRandomVocabularyItem(): VocabularyItem? {
        val quizQuestion = apiService.getRandomVocabularyItem() ?: return null
        return mapToVocabularyItem(quizQuestion)
    }

    /**
     * Get vocabulary options for a quiz
     */
    suspend fun getVocabularyOptions(size: Int = 4): List<VocabularyItem> {
        val options = apiService.getVocabularyOptions(size)
        return options.map { mapToVocabularyItem(it) }
    }

    /**
     * Map API response to UI model
     */
    private fun mapToVocabularyItem(quizQuestion: QuizQuestion): VocabularyItem {
        val currentLanguage = "en" // Change based on app settings

        // Create ImageSelectionQuestion using the factory
        val imageQuestion = questionFactory.createQuestion(
            QuestionType.IMAGE_SELECTION,
            quizQuestion,
            currentLanguage
        ) as ImageSelectionQuestion

        // Extract the Japanese (native) word
        val nativeWord = quizQuestion.languageContent["ja"]?.text ?: "N/A"

        // Extract romaji (pronunciation)
        val romaji = quizQuestion.languageContent["ja"]?.pronunciation ?: imageQuestion.pronunciation

        // Extract the English translation
        val english = quizQuestion.languageContent["en"]?.text ?: imageQuestion.languageOption

        // Map to VocabularyItem (used by the UI)
        return VocabularyItem(
            id = quizQuestion.id,
            native = nativeWord,
            romaji = romaji,
            english = english,
            imageResId = quizQuestion.options.firstOrNull {
                it.id == quizQuestion.correctAnswerId
            }?.image ?: 0
        )
    }
}