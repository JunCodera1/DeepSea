package com.example.deepsea.ui.widget.theme

import androidx.compose.ui.unit.sp
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle

object JetChatGlanceTextStyles {

    val titleMedium = TextStyle(
        fontSize = 16.sp,
        color = JetchatGlanceColorScheme.colors.onSurfaceVariant,
        fontWeight = FontWeight.Bold
    )
    val bodyMedium = TextStyle(
        fontSize = 16.sp,
        color = JetchatGlanceColorScheme.colors.onSurfaceVariant,
        fontWeight = FontWeight.Normal
    )
}
