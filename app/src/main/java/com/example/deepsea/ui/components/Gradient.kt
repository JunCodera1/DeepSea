package com.example.deepsea.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.Density

fun Modifier.diagonalGradientTint(
    colors: List<Color>,
    blendMode: BlendMode
) = drawWithContent {
    drawContent()
    drawRect(
        brush = Brush.linearGradient(colors),
        blendMode = blendMode
    )
}

fun Modifier.offsetGradientBackground(
    colors: List<Color>,
    blendMode: BlendMode
) = drawWithContent {
    drawContent()
    drawRect(
        brush = Brush.linearGradient(colors),
        blendMode = blendMode
    )
}

fun Modifier.offsetGradientBackground(
    colors: List<Color>,
    width: Density.() -> Float,
    offset: Density.() -> Float = {0f}
) = drawBehind {
    val actualOffset = offset()

    drawRect(
        Brush.horizontalGradient(
            colors = colors,
            startX = -actualOffset,
            endX = width() - actualOffset,
            tileMode = TileMode.Mirror
        )
    )
}