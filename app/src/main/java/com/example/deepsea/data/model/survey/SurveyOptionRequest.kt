package com.example.deepsea.data.model.survey

data class SurveyOptionRequest(
    val userId: Long?,
    val selectedOptions: Set<SurveyOption>
)
