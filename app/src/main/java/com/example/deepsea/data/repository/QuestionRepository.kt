package com.example.deepsea.data.repository

import com.example.deepsea.data.api.LessonApi
import com.example.deepsea.data.model.question.Question
import com.example.deepsea.data.model.question.QuestionType
import com.example.deepsea.data.model.question.QuizQuestion
import javax.inject.Inject

class QuestionRepository @Inject constructor(
    private val api: LessonApi,
    private val questionFactory: QuestionFactory
) {
    suspend fun getRandomQuestion(type: QuestionType? = null): Question {
        val questionData = api.getRandomQuestion(type?.name)

        val questionType = QuestionType.valueOf(questionData.languageContent as String)
        return questionFactory.createQuestion(questionType, questionData, "")
    }

    suspend fun getLessonQuestions(lessonId: Long, type: QuestionType? = null): List<Question> {
        val questionsData = api.getLessonQuestions(lessonId, type?.name)

        return questionsData.map { questionData ->
            val questionType = QuestionType.valueOf(questionData["type"]?.toString() ?: "DEFAULT")

            val quizQuestion = questionData["quizQuestion"] as QuizQuestion
            questionFactory.createQuestion(questionType, quizQuestion, currentLanguage = "en")
        }
    }
}