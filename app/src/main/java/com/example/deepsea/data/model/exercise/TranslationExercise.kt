package com.example.deepsea.data.model.exercise

/**
 * Data class representing a translation exercise where
 * users build a sentence by selecting words in order
 */
data class TranslationExercise(
    val id: String,
    val sourceText: String,        // The text to be translated (e.g., English)
    val targetText: String,        // The correct translation (e.g., Japanese)
    val sourceLanguage: String,    // Source language code (e.g., "en")
    val targetLanguage: String,    // Target language code (e.g., "ja")
    val wordOptions: List<String>  // Available word options to build the answer
)