package com.example.deepsea.utils

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.api.SessionData
import com.example.deepsea.data.dto.LessonCompletionDto
import com.example.deepsea.data.model.exercise.LessonResult
import com.example.deepsea.data.repository.MistakeRepository
import com.example.deepsea.ui.screens.feature.learn.LanguageListeningScreen
import com.example.deepsea.ui.screens.feature.learn.MatchingPairsScreen
import com.example.deepsea.ui.screens.feature.learn.MatchingPairsViewModel
import com.example.deepsea.ui.screens.feature.learn.QuizImageScreen
import com.example.deepsea.ui.screens.feature.learn.WordBuildingScreen
import com.example.deepsea.ui.viewmodel.home.HomeViewModel
import com.example.deepsea.ui.viewmodel.learn.LearningViewModel
import com.example.deepsea.ui.viewmodel.learn.LessonViewModel
import com.example.deepsea.ui.viewmodel.learn.WordBuildingViewModel
import com.example.deepsea.ui.viewmodel.learn.WordBuildingViewModelFactory
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun LearningSessionManager(
    modifier: Modifier = Modifier,
    lessonId: Long,
    navController: NavController,
    homeViewModel: HomeViewModel,
    onComplete: () -> Unit
) {
    val navControllerLocal = rememberNavController()
    var sessionState by remember { mutableStateOf(SessionState()) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current.applicationContext as Application
    val lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.LessonViewModelFactory())
    val lifecycleOwner = LocalLifecycleOwner.current

    // Time tracking
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var pausedTime by remember { mutableStateOf(0L) }
    var isPaused by remember { mutableStateOf(false) }
    var lessonResult by remember { mutableStateOf(LessonResult(xp = 0, time = "0:00", accuracy = 100)) }

    // Pause/resume functions
    fun pauseSession() {
        if (!isPaused) {
            isPaused = true
            pausedTime = System.currentTimeMillis()
        }
    }

    fun resumeSession() {
        if (isPaused) {
            startTime += (System.currentTimeMillis() - pausedTime)
            isPaused = false
            pausedTime = 0L
        }
    }

    // Lifecycle observer for pause/resume
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> pauseSession()
                Lifecycle.Event.ON_RESUME -> resumeSession()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Initialize session
    LaunchedEffect(Unit) {
        val screenCount = Random.nextInt(3, 5)
        val screens = generateRandomScreens(screenCount)
        sessionState = sessionState.copy(
            totalScreens = screenCount,
            screens = screens,
            currentScreenIndex = 0,
            progress = 0f
        )
        startTime = System.currentTimeMillis()
        Log.d("LearningSessionManager", "Initialized session with $screenCount screens")
    }

    // Update LessonResult
    fun updateLessonResult(progress: Float) {
        val xp = (progress * 100).toInt()
        val accuracy = (progress * 100).toInt()
        lessonResult = LessonResult(xp = xp, time = "0:00", accuracy = accuracy)
    }

    if (sessionState.screens.isNotEmpty()) {
        NavHost(
            navController = navControllerLocal,
            startDestination = "learning_screen/0",
            modifier = modifier
        ) {
            sessionState.screens.forEachIndexed { index, screenType ->
                composable("learning_screen/$index") {
                    val viewModel = when (screenType) {
                        ScreenType.WORD_BUILDING -> {
                            viewModel<WordBuildingViewModel>(
                                factory = WordBuildingViewModelFactory(
                                    RetrofitClient.wordBuildingService,
                                    MistakeRepository(RetrofitClient.mistakeApiService),
                                    context
                                )
                            )
                        }
                        ScreenType.QUIZ_IMAGE -> viewModel<LearningViewModel>(
                            factory = LearningViewModel.Factory(context, lessonId)
                        )
                        ScreenType.MATCHING_PAIRS -> viewModel<MatchingPairsViewModel>()
                        else -> null
                    }
                    LearningScreenWrapper(
                        sessionState = sessionState,
                        lessonId = lessonId,
                        screenType = screenType,
                        viewModel = viewModel,
                        onUpdateProgress = { newProgress ->
                            val screenProgress = 1f / sessionState.totalScreens
                            val totalProgress = (sessionState.currentScreenIndex * screenProgress) + (newProgress * screenProgress)
                            sessionState = sessionState.copy(progress = totalProgress.coerceIn(0f, 1f))
                            updateLessonResult(totalProgress)
                            Log.d("LearningSessionManager", "Updated session progress: $totalProgress")
                        },
                        onNextScreen = {
                            Log.d("LearningSessionManager", "Navigating to next screen: ${sessionState.currentScreenIndex + 1}, totalScreens: ${sessionState.totalScreens}")
                            val nextIndex = sessionState.currentScreenIndex + 1
                            if (nextIndex < sessionState.totalScreens) {
                                sessionState = sessionState.copy(currentScreenIndex = nextIndex)
                                try {
                                    navControllerLocal.navigate("learning_screen/$nextIndex") {
                                        popUpTo(navControllerLocal.graph.startDestinationId) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                    Log.d("LearningSessionManager", "Navigation successful to screen $nextIndex")
                                } catch (e: Exception) {
                                    Log.e("LearningSessionManager", "Navigation failed: ${e.message}")
                                }
                            } else {
                                coroutineScope.launch {
                                    saveSessionProgress(lessonId, sessionState)
                                    val elapsedMillis = if (isPaused) {
                                        (pausedTime - startTime)
                                    } else {
                                        (System.currentTimeMillis() - startTime)
                                    }
                                    val elapsedSeconds = elapsedMillis / 1000
                                    val finalTime = "${elapsedSeconds / 60}:${(elapsedSeconds % 60).toString().padStart(2, '0')}"
                                    lessonResult = lessonResult.copy(time = finalTime)

                                    lessonViewModel.saveLessonResult(
                                        xp = lessonResult.xp,
                                        time = lessonResult.time,
                                        accuracy = lessonResult.accuracy
                                    )

                                    try {
                                        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                        val userId = sharedPreferences.getLong("user_id", 1L)
                                        RetrofitClient.lessonApi.completeLesson(
                                            lessonId,
                                            userId,
                                            LessonCompletionDto(
                                                score = lessonResult.accuracy,
                                                timeTaken = lessonResult.time
                                            )
                                        )
                                        homeViewModel.updateDailyStreak()
                                        Log.d("LearningSessionManager", "Streak updated for userId: $userId")
                                    } catch (e: Exception) {
                                        Log.e("LearningSessionManager", "Failed to complete lesson: ${e.message}")
                                        Toast.makeText(context, "Failed to complete lesson: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }

                                    navController.navigate("lesson_completed/${lessonResult.xp}/${lessonResult.time}/${lessonResult.accuracy}/$lessonId") {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = true
                                        }
                                    }
                                    onComplete()
                                }
                            }
                        },
                        onBack = { navControllerLocal.popBackStack() }
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading session...", fontSize = 16.sp)
        }
    }
}

data class SessionState(
    val totalScreens: Int = 0,
    val currentScreenIndex: Int = 0,
    val screens: List<ScreenType> = emptyList(),
    val progress: Float = 0f
)

enum class ScreenType {
    MATCHING_PAIRS, QUIZ_IMAGE, WORD_BUILDING, LANGUAGE_LISTENING
}

fun generateRandomScreens(count: Int): List<ScreenType> {
    val screens = mutableListOf<ScreenType>()
    val types = ScreenType.values()
    repeat(count) {
        screens.add(types.random())
    }
    return screens
}

@Composable
fun LearningScreenWrapper(
    sessionState: SessionState,
    lessonId: Long,
    screenType: ScreenType,
    viewModel: Any?,
    onUpdateProgress: (Float) -> Unit,
    onNextScreen: () -> Unit,
    onBack: () -> Unit
) {
    val sectionId = ((lessonId - 1) / 5) + 1
    val unitId = lessonId

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LinearProgressIndicator(
            progress = sessionState.progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = Color(0xFF58CC02),
            trackColor = Color.LightGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (screenType) {
            ScreenType.MATCHING_PAIRS -> {
                val matchingViewModel = viewModel as MatchingPairsViewModel
                MatchingPairsScreen(
                    viewModel = matchingViewModel,
                    onNavigateToSettings = onBack,
                    onComplete = {
                        onUpdateProgress(1f)
                        onNextScreen()
                    }
                )
                LaunchedEffect(matchingViewModel.progress.collectAsState().value) {
                    onUpdateProgress(matchingViewModel.progress.value)
                }
            }
            ScreenType.QUIZ_IMAGE -> {
                val learningViewModel = viewModel as LearningViewModel
                QuizImageScreen(
                    lessonId = lessonId,
                    onBack = onBack,
                    onComplete = {
                        onUpdateProgress(1f)
                        onNextScreen()
                    }
                )
                LaunchedEffect(learningViewModel.progress.collectAsState().value) {
                    onUpdateProgress(learningViewModel.progress.value)
                }
            }
            ScreenType.WORD_BUILDING -> {
                val wordBuildingViewModel = viewModel as WordBuildingViewModel
                WordBuildingScreen(
                    viewModel = wordBuildingViewModel,
                    onNavigateToSettings = onBack,
                    onComplete = {
                        onUpdateProgress(1f)
                        onNextScreen()
                    },
                    onBackClick = onBack
                )
                LaunchedEffect(wordBuildingViewModel.userProgress.collectAsState().value) {
                    val progress = wordBuildingViewModel.userProgress.value
                    Log.d("LearningScreenWrapper", "Updating progress: $progress")
                    onUpdateProgress(progress)
                }
            }
            ScreenType.LANGUAGE_LISTENING -> LanguageListeningScreen(
                sectionId = sectionId,
                unitId = unitId,
                onNavigateToSettings = onBack,
                onComplete = {
                    onUpdateProgress(1f)
                    onNextScreen()
                }
            )
        }
    }
}

private suspend fun saveSessionProgress(lessonId: Long, sessionState: SessionState) {
    val sessionData = SessionData(
        lessonId = lessonId,
        totalScreens = sessionState.totalScreens,
        completedScreens = sessionState.currentScreenIndex + 1,
        screensCompleted = sessionState.screens.take(sessionState.currentScreenIndex + 1)
            .map { it.name }
    )
    try {
        RetrofitClient.sessionApiService.saveSession(sessionData)
    } catch (e: Exception) {
        Log.e("LearningSessionManager", "Failed to save session: ${e.message}")
    }
}