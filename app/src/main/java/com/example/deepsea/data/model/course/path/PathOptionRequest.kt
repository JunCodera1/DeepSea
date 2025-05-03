package com.example.deepsea.data.model.course.path

import com.example.deepsea.data.model.course.language.LanguageOption

data class PathOptionRequest(
    val userId: Long,
    val language: LanguageOption,
    val path: PathOption
)

