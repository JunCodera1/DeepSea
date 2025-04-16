package com.example.deepsea.data.model

data class UploadResponse(
    val success: Boolean,
    val url: String,
    val message: String? = null
)