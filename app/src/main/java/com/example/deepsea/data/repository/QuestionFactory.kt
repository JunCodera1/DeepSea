package com.example.deepsea.data.repository

import com.example.deepsea.data.model.question.Question
import com.example.deepsea.data.model.question.QuestionType
import com.example.deepsea.data.model.question.QuizQuestion

interface QuestionFactory {
    fun createQuestion(type: QuestionType, data: QuizQuestion, currentLanguage: String): Question
}