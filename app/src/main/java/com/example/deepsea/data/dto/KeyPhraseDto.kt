package com.example.deepsea.data.dto

data class KeyPhraseDto(
    val id: Long,
    val unitId: Long,
    val originalText: String,
    val translatedText: String,
    val audioUrl: String? = null,
    val orderIndex: Int = 0
)