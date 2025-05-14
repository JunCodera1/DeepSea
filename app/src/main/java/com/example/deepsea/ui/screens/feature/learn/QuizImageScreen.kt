package com.example.deepsea.ui.screens.feature.learn

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deepsea.R
import com.example.deepsea.ui.theme.DeepSeaBlue
import com.example.deepsea.ui.theme.HeartRed
import com.example.deepsea.ui.theme.SuccessGreen
import com.example.deepsea.ui.viewmodel.learn.LearningViewModel
import com.example.deepsea.ui.viewmodel.learn.VocabularyItem
import kotlinx.coroutines.delay

@Composable
fun QuizImageScreen(
    lessonId: Long,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val learningViewModel: LearningViewModel = viewModel(
        factory = LearningViewModel.Factory(lessonId)
    )

    val isLoading by learningViewModel.isLoading.collectAsState()
    val currentWord by learningViewModel.currentWord.collectAsState()
    val options by learningViewModel.options.collectAsState()
    val heartCount by learningViewModel.hearts.collectAsState()
    val progressPercent by learningViewModel.progress.collectAsState()
    val isAnswerCorrect by learningViewModel.isAnswerCorrect.collectAsState()

    var selectedOption by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }

    // Check if the game is over
    val isGameOver = heartCount <= 0

    // Check for lesson completion
    val isLessonComplete = progressPercent >= 1.0f

    LaunchedEffect(isGameOver, isLessonComplete) {
        if (isGameOver) {
            // Handle game over
            delay(1500)
            onBack()
        } else if (isLessonComplete) {
            // Handle lesson completion
            delay(1500)
            onComplete()
        }
    }

    // Load next word when first launched
    LaunchedEffect(Unit) {
        if (currentWord == null && !isLoading) {
            learningViewModel.loadNextWord()
        }
    }

    // Surface for consistent theming
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isLoading) {
            LoadingScreen()
            return@Surface
        }

        // Check for feedback handling
        LaunchedEffect(isAnswerCorrect) {
            if (isAnswerCorrect != null) {
                showFeedback = true
                delay(2000) // Show feedback for 2 seconds
                showFeedback = false

                if (isAnswerCorrect == true) {
                    learningViewModel.loadNextWord()
                    selectedOption = null
                    learningViewModel.resetAnswerState()
                } else {
                    // Only reset answer state for wrong answers
                    learningViewModel.resetAnswerState()
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Bar
                TopBar(
                    heartCount = heartCount,
                    progressPercent = progressPercent,
                    onBackClick = onBack
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = "Select the correct image",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Word To Learn Section
                currentWord?.let { word ->
                    WordToLearnSection(currentWord = word)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Options Grid (only show if we have options)
                if (options.isNotEmpty()) {
                    OptionsGrid(
                        options = options,
                        selectedOption = selectedOption,
                        onOptionSelected = { option ->
                            if (isAnswerCorrect == null) {
                                selectedOption = option
                            }
                        },
                        isEnabled = isAnswerCorrect == null
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Check Button
                CheckButton(
                    isEnabled = selectedOption != null && isAnswerCorrect == null,
                    onClick = {
                        selectedOption?.let {
                            learningViewModel.checkAnswer(it)
                        }
                    }
                )
            }

            // Feedback overlay
            AnimatedVisibility(
                visible = showFeedback,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x80000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(16.dp)
                            .shadow(8.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (isAnswerCorrect == true) R.drawable.ic_check_circle
                                    else R.drawable.ic_mistakes
                                ),
                                contentDescription = if (isAnswerCorrect == true) "Correct" else "Incorrect",
                                tint = if (isAnswerCorrect == true) SuccessGreen else HeartRed,
                                modifier = Modifier.size(64.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = if (isAnswerCorrect == true) "Correct!" else "Try Again!",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAnswerCorrect == true) SuccessGreen else HeartRed
                            )

                            if (isAnswerCorrect == false && currentWord != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "The correct answer is: ${currentWord!!.english}",
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = DeepSeaBlue,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading quiz...",
                fontSize = 18.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TopBar(
    heartCount: Int,
    progressPercent: Float,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = "Settings",
                tint = Color.Gray
            )
        }

        // Progress Bar
        LinearProgressIndicator(
            progress = progressPercent,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
                .height(12.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = SuccessGreen,
            trackColor = Color.LightGray
        )

        // Hearts Counter
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_heart),
                contentDescription = "Hearts",
                tint = HeartRed,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = heartCount.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = HeartRed,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun WordToLearnSection(currentWord: VocabularyItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        // Audio Button
        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = Color(0xFF26A6E0)),
            modifier = Modifier.size(80.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Play Audio",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Word Information
        Column {
            // Romaji (phonetic spelling)
            Text(
                text = currentWord.romaji,
                fontSize = 18.sp,
                color = Color.Gray
            )

            // Japanese word
            Text(
                text = currentWord.native,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OptionsGrid(
    options: List<VocabularyItem>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    isEnabled: Boolean
) {
    // Ensure we have valid options to display
    if (options.size < 2) return

    // Group options into rows of 2
    val rows = options.chunked(2)

    Column(modifier = Modifier.fillMaxWidth()) {
        rows.forEach { rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowOptions.forEach { option ->
                    ImageOption(
                        imageRes = option.imageResId,
                        label = option.english,
                        isSelected = selectedOption == option.english,
                        onSelected = { onOptionSelected(option.english) },
                        isEnabled = isEnabled,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Add empty placeholders if needed to maintain layout
                if (rowOptions.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ImageOption(
    imageRes: Int,
    label: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = isEnabled) { onSelected() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE0F7FA) else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Image with error handling
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = label,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize(0.8f)
                    )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Label
            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CheckButton(
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEnabled) DeepSeaBlue else Color(0xFFE0E0E0),
            contentColor = if (isEnabled) Color.White else Color.Gray
        )
    ) {
        Text(
            text = "CHECK",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

