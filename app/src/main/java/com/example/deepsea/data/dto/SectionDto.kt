package com.example.deepsea.data.dto

import android.content.Context
import android.util.Log
import com.example.deepsea.R
import com.example.deepsea.ui.components.SectionData
import com.example.deepsea.ui.components.UnitData

data class SectionDto(
    val id: Long,
    val title: String,
    val description: String,
    val level: String?,
    val color: String,
    val darkerColor: String,
    val image: String?
)

fun SectionDto.toSectionData(
    context: Context,
    units: List<UnitData> = emptyList()
): SectionData {
    // Improved image resolution with better logging
    val imageResIdInt = if (!image.isNullOrBlank()) {
        val resId = context.resources.getIdentifier(image, "drawable", context.packageName)
        if (resId == 0) {
            Log.w("SectionDto", "Could not find drawable resource for: $image")
            R.drawable.cut  // Fallback image
        } else {
            resId
        }
    } else {
        Log.d("SectionDto", "No image provided for section: $title, using default")
        R.drawable.cut  // Default image for null values
    }

    return SectionData(
        title = title,
        description = description,
        level = level ?: "A1",
        image = imageResIdInt,
        color = convertHexToColor(color),
        darkerColor = convertHexToColor(darkerColor),
        units = units
    )
}