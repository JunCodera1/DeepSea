package com.example.deepsea.data.dto

import android.content.Context
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
    val imageResId: String?           // Keep as nullable
)

fun SectionDto.toSectionData(
    context: Context,
    units: List<UnitData> = emptyList()
): SectionData {
    // Chuyển đổi tên hình ảnh (string) sang drawable resource ID
    val imageResIdInt = imageResId?.let { name ->
        context.resources.getIdentifier(name, "drawable", context.packageName)
            .takeIf { it != 0 }  // Nếu không tìm thấy sẽ trả 0, tránh lỗi
    } ?: R.drawable.cut  // Mặc định

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
