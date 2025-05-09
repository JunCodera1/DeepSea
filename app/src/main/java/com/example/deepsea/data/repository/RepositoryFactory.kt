package com.example.deepsea.data.repository


object RepositoryFactory {
    val lessonRepository: LessonRepository by lazy {
        LessonRepositoryImpl()
    }
}