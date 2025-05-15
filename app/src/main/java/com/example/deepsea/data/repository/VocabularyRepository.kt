package com.example.deepsea.data.repository

import com.example.deepsea.data.api.VocabularyApiService
import com.example.deepsea.data.model.question.QuizQuestion
import com.example.deepsea.ui.viewmodel.learn.VocabularyItem
import com.example.deepsea.utils.QuizResponseParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class VocabularyRepository(
    private val apiService: VocabularyApiService,
    private val questionFactory: QuestionFactoryImpl
) {
    private val quizResponseParser = QuizResponseParser()

    /**
     * Get vocabulary items for a specific lesson
     */
    fun getLessonVocabularyItems(lessonId: Long): Flow<List<VocabularyItem>> = flow {
        try {
            // Get questions from API - specify IMAGE_SELECTION type
            val quizQuestions = apiService.getLessonQuestions(lessonId, "IMAGE_SELECTION")

            // Convert to VocabularyItems
            val vocabularyItems = quizResponseParser.convertToVocabularyItems(quizQuestions)

            emit(vocabularyItems)
        } catch (e: Exception) {
            Timber.e(e, "Error getting lesson vocabulary items")

            // Fallback to local data as backup
            val fallbackItems = questionFactory.createVocabularyItemsForLesson(lessonId)
            emit(fallbackItems)
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Get random vocabulary options for quizzes
     */
    suspend fun getVocabularyOptions(count: Int): List<VocabularyItem> {
        return try {
            val options = apiService.getVocabularyOptions(count)
            quizResponseParser.convertToVocabularyItems(options)
        } catch (e: Exception) {
            Timber.e(e, "Error getting vocabulary options")
            questionFactory.createRandomVocabularyItems(count)
        }
    }

    fun convertQuizQuestionToVocabularyItem(question: QuizQuestion): VocabularyItem {
        return quizResponseParser.convertToVocabularyItem(question)
    }

    /**
     * Process raw JSON response from API
     * This method can be called directly when handling raw JSON data
     */
    fun processRawQuizResponse(jsonString: String): List<VocabularyItem> {
        val quizQuestions = quizResponseParser.parseQuizQuestions(jsonString)
        return quizResponseParser.convertToVocabularyItems(quizQuestions)
    }

    suspend fun getQuestionById(questionId: Long): QuizQuestion {
        return apiService.getQuestionById(questionId)
    }

    suspend fun getRandomQuestion(): QuizQuestion {
        return apiService.getRandomQuestion()
    }
}