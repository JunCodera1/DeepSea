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
    val image: String?,               // This is the correct field name
    val orderIndex: Int,
    val starsRequired: Int,
    val lessonCount: Int
)
fun UnitDto.toUnitData(context: Context): UnitData {
    return UnitData(
        title = this.title,
        description = this.description,
        color = convertHexToColor(color),
        darkerColor = convertHexToColor(darkerColor),
        image = this.image?.let { imageName ->
            context.resources.getIdentifier(imageName, "drawable", context.packageName)
        } ?: R.drawable.cut
    )
}

fun convertHexToColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: IllegalArgumentException) {
        Log.e("COLOR_ERROR", "Invalid color code: $hex, using fallback", e)
        Color.Gray
    }
}

