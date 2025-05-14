package com.example.deepsea.data.model.question

data class VocabularyOption(
    val id: Long,
    val image: Int,
    val languageContent: Map<String, LanguageText>
) {
    // Hàm tiện ích để lấy nội dung theo ngôn ngữ
    fun getTextForLanguage(language: String): String {
        return languageContent[language]?.text ?: ""
    }

    fun getPronunciationForLanguage(language: String): String {
        return languageContent[language]?.pronunciation ?: ""
    }
}

