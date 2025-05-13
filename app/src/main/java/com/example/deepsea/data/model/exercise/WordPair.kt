package com.example.deepsea.data.model.exercise

data class WordPair(
    val id: Long,
    val unitId: Long,
    val english: String,
    val japanese: String,
    val pronunciation: String,
    val level: String,
    var isSelected: Boolean = false, // Added for UI state
    var isMatched: Boolean = false   // Added for UI state
)