package com.example.deepsea.ui.screens.feature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deepsea.ui.viewmodel.LanguageListeningViewModel

@Composable
fun LanguageListeningScreen(viewModel: LanguageListeningViewModel = viewModel()) {
    val hearingExercise by viewModel.currentExercise.collectAsState()
    val userProgress by viewModel.userProgress.collectAsState()
    val hearts by viewModel.hearts.collectAsState()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()
    val isSpellingPlaying by viewModel.isSpellingPlaying.collectAsState()
    val selectedOption by viewModel.selectedOption.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar with Settings and Progress
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Settings Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.3f))
                    .clickable { /* Open Settings */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚙️",
                    fontSize = 20.sp
                )
            }

            // Progress Bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width((userProgress * 100).dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Green)
                )
            }

            // Hearts
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "❤️",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = hearts.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
        }

        // Main Instruction
        Text(
            text = "Tap what you hear",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        // Audio Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            // Regular Audio Button
            AudioButton(
                size = 100.dp,
                isPlaying = isAudioPlaying,
                icon = "🔊",
                iconSize = 36.sp,
                onClick = { viewModel.playAudio() }
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Slow Audio Button
            AudioButton(
                size = 70.dp,
                isPlaying = isAudioPlaying,
                icon = "🐢🔊",
                iconSize = 20.sp,
                onClick = { viewModel.playSlowAudio() }
            )
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Answer Options
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            items(hearingExercise.options.size) { index ->
                val option = hearingExercise.options[index]
                val isSelected = selectedOption == option
                val isCorrect = selectedOption != null && option == hearingExercise.correctAnswer

                AnswerOption(
                    text = option,
                    isSelected = isSelected,
                    isCorrect = isCorrect,
                    isWrong = isSelected && !isCorrect,
                    isPlayingSpelling = isSpellingPlaying && isSelected,
                    onClick = { viewModel.checkAnswer(option) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Bar
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "CAN'T LISTEN NOW",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Check Button
        Button(
            onClick = { viewModel.checkExercise() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedOption != null) MaterialTheme.colorScheme.primary else Color.LightGray,
                contentColor = if (selectedOption != null) Color.White else Color.Gray
            ),
            enabled = selectedOption != null
        ) {
            Text(
                text = "CHECK",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AudioButton(
    size: androidx.compose.ui.unit.Dp,
    isPlaying: Boolean,
    icon: String,
    iconSize: androidx.compose.ui.unit.TextUnit,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPlaying) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(size)
            .scale(if (isPlaying) scale else 1f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2196F3))
            .clickable(enabled = !isPlaying) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = icon,
            fontSize = iconSize,
            color = Color.White
        )

        if (isPlaying) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.1f))
            )
        }
    }
}

@Composable
fun AnswerOption(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isWrong: Boolean,
    isPlayingSpelling: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isCorrect -> Color.Green.copy(alpha = 0.2f)
        isWrong -> Color.Red.copy(alpha = 0.2f)
        isSelected -> Color.Blue.copy(alpha = 0.1f)
        else -> Color.Transparent
    }

    val borderColor = when {
        isCorrect -> Color.Green
        isWrong -> Color.Red
        isSelected -> Color.Blue
        else -> Color.LightGray
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(28.dp))
            .clickable(enabled = !isPlayingSpelling) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = when {
                isCorrect -> Color.Green.copy(alpha = 0.8f)
                isWrong -> Color.Red.copy(alpha = 0.8f)
                else -> Color.Black
            }
        )

        // Hiệu ứng đang phát âm spelling
        if (isPlayingSpelling) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Blue.copy(alpha = 0.05f))
            )

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hiệu ứng sóng âm nhỏ
                SpellingAnimationDots()
            }
        }
    }
}

@Composable
fun SpellingAnimationDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "spelling_animation")
    val dots = 3

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (i in 0 until dots) {
            val delay = i * 100
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500, delayMillis = delay, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_alpha_$i"
            )

            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(Color.Blue.copy(alpha = alpha))
            )
        }
    }
}

// Use auto-imports in Android Studio to resolve missing imports
// LazyVerticalGrid từ androidx.compose.foundation.lazy.grid
