package com.example.deepsea.data.model.exercise

// Data model for the matching pairs
data class MatchingPair(
    val id: String,
    val english: String,
    val japanese: String,
    val pronunciation: String,
    var isSelected: Boolean = false,
    var isMatched: Boolean = false
)