package com.example.deepsea.ui.screens.feature.learn

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Word Building") },
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
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = DeepSeaBlue
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
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Progress and Hearts
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            LinearProgressIndicator(
                                progress = userProgress,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp),
                                color = DeepSeaBlue
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Row {
                                repeat(hearts) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_heart),
                                        contentDescription = "Heart",
                                        tint = HeartRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        // Source Text and Audio Buttons
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
                            IconButton(
                                onClick = { viewModel.playSentenceAudio() },
                                enabled = !isAudioPlaying
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play Sentence",
                                    tint = DeepSeaBlue
                                )
                            }
                            IconButton(
                                onClick = { viewModel.playSlowSentenceAudio() },
                                enabled = !isAudioPlaying
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_slow_play),
                                    contentDescription = "Play Slow Sentence",
                                    tint = DeepSeaBlue
                                )
                            }
                        }

                        // Selected Words
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                selectedWords.forEachIndexed { index, word ->
                                    Text(
                                        text = word,
                                        modifier = Modifier
                                            .background(
                                                color = Color.LightGray,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(4.dp)
                                            .clickable { viewModel.removeWord(index) },
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        // Word Options
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(currentExercise?.wordOptions ?: emptyList()) { word ->
                                Button(
                                    onClick = { viewModel.addWord(word) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DeepSeaBlue
                                    )
                                ) {
                                    Text(word)
                                }
                            }
                        }

                        // Check Answer Button
                        Button(
                            onClick = { viewModel.checkAnswer() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepSeaBlue
                            ),
                            enabled = selectedWords.isNotEmpty()
                        ) {
                            Text("Check Answer")
                        }

                        // Answer Feedback
                        isAnswerCorrect?.let { correct ->
                            Text(
                                text = if (correct) {
                                    "Correct!"
                                } else {
                                    "Incorrect. Mistake saved for review."
                                },
                                fontSize = 18.sp,
                                color = if (correct) Color.Green else HeartRed,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    viewModel.resetState()
                                    viewModel.loadExercise()
                                    if (correct && viewModel.userProgress.value >= 1f) {
                                        onComplete()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DeepSeaBlue
                                )
                            ) {
                                Text("Next Exercise")
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
    Column(
        modifier = modifier,
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
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetryClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = DeepSeaBlue
            )
        ) {
            Text("Retry")
        }
    }
}