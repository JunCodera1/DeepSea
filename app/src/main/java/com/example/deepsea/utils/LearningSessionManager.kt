package com.example.deepsea.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.api.SessionData
import com.example.deepsea.ui.screens.feature.learn.LanguageListeningScreen
import com.example.deepsea.ui.screens.feature.learn.MatchingPairsScreen
import com.example.deepsea.ui.screens.feature.learn.QuizImageScreen
import com.example.deepsea.ui.screens.feature.learn.WordBuildingScreen
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun LearningSessionManager(
    modifier: Modifier = Modifier,
    lessonId: Long,
    onComplete: () -> Unit
) {
    val navController = rememberNavController()
    var sessionState by remember { mutableStateOf(SessionState()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val screenCount = Random.nextInt(10, 20) // Random between 10 and 19
        val screens = generateRandomScreens(screenCount)
        sessionState = sessionState.copy(
            totalScreens = screenCount,
            screens = screens,
            currentScreenIndex = 0
        )
    }

    NavHost(
        navController = navController,
        startDestination = "learning_screen",
        modifier = modifier
    ) {
        composable("learning_screen") {
            LearningScreenWrapper(
                sessionState = sessionState,
                lessonId = lessonId,
                onNextScreen = {
                    val nextIndex = sessionState.currentScreenIndex + 1
                    if (nextIndex < sessionState.totalScreens) {
                        sessionState = sessionState.copy(currentScreenIndex = nextIndex)
                        navController.navigate("learning_screen")
                    } else {
                        coroutineScope.launch {
                            saveSessionProgress(lessonId, sessionState)
                            onComplete()
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
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
    onNextScreen: () -> Unit,
    onBack: () -> Unit
) {
    // Map lessonId to sectionId and unitId
    val sectionId = ((lessonId - 1) / 5) + 1
    val unitId = lessonId

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LinearProgressIndicator(
            progress = if (sessionState.totalScreens > 0) {
                (sessionState.currentScreenIndex + 1).toFloat() / sessionState.totalScreens
            } else 0f,
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = Color(0xFF58CC02),
            trackColor = Color.LightGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (sessionState.screens.getOrNull(sessionState.currentScreenIndex)) {
            ScreenType.MATCHING_PAIRS -> MatchingPairsScreen(onComplete = onNextScreen)
            ScreenType.QUIZ_IMAGE -> QuizImageScreen(
                lessonId = lessonId,
                onBack = onBack,
                onComplete = onNextScreen
            )
            ScreenType.WORD_BUILDING -> WordBuildingScreen(onComplete = onNextScreen)
            ScreenType.LANGUAGE_LISTENING -> LanguageListeningScreen(
                sectionId = sectionId,
                unitId = unitId,
                onNavigateToSettings = { /* Navigate to settings screen or no-op */ },
                onComplete = onNextScreen
            )
            null -> Text("Session Complete", fontSize = 24.sp, fontWeight = FontWeight.Bold)
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
        println("Failed to save session: ${e.message}")
    }
}