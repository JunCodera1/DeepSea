package com.example.deepsea.data.model.question

data class QuizQuestion(
    val id: Long,
    val prompt: String,
    val difficulty: Int,
    val languageContent: Map<String, LanguageContent>,
    val options: List<VocabularyOption>,
    val correctAnswerId: Long,
    val metadata: QuestionMetadata
)