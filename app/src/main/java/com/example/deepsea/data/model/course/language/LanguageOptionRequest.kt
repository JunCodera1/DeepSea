package com.example.deepsea.data.model.course.language

data class LanguageOptionRequest(
    val userId: Long?,
    val selectedOptions: Set<LanguageOption>
)
