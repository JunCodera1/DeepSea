package com.example.deepsea.data.model.question

import androidx.annotation.DrawableRes

data class VocabularyOptionUi(
    val id: Long,
    @DrawableRes val imageResId: Int,
    val name: String
)
