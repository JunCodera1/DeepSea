package com.example.deepsea.data.model.question

import androidx.annotation.DrawableRes

data class  VocabularyOption(
    val id: Long,
    @DrawableRes val image: Int,
    val languageContent: Map<String, LanguageContent>
)

