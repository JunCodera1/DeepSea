package com.example.deepsea.data.model

data class SurveyOptionRequest(
    val userId: Long?,
    val selectedOptions: Set<SurveyOption>
)
