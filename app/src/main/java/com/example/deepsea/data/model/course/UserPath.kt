package com.example.deepsea.data.model.course

import com.example.deepsea.data.model.course.language.LanguageOption
import com.example.deepsea.data.model.course.path.PathOption

data class UserPathDto(
    val language: LanguageOption,
    val path: PathOption?
)
