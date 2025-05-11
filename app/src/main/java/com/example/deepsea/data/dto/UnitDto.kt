package com.example.deepsea.data.dto

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.example.deepsea.R
import com.example.deepsea.ui.components.UnitData

data class UnitDto(
    val id: Long,
    val sectionId: Long,
    val title: String,
    val description: String,
    val color: String,
    val darkerColor: String,
    val image: String?,
    val orderIndex: Int,
    val starsRequired: Int,
    val lessonCount: Int
)

fun UnitDto.toUnitData(context: Context): UnitData {
    // Improved image resource resolution with debug logging
    val imageResId = if (!image.isNullOrBlank()) {
        val resId = context.resources.getIdentifier(image, "drawable", context.packageName)
        if (resId == 0) {
            Log.w("UnitDto", "Could not find drawable resource for: $image, unit: $title")
            R.drawable.cut  // Fallback image
        } else {
            resId
        }
    } else {
        Log.d("UnitDto", "No image provided for unit: $title, using default")
        R.drawable.cut  // Default image for null values
    }

    return UnitData(
        id = this.id,
        title = this.title,
        description = this.description,
        color = convertHexToColor(color),
        darkerColor = convertHexToColor(darkerColor),
        image = imageResId
    )
}

fun convertHexToColor(hex: String): Color {
    return try {
        // Handle both formats - with or without the # prefix
        val normalizedHex = if (hex.startsWith("#")) hex else "#$hex"
        Color(android.graphics.Color.parseColor(normalizedHex))
    } catch (e: IllegalArgumentException) {
        Log.e("COLOR_ERROR", "Invalid color code: $hex, using fallback", e)
        Color.Gray
    }
}