package com.example.deepsea.data.model

data class LanguageOptionRequest(
    val userId: Long?,
    val selectedOptions: Set<LanguageOption>
)
