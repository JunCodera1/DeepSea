package com.example.deepsea.data.model.question

data class ImageSelectionQuestion(
    override val id: Long,
    override val prompt: String,
    override val difficulty: Int,
    val pronunciation: String,
    val languageOption: String,
    val options: List<VocabularyOptionUi>,
    val correctAnswer: VocabularyOption
) : Question