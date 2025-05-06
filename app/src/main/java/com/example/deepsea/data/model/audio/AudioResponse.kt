package com.example.deepsea.data.model.audio

data class AudioResponse(
    val audioUrl: String,
    val word: String,
    val phonetics: String?
)