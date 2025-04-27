package com.example.deepsea.data.model.language

data class LanguageOptionRequest(
    val userId: Long?,
    val selectedOptions: Set<LanguageOption>
)
