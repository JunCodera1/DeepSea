package com.example.deepsea.utils

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.navigation.compose.*
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.api.SessionData
import com.example.deepsea.data.dto.LessonCompletionDto
import com.example.deepsea.data.model.exercise.LessonResult
import com.example.deepsea.data.repository.MistakeRepository
import com.example.deepsea.ui.screens.feature.learn.*
import com.example.deepsea.ui.viewmodel.home.HomeViewModel
import com.example.deepsea.ui.viewmodel.learn.*
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

    val unitId = lessonId
    val sectionId = ((lessonId - 1) / 5) + 1

    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var pausedTime by remember { mutableStateOf(0L) }
    var isPaused by remember { mutableStateOf(false) }
    var lessonResult by remember { mutableStateOf(LessonResult(xp = 0, time = "0:00", accuracy = 100)) }

    val screenAccuracies = mutableListOf<Float>()

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

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> pauseSession()
                Lifecycle.Event.ON_RESUME -> resumeSession()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

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
        Log.d("LearningSessionManager", "Initialized session: lessonId=$lessonId, unitId=$unitId, screens=$screenCount")
    }

    fun updateLessonResult(progress: Float, accuracy: Float) {
        val xp = (progress * 100).toInt()
        screenAccuracies.add(accuracy)
        val avgAccuracy = if (screenAccuracies.isNotEmpty()) screenAccuracies.average().toInt() else 100
        lessonResult = LessonResult(xp = xp, time = "0:00", accuracy = avgAccuracy)
        Log.d("LearningSessionManager", "Updated lesson result: XP=$xp, Accuracy=$avgAccuracy%")
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
                        ScreenType.WORD_BUILDING -> viewModel<WordBuildingViewModel>(
                            factory = WordBuildingViewModelFactory(
                                RetrofitClient.wordBuildingService,
                                MistakeRepository(RetrofitClient.mistakeApiService),
                                context
                            )
                        )
                        ScreenType.QUIZ_IMAGE -> viewModel<LearningViewModel>(
                            factory = LearningViewModel.Factory(context, lessonId)
                        )
                        ScreenType.MATCHING_PAIRS -> viewModel<MatchingPairsViewModel>(
                            factory = MatchingPairsViewModelFactory(sectionId, unitId)
                        )
                        ScreenType.LANGUAGE_LISTENING -> viewModel<LanguageListeningViewModel>(
                            factory = LanguageListeningViewModelFactory(
                                apiService = RetrofitClient.hearingService,
                                context = context
                            )
                        )
                    }
                    Log.d("LearningSessionManager", "Rendering screen: type=$screenType, index=$index")
                    LearningScreenWrapper(
                        sessionState = sessionState,
                        lessonId = lessonId,
                        screenType = screenType,
                        viewModel = viewModel,
                        onUpdateProgress = { newProgress, accuracy ->
                            val screenProgress = 1f / sessionState.totalScreens
                            val totalProgress = (sessionState.currentScreenIndex * screenProgress) + (newProgress * screenProgress)
                            sessionState = sessionState.copy(progress = totalProgress.coerceIn(0f, 1f))
                            updateLessonResult(totalProgress, accuracy)
                            Log.d("LearningSessionManager", "Progress updated: total=$totalProgress, accuracy=$accuracy")
                        },
                        onNextScreen = {
                            Log.d("LearningSessionManager", "Next screen requested: currentIndex=${sessionState.currentScreenIndex}, totalScreens=${sessionState.totalScreens}")
                            val nextIndex = sessionState.currentScreenIndex + 1
                            if (nextIndex < sessionState.totalScreens) {
                                sessionState = sessionState.copy(currentScreenIndex = nextIndex)
                                try {
                                    navControllerLocal.navigate("learning_screen/$nextIndex") {
                                        popUpTo(navControllerLocal.graph.startDestinationId) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                    Log.d("LearningSessionManager", "Navigated to screen $nextIndex")
                                } catch (e: Exception) {
                                    Log.e("LearningSessionManager", "Navigation failed: ${e.message}")
                                }
                            } else {
                                Log.d("LearningSessionManager", "All screens completed for lessonId=$lessonId, unitId=$unitId")
                                coroutineScope.launch {
                                    saveSessionProgress(lessonId, sessionState)
                                    val elapsedMillis = if (isPaused) (pausedTime - startTime) else (System.currentTimeMillis() - startTime)
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
                                        val completedStars = homeViewModel.completedStars.value[unitId] ?: emptySet()
                                        val starIndex = completedStars.size
                                        Log.d("LearningSessionManager", "Completing star: unitId=$unitId, starIndex=$starIndex, completedStars=$completedStars")
                                        if (starIndex < 5) {
                                            homeViewModel.completeStar(unitId, starIndex, earnedXp = lessonResult.xp)
                                            Log.d("LearningSessionManager", "Star $starIndex completed for unit $unitId")
                                        } else {
                                            Log.w("LearningSessionManager", "Star index $starIndex exceeds max (5) for unit $unitId")
                                        }
                                        homeViewModel.updateDailyStreak()
                                        Log.d("LearningSessionManager", "Streak updated for userId=$userId")
                                    } catch (e: Exception) {
                                        Log.e("LearningSessionManager", "Lesson completion failed: ${e.message}")
                                        Toast.makeText(context, "Failed to complete lesson: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }

                                    try {
                                        navController.navigate("lesson_completed/${lessonResult.xp}/${lessonResult.time}/${lessonResult.accuracy}/$lessonId") {
                                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                        }
                                        Log.d("LearningSessionManager", "Navigated to lesson_completed screen")
                                    } catch (e: Exception) {
                                        Log.e("LearningSessionManager", "Navigation to lesson_completed failed: ${e.message}")
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
            modifier = Modifier.fillMaxSize().padding(16.dp),
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
    repeat(count) { screens.add(types.random()) }
    return screens
}

@Composable
fun LearningScreenWrapper(
    sessionState: SessionState,
    lessonId: Long,
    screenType: ScreenType,
    viewModel: Any?,
    onUpdateProgress: (Float, Float) -> Unit,
    onNextScreen: () -> Unit,
    onBack: () -> Unit
) {
    val sectionId = ((lessonId - 1) / 5) + 1
    val unitId = lessonId

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        LinearProgressIndicator(
            progress = sessionState.progress,
            modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(8.dp)),
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
                        onUpdateProgress(1f, matchingViewModel.accuracy.value)
                        onNextScreen()
                    }
                )
                LaunchedEffect(matchingViewModel.progress.collectAsState().value, matchingViewModel.accuracy.collectAsState().value) {
                    onUpdateProgress(matchingViewModel.progress.value, matchingViewModel.accuracy.value)
                }
            }
            ScreenType.QUIZ_IMAGE -> {
                val learningViewModel = viewModel as LearningViewModel
                QuizImageScreen(
                    lessonId = lessonId,
                    onBack = onBack,
                    onComplete = {
                        onUpdateProgress(1f, learningViewModel.accuracy.value)
                        onNextScreen()
                    }
                )
                LaunchedEffect(learningViewModel.progress.collectAsState().value, learningViewModel.accuracy.collectAsState().value) {
                    onUpdateProgress(learningViewModel.progress.value, learningViewModel.accuracy.value)
                }
            }
            ScreenType.WORD_BUILDING -> {
                val wordBuildingViewModel = viewModel as WordBuildingViewModel
                WordBuildingScreen(
                    viewModel = wordBuildingViewModel,
                    onNavigateToSettings = onBack,
                    onComplete = {
                        onUpdateProgress(1f, wordBuildingViewModel.accuracy.value)
                        onNextScreen()
                    },
                    onBackClick = onBack
                )
                LaunchedEffect(wordBuildingViewModel.userProgress.collectAsState().value, wordBuildingViewModel.accuracy.collectAsState().value) {
                    onUpdateProgress(wordBuildingViewModel.userProgress.value, wordBuildingViewModel.accuracy.value)
                }
            }
            ScreenType.LANGUAGE_LISTENING -> {
                LanguageListeningScreen(
                    sectionId = sectionId,
                    unitId = unitId,
                    onNavigateToSettings = onBack,
                    onComplete = {
                        val languageListeningViewModel = viewModel as LanguageListeningViewModel
                        onUpdateProgress(1f, languageListeningViewModel.accuracy.value)
                        onNextScreen()
                    }
                )
            }
        }
    }
}

private suspend fun saveSessionProgress(lessonId: Long, sessionState: SessionState) {
    val sessionData = SessionData(
        lessonId = lessonId,
        totalScreens = sessionState.totalScreens,
        completedScreens = sessionState.currentScreenIndex + 1,
        screensCompleted = sessionState.screens.take(sessionState.currentScreenIndex + 1).map { it.name }
    )
    try {
        RetrofitClient.sessionApiService.saveSession(sessionData)
        Log.d("LearningSessionManager", "Session progress saved for lessonId=$lessonId")
    } catch (e: Exception) {
        Log.e("LearningSessionManager", "Failed to save session: ${e.message}")
    }
}