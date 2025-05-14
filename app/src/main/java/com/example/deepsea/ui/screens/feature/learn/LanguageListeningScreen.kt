package com.example.deepsea.ui.screens.feature.learn

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.ui.viewmodel.learn.LanguageListeningViewModel
import com.example.deepsea.ui.viewmodel.learn.LanguageListeningViewModelFactory
import kotlinx.coroutines.delay

@Composable
fun LanguageListeningScreen(
    sectionId: Long,
    unitId: Long,
    onNavigateToSettings: () -> Unit = {},
    onComplete: () -> Unit = {}
) {
    val context = LocalContext.current

    val viewModel: LanguageListeningViewModel = viewModel(
        factory = LanguageListeningViewModelFactory(RetrofitClient.hearingService, context)
    )

    val hearingExercise by viewModel.currentExercise.collectAsState()
    val userProgress by viewModel.userProgress.collectAsState()
    val hearts by viewModel.hearts.collectAsState()
    val isTtsPlaying by viewModel.isTtsPlaying.collectAsState()
    val selectedOption by viewModel.selectedOption.collectAsState()
    val isAnswerCorrect by viewModel.isAnswerCorrect.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showFeedback by remember { mutableStateOf(false) }

    // Show feedback when answer is checked
    LaunchedEffect(isAnswerCorrect) {
        if (isAnswerCorrect != null) {
            showFeedback = true
            delay(1500)
            showFeedback = false
            if (isAnswerCorrect == true) {
                viewModel.onExerciseCompleted()
                onComplete()
            }
        }
    }

    // Initialize TTS and fetch exercise
    LaunchedEffect(Unit) {
        viewModel.initializeTts()
        viewModel.fetchRandomExercise(sectionId, unitId)
    }

    // Retry fetching exercise on error
    val retryFetchExercise = { viewModel.fetchRandomExercise(sectionId, unitId) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            errorMessage != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = errorMessage ?: "Unknown error",
                        color = Color.Red,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(onClick = retryFetchExercise) {
                        Text("Retry")
                    }
                }
            }
            hearingExercise == null -> {
                Text(
                    text = "No exercise available",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxSize()
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

                    // Audio Button
                    AudioControls(
                        isTtsPlaying = isTtsPlaying,
                        onPlayTts = { viewModel.playTts(hearingExercise?.correctAnswer ?: "") }
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
                        if (hearingExercise?.options?.isNotEmpty() == true) {
                            AnswerOptions(
                                options = hearingExercise!!.options,
                                correctAnswer = hearingExercise!!.correctAnswer,
                                selectedOption = selectedOption,
                                isSpellingPlaying = false,
                                onOptionClick = { viewModel.checkAnswer(it) }
                            )
                        } else {
                            Text(
                                text = "Loading options...",
                                    fontSize = 16.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
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
                    .fillMaxWidth(progress)
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
    isTtsPlaying: Boolean,
    onPlayTts: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        AudioButton(
            size = 100.dp,
            isPlaying = isTtsPlaying,
            icon = "🔊",
            iconSize = 36.sp,
            onClick = onPlayTts
        )
    }
}

@Composable
fun AudioButton(
    size: Dp,
    isPlaying: Boolean,
    icon: String,
    iconSize: TextUnit,
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
    if (options.isEmpty()) {
        Text(
            text = "No options available",
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        items(options.size) { index ->
            val option = options.getOrNull(index) ?: return@items
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
    icon: ImageVector,
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