package com.example.deepsea.data.model.audio

data class SpellingResponse(
    val word: String,
    val phoneticParts: List<String>,
    val audioUrls: List<String>
)
