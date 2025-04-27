package com.example.deepsea.data.model.user

data class UploadResponse(
    val success: Boolean,
    val url: String,
    val message: String? = null
)