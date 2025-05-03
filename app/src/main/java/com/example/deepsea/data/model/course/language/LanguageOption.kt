package com.example.deepsea.data.model.course.language

import androidx.annotation.DrawableRes
import com.example.deepsea.R

enum class LanguageOption(val displayName: String, @DrawableRes val flagResId: Int) {
    ENGLISH("English", R.drawable.flag_england),
    SPANISH("Spanish", R.drawable.flag_spain),
    JAPANESE("Japanese", R.drawable.flag_japan),
    FRENCH("French", R.drawable.flag_france),
    GERMANY("Germany", R.drawable.flag_germany),
    ITALY("Italy", R.drawable.flag_italy)
}