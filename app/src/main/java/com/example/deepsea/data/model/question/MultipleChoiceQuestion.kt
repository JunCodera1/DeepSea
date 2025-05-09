package com.example.deepsea.data.model.question

data class MultipleChoiceQuestion(
    override val id: Long,
    override val prompt: String,
    override val difficulty: Int,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int
) : Question