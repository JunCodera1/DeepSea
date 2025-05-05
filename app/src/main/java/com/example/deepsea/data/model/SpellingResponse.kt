package com.example.deepsea.data.model

data class SpellingResponse(
    val word: String,
    val phoneticParts: List<String>,
    val audioUrls: List<String>
)
