package com.example.deepsea.ui.screens.feature

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deepsea.data.model.HearingExercise
import com.example.deepsea.ui.viewmodel.LanguageListeningViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun LanguageListeningScreen(
    viewModel: LanguageListeningViewModel = viewModel(),
    onNavigateToSettings: () -> Unit = {}
) {
    val hearingExercise by viewModel.currentExercise.collectAsState()
    val userProgress by viewModel.userProgress.collectAsState()
    val hearts by viewModel.hearts.collectAsState()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()
    val isSpellingPlaying by viewModel.isSpellingPlaying.collectAsState()
    val selectedOption by viewModel.selectedOption.collectAsState()
    val isAnswerCorrect by viewModel.isAnswerCorrect.collectAsState()

    val scope = rememberCoroutineScope()
    var showFeedback by remember { mutableStateOf(false) }

    // Show feedback when answer is checked
    LaunchedEffect(isAnswerCorrect) {
        if (isAnswerCorrect != null) {
            showFeedback = true
            delay(1500)
            showFeedback = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar with Settings and Progress
        TopBar(
            progress = userProgress,
            hearts = hearts,
            onSettingsClick = onNavigateToSettings
        )

        // Main Instruction
        Text(
            text = "Tap what you hear",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        // Audio Buttons
        AudioControls(
            isAudioPlaying = isAudioPlaying,
            onPlayNormal = { viewModel.playAudio() },
            onPlaySlow = { viewModel.playSlowAudio() },
            onPlayExerciseAudio = { viewModel.playExerciseAudio() }
        )

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Feedback animation when answer is checked
        AnimatedVisibility(
            visible = showFeedback,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isAnswerCorrect == true) {
                    FeedbackMessage(
                        message = "Great job!",
                        icon = Icons.Default.Check,
                        color = Color.Green
                    )
                } else if (isAnswerCorrect == false) {
                    FeedbackMessage(
                        message = "Try again!",
                        icon = Icons.Default.Close,
                        color = Color.Red
                    )
                }
            }
        }

        // Answer Options
        if (!showFeedback) {
            AnswerOptions(
                options = hearingExercise.options,
                correctAnswer = hearingExercise.correctAnswer,
                selectedOption = selectedOption,
                isSpellingPlaying = isSpellingPlaying,
                onOptionClick = { viewModel.checkAnswer(it) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Skip Listening Option
        TextButton(
            onClick = { /* Skip listening functionality */ },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "CAN'T LISTEN NOW",
                color = Color.Gray,
                fontSize = 16.sp
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
fun TopBar(
    progress: Float,
    hearts: Int,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Settings Icon
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.3f))
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.DarkGray
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
                    .fillMaxWidth(progress) // Use fraction instead of fixed width
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
}

@Composable
fun AudioControls(
    isAudioPlaying: Boolean,
    onPlayNormal: () -> Unit,
    onPlaySlow: () -> Unit,
    onPlayExerciseAudio: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        // Main Audio Button
        AudioButton(
            size = 100.dp,
            isPlaying = isAudioPlaying,
            icon = "🔊",
            iconSize = 36.sp,
            onClick = onPlayExerciseAudio
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Regular Speed Audio Button
        AudioButton(
            size = 70.dp,
            isPlaying = isAudioPlaying,
            icon = "🎧",
            iconSize = 24.sp,
            onClick = onPlayNormal
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Slow Audio Button
        AudioButton(
            size = 70.dp,
            isPlaying = isAudioPlaying,
            icon = "🐢",
            iconSize = 24.sp,
            onClick = onPlaySlow
        )
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
fun AnswerOptions(
    options: List<String>,
    correctAnswer: String,
    selectedOption: String?,
    isSpellingPlaying: Boolean,
    onOptionClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        items(options.size) { index ->
            val option = options[index]
            val isSelected = selectedOption == option
            val isCorrect = selectedOption != null && option == correctAnswer
            val isWrong = isSelected && option != correctAnswer

            AnswerOption(
                text = option,
                isSelected = isSelected,
                isCorrect = isCorrect,
                isWrong = isWrong,
                isPlayingSpelling = isSpellingPlaying && isSelected,
                onClick = { onOptionClick(option) }
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
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = when {
                isCorrect -> Color.Green.copy(alpha = 0.8f)
                isWrong -> Color.Red.copy(alpha = 0.8f)
                else -> Color.Black
            }
        )

        // Spelling animation effect
        if (isPlayingSpelling) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Blue.copy(alpha = 0.05f))
            )

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sound wave animation effect
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

@Composable
fun FeedbackMessage(
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = message,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageListeningScreenPreview() {
    // Create a mock ViewModel for preview
    val mockViewModel = object {
        val currentExercise = MutableStateFlow(
            HearingExercise(
                id = "1",
                audio = "audio_url",
                correctAnswer = "ください",
                options = listOf("ください", "おちゃ", "ごはん", "と")
            )
        )
        val userProgress = MutableStateFlow(0.4f)
        val hearts = MutableStateFlow(3)
        val isAudioPlaying = MutableStateFlow(false)
        val isSpellingPlaying = MutableStateFlow(false)
        val selectedOption = MutableStateFlow<String?>(null)
        val isAnswerCorrect = MutableStateFlow<Boolean?>(null)

        fun playAudio() {}
        fun playSlowAudio() {}
        fun playExerciseAudio() {}
        fun checkAnswer(option: String) {}
        fun checkExercise() {}
    }

    // Material theme wrapper
    MaterialTheme {
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar Preview
                TopBar(progress = 0.4f, hearts = 3, onSettingsClick = {})

                Spacer(modifier = Modifier.height(16.dp))

                // Audio Controls Preview
                AudioControls(
                    isAudioPlaying = false,
                    onPlayNormal = {},
                    onPlaySlow = {},
                    onPlayExerciseAudio = {}
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Answer Options Preview
                AnswerOptions(
                    options = listOf("ください", "おちゃ", "ごはん", "と"),
                    correctAnswer = "ください",
                    selectedOption = "おちゃ",
                    isSpellingPlaying = false,
                    onOptionClick = {}
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Feedback Message Preview
                FeedbackMessage(
                    message = "Great job!",
                    icon = Icons.Default.Check,
                    color = Color.Green
                )
            }
        }
    }
}