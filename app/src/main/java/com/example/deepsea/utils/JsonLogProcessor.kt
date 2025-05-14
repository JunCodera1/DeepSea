package com.example.deepsea.utils

import android.util.Log
import com.example.deepsea.ui.viewmodel.learn.VocabularyItem

/**
 * Utility class to handle JSON log responses
 */
object JsonLogProcessor {
    private const val TAG = "JsonLogProcessor"
    private val quizResponseParser = QuizResponseParser()

    /**
     * Process a log line that contains JSON data
     * This is specifically for debugging and development purposes
     */
    fun processLogLine(logLine: String): List<VocabularyItem> {
        // Extract the JSON part from the log line
        val jsonStart = logLine.indexOf("[{")
        val jsonEnd = logLine.lastIndexOf("}]") + 2

        if (jsonStart == -1 || jsonEnd == -1 || jsonEnd <= jsonStart) {
            Log.e(TAG, "Invalid JSON format in log line")
            return emptyList()
        }

        val jsonString = logLine.substring(jsonStart, jsonEnd)
        Log.d(TAG, "Extracted JSON: $jsonString")

        // Parse the JSON
        return quizResponseParser.parseQuizQuestions(jsonString).let { questions ->
            quizResponseParser.convertToVocabularyItems(questions)
        }
    }

    /**
     * Process the specific log line provided on May 14, 2025
     * This is a hardcoded version for quick implementation
     */
    fun processHardcodedLogResponse(): List<VocabularyItem> {
        val hardcodedJson = """[{"id":2,"type":"IMAGE_SELECTION","prompt":"Choose the correct translation for 'sushi'","lessonId":1,"languageContent":{"en":{"text":"Choose the correct image for sushi","pronunciation":""},"ja":{"text":"sushiの正しい画像を選んでください","pronunciation":""}},"options":[{"id":2,"image":1002,"languageContent":{"en":{"text":"sushi","pronunciation":""},"ja":{"text":"すし","pronunciation":"sushi"}}}],"correctAnswer":{"id":2,"image":1002,"languageContent":{"en":{"text":"sushi","pronunciation":""},"ja":{"text":"すし","pronunciation":"sushi"}}}},{"id":3,"type":"IMAGE_SELECTION","prompt":"Choose the correct translation for 'water'","lessonId":1,"languageContent":{"en":{"text":"Choose the correct image for water","pronunciation":""},"ja":{"text":"waterの正しい画像を選んでください","pronunciation":""}},"options":[{"id":3,"image":1003,"languageContent":{"en":{"text":"water","pronunciation":""},"ja":{"text":"みず","pronunciation":"mizu"}}}],"correctAnswer":{"id":3,"image":1003,"languageContent":{"en":{"text":"water","pronunciation":""},"ja":{"text":"みず","pronunciation":"mizu"}}}},{"id":4,"type":"IMAGE_SELECTION","prompt":"Choose the correct translation for 'green tea'","lessonId":1,"languageContent":{"en":{"text":"Choose the correct image for green tea","pronunciation":""},"ja":{"text":"green teaの正しい画像を選んでください","pronunciation":""}},"options":[{"id":4,"image":1004,"languageContent":{"en":{"text":"green tea","pronunciation":""},"ja":{"text":"おちゃ","pronunciation":"ocha"}}}],"correctAnswer":{"id":4,"image":1004,"languageContent":{"en":{"text":"green tea","pronunciation":""},"ja":{"text":"おちゃ","pronunciation":"ocha"}}}}]"""

        return quizResponseParser.parseQuizQuestions(hardcodedJson).let { questions ->
            quizResponseParser.convertToVocabularyItems(questions)
        }
    }
}