package com.example.deepsea.data.api

import com.google.gson.annotations.SerializedName

data class SessionData(
    @SerializedName("lessonId") val lessonId: Long,
    @SerializedName("userId") val userId: Long = 1, // Default user for simplicity
    @SerializedName("totalScreens") val totalScreens: Int,
    @SerializedName("completedScreens") val completedScreens: Int,
    @SerializedName("screensCompleted") val screensCompleted: List<String>
)