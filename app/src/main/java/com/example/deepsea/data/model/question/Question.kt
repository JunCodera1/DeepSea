package com.example.deepsea.data.model.question

interface Question {
    val id: Long
    val prompt: String
    val difficulty: Int // Mức độ khó: 1-Dễ, 2-Trung bình, 3-Khó
}