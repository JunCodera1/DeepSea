package com.example.deepsea.data.repository

import com.example.deepsea.R
import com.example.deepsea.data.model.question.ImageSelectionQuestion
import com.example.deepsea.data.model.question.MultipleChoiceQuestion
import com.example.deepsea.data.model.question.Question
import com.example.deepsea.data.model.question.QuestionType
import com.example.deepsea.data.model.question.QuizQuestion
import com.example.deepsea.data.model.question.TranslationQuestion
import com.example.deepsea.data.model.question.VocabularyOptionUi
import com.example.deepsea.ui.viewmodel.learn.VocabularyItem

class QuestionFactoryImpl : QuestionFactory {
    override fun createQuestion(type: QuestionType, data: QuizQuestion, currentLanguage: String): Question {
        return when (type) {
            QuestionType.IMAGE_SELECTION -> createImageSelectionQuestion(data, currentLanguage)
            QuestionType.MULTIPLE_CHOICE -> createMultipleChoiceQuestion(data, currentLanguage)
            QuestionType.TRANSLATION -> createTranslationQuestion(data)
        }
    }


    private fun createImageSelectionQuestion(data: QuizQuestion, currentLanguage: String): ImageSelectionQuestion {
        return ImageSelectionQuestion(
            id = data.id,
            prompt = data.prompt,
            difficulty = calculateDifficulty(data),
            pronunciation = data.prompt,
            languageOption = data.languageContent[currentLanguage]?.text ?: "N/A",
            options = data.options.map { option ->
                VocabularyOptionUi(
                    id = option.id,
                    imageResId = option.image,
                    name = option.languageContent[currentLanguage]?.text ?: "N/A"
                )
            },
            correctAnswer = data.correctAnswer
        )
    }


    private fun createMultipleChoiceQuestion(data: QuizQuestion, currentLanguage: String): MultipleChoiceQuestion {
        return MultipleChoiceQuestion(
            id = data.id,
            prompt = data.prompt,
            difficulty = calculateDifficulty(data),
            question = data.prompt,
            options = data.options.map {
                it.languageContent[currentLanguage]?.text ?: "N/A"
            },
            correctAnswerIndex = data.options.indexOfFirst { it.id == data.correctAnswer.id }
        )
    }



    private fun createTranslationQuestion(data: QuizQuestion): TranslationQuestion {
        // Xác định ngôn ngữ nguồn và đích dựa trên metadata hoặc context
        val (sourceLanguage, targetLanguage) = determineLanguages(data)

        return TranslationQuestion(
            id = data.id,
            prompt = data.prompt,
            difficulty = calculateDifficulty(data),
            sourceText = data.languageContent[sourceLanguage]!!.text,
            sourceLanguage = sourceLanguage,
            targetLanguage = targetLanguage,
            correctTranslation = data.prompt
        )
    }

    /**
     * Tính độ khó của câu hỏi dựa trên dữ liệu có sẵn
     */
    private fun calculateDifficulty(data: QuizQuestion): Int {
        // Logic để tính toán độ khó dựa trên độ phức tạp của câu hỏi
        // Có thể dựa vào độ dài của văn bản, số từ hiếm, v.v.
        return 1 // Giá trị mặc định, cần cải thiện
    }

    /**
     * Xác định ngôn ngữ nguồn và đích cho câu hỏi dịch thuật
     */
    private fun determineLanguages(data: QuizQuestion): Pair<String, String> {
        // Đây là nơi bạn có thể triển khai logic phức tạp hơn để xác định ngôn ngữ
        // Ví dụ: từ ngữ cảnh của bài học, metadata, hoặc cấu hình ứng dụng

        // Giả sử chúng ta có metadata hoặc context trong môi trường
        val sourceLanguage = "ja" // Default to Japanese
        val targetLanguage = "en" // Default to English

        return Pair(sourceLanguage, targetLanguage)
    }

    /**
     * Create vocabulary items for a specific lesson when API fails
     * This is a fallback method used in the repository
     */
    fun createVocabularyItemsForLesson(lessonId: Long): List<VocabularyItem> {
        // Create hardcoded vocabulary items based on lessonId
        return when (lessonId) {
            1L -> listOf(
                VocabularyItem(
                    id = 1,
                    native = "ごはん",
                    romaji = "gohan",
                    english = "rice",
                    imageResId = R.drawable.ic_rice
                ),
                VocabularyItem(
                    id = 2,
                    native = "すし",
                    romaji = "sushi",
                    english = "sushi",
                    imageResId = R.drawable.ic_sushi
                ),
                VocabularyItem(
                    id = 3,
                    native = "みず",
                    romaji = "mizu",
                    english = "water",
                    imageResId = R.drawable.ic_water
                ),
                VocabularyItem(
                    id = 4,
                    native = "おちゃ",
                    romaji = "ocha",
                    english = "green tea",
                    imageResId = R.drawable.ic_drinks
                )
            )
            2L -> listOf(
                VocabularyItem(
                    id = 5,
                    native = "いぬ",
                    romaji = "inu",
                    english = "dog",
                    imageResId = R.drawable.ic_sushi // Placeholder, should be replaced with actual image
                ),
                VocabularyItem(
                    id = 6,
                    native = "ねこ",
                    romaji = "neko",
                    english = "cat",
                    imageResId = R.drawable.ic_rice // Placeholder
                )
            )
            else -> emptyList()
        }
    }

    /**
     * Create random vocabulary items for quiz options
     * Used when API call for options fails
     */
    fun createRandomVocabularyItems(count: Int): List<VocabularyItem> {
        val allItems = listOf(
            VocabularyItem(
                id = 1,
                native = "ごはん",
                romaji = "gohan",
                english = "rice",
                imageResId = R.drawable.ic_rice
            ),
            VocabularyItem(
                id = 2,
                native = "すし",
                romaji = "sushi",
                english = "sushi",
                imageResId = R.drawable.ic_sushi
            ),
            VocabularyItem(
                id = 3,
                native = "みず",
                romaji = "mizu",
                english = "water",
                imageResId = R.drawable.ic_water
            ),
            VocabularyItem(
                id = 4,
                native = "おちゃ",
                romaji = "ocha",
                english = "green tea",
                imageResId = R.drawable.ic_drinks
            ),
            VocabularyItem(
                id = 5,
                native = "いぬ",
                romaji = "inu",
                english = "dog",
                imageResId = R.drawable.ic_sushi
            ),
            VocabularyItem(
                id = 6,
                native = "ねこ",
                romaji = "neko",
                english = "cat",
                imageResId = R.drawable.ic_rice
            )
        )

        // Return random items from the pool
        return allItems.shuffled().take(count.coerceAtMost(allItems.size))
    }
}