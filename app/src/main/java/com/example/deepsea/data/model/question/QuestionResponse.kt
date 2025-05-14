package com.example.deepsea.data.model.question

data class QuestionResponse(
    val id: Long,
    val type: String,
    val prompt: String,
    val lessonId: Long,
    val languageContent: Map<String, LanguageText>,
    val options: List<VocabularyOption>,
    val correctAnswer: VocabularyOption
)