package com.example.deepsea.data.model.review

import java.util.UUID

data class Word(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val reading: String,
    val meaning: String,
    val storyTitle: String,
    val level: String,
    val context: String,
    val theme: String,
    val exampleSentence: String = "",
    val pronunciation: String = ""
)