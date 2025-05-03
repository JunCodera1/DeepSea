package com.example.deepsea.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SkillIndicator(name: String, level: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        val color = when {
            level >= 9 -> colorScheme.primary
            level >= 7 -> colorScheme.secondary
            level >= 5 -> colorScheme.tertiary
            else -> colorScheme.error
        }

        Canvas(modifier = Modifier.size(30.dp)) {
            val strokeWidth = 5f
            val center = Offset(size.width / 2, size.height / 2)
            val radius = (size.width - strokeWidth) / 2

            // Background circle
            drawCircle(
                color = Color(0xFF4FC3F7) ,
                radius = radius,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth
                )
            )

            // Progress arc
            val sweepAngle = 360f * (level / 10f)
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = name,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = "$level/10",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}