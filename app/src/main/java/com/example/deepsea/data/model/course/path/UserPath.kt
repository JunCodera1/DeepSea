package com.example.deepsea.data.model.course.path

import com.example.deepsea.data.model.course.language.LanguageOption

data class UserPathDto(
    val language: LanguageOption,
    val path: PathOption?
)
