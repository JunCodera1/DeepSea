package com.example.deepsea.data.model.review

data class ShadowListeningSession(
    val id: String,
    val title: String,
    val difficulty: String,
    val duration: String,
    val transcript: String,
    val keyPhrases: List<String>,
    val language: String = "Japanese",
    val description: String? = null,
    val createdAt: String? = null, // Use String for JSON serialization
    val updatedAt: String? = null
)