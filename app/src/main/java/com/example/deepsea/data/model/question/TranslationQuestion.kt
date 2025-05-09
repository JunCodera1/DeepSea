package com.example.deepsea.data.model.question

data class TranslationQuestion(
    override val id: Long,
    override val prompt: String,
    override val difficulty: Int,
    val sourceText: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val correctTranslation: String
) : Question