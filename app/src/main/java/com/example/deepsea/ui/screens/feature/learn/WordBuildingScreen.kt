package com.example.deepsea.ui.screens.feature.learn

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deepsea.R
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.repository.MistakeRepository
import com.example.deepsea.ui.theme.DeepSeaBlue
import com.example.deepsea.ui.theme.HeartRed
import com.example.deepsea.ui.viewmodel.learn.WordBuildingViewModel
import com.example.deepsea.ui.viewmodel.learn.WordBuildingViewModelFactory
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordBuildingScreen(
    onBackClick: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onComplete: () -> Unit,
    viewModel: WordBuildingViewModel = viewModel(factory =
        WordBuildingViewModelFactory(
            apiService = RetrofitClient.wordBuildingService,
            mistakeRepository = MistakeRepository(RetrofitClient.mistakeApiService),
            application = LocalContext.current.applicationContext as Application
        ))
) {
    val currentExercise by viewModel.currentExercise.collectAsState()
    val selectedWords by viewModel.selectedWords.collectAsState()
    val isAnswerCorrect by viewModel.isAnswerCorrect.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val hearts by viewModel.hearts.collectAsState()
    val userProgress by viewModel.userProgress.collectAsState()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()

    // Log để gỡ lỗi
    LaunchedEffect(userProgress, isAnswerCorrect) {
        Log.d("WordBuildingScreen", "User progress: $userProgress, isAnswerCorrect: $isAnswerCorrect")
    }

    // Gọi onComplete ngay khi trả lời đúng
    LaunchedEffect(isAnswerCorrect) {
        if (isAnswerCorrect == true) {
            Log.d("WordBuildingScreen", "Correct answer, calling onComplete")
            onComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Word Building",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepSeaBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F9FF))
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = DeepSeaBlue,
                        strokeWidth = 4.dp
                    )
                }
                errorMessage != null -> {
                    ErrorMessage(
                        message = errorMessage!!,
                        onRetryClick = { viewModel.loadExercise() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                currentExercise == null -> {
                    Text(
                        text = "No exercise available",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Progress and Hearts
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LinearProgressIndicator(
                                progress = userProgress,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(10.dp)
                                    .clip(CircleShape),
                                color = DeepSeaBlue,
                                trackColor = Color(0xFFD0D0D0)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Row {
                                repeat(hearts) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_heart),
                                        contentDescription = "Heart",
                                        tint = HeartRed,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }

                        // Source Text Card
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Sentence to build:",
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = currentExercise?.sourceText ?: "",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepSeaBlue,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.playSentenceAudio() },
                                            enabled = !isAudioPlaying,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (!isAudioPlaying) DeepSeaBlue.copy(alpha = 0.1f)
                                                    else Color.Gray.copy(alpha = 0.1f)
                                                )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Play Sentence",
                                                tint = if (!isAudioPlaying) DeepSeaBlue else Color.Gray,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        IconButton(
                                            onClick = { viewModel.playSlowSentenceAudio() },
                                            enabled = !isAudioPlaying,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (!isAudioPlaying) DeepSeaBlue.copy(alpha = 0.1f)
                                                    else Color.Gray.copy(alpha = 0.1f)
                                                )
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_slow_play),
                                                contentDescription = "Play Slow Sentence",
                                                tint = if (!isAudioPlaying) DeepSeaBlue else Color.Gray,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Selected Words
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Your answer:",
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                if (selectedWords.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .border(
                                                width = 1.dp,
                                                color = Color.LightGray,
                                                shape = RoundedCornerShape(8.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Tap words below to build your sentence",
                                            color = Color.Gray,
                                            fontSize = 14.sp
                                        )
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        selectedWords.forEachIndexed { index, word ->
                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = DeepSeaBlue.copy(alpha = 0.1f)
                                                ),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier
                                                    .clickable { viewModel.removeWord(index) }
                                            ) {
                                                Text(
                                                    text = word,
                                                    modifier = Modifier.padding(
                                                        horizontal = 12.dp,
                                                        vertical = 8.dp
                                                    ),
                                                    fontSize = 16.sp,
                                                    color = DeepSeaBlue,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Word Options
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Available words:",
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(4.dp)
                                ) {
                                    items(currentExercise?.wordOptions ?: emptyList()) { word ->
                                        Button(
                                            onClick = { viewModel.addWord(word) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = DeepSeaBlue
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 8.dp
                                            ),
                                            elevation = ButtonDefaults.buttonElevation(
                                                defaultElevation = 4.dp
                                            )
                                        ) {
                                            Text(
                                                text = word,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Check Answer Button
                        Button(
                            onClick = { viewModel.checkAnswer() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepSeaBlue,
                                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                            ),
                            enabled = selectedWords.isNotEmpty() && isAnswerCorrect == null,
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp
                            )
                        ) {
                            Text(
                                "Check Answer",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Answer Feedback
                        AnimatedVisibility(
                            visible = isAnswerCorrect != null,
                            enter = fadeIn(animationSpec = tween(300)),
                            exit = fadeOut(animationSpec = tween(300))
                        ) {
                            isAnswerCorrect?.let { correct ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (correct) Color(0xFFE7F8E7) else Color(0xFFFCE8E8)
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = if (correct) {
                                                "Correct! Good job!"
                                            } else {
                                                "Incorrect. Mistake saved for review."
                                            },
                                            fontSize = 18.sp,
                                            color = if (correct) Color(0xFF2E7D32) else HeartRed,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            viewModel.resetState()
                                            if (!correct) {
                                                viewModel.loadExercise()
                                            }
                                            // Không gọi onComplete ở đây vì đã gọi trong LaunchedEffect
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (correct) Color(0xFF4CAF50) else DeepSeaBlue
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 4.dp
                                        )
                                    ) {
                                        Text(
                                            if (correct) "Continue" else "Try Again",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .padding(16.dp)
            .width(300.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_mistakes),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = HeartRed
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Oops! Something went wrong",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetryClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepSeaBlue
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    "Retry",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}