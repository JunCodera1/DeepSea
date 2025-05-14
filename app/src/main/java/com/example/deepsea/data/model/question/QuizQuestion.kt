package com.example.deepsea.data.model.question

data class QuizQuestion(
    val id: Long,
    val type: String,
    val prompt: String,
    val lessonId: Long,
    val languageContent: Map<String, LanguageContent>,
    val options: List<VocabularyOption>,
    val correctAnswer: VocabularyOption
){
    // Hàm tiện ích để lấy nội dung theo ngôn ngữ
    fun getPromptForLanguage(language: String): String {
        return languageContent[language]?.text ?: prompt
    }
}
