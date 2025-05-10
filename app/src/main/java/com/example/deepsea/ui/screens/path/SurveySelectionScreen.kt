package com.example.deepsea.ui.screens.path

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.deepsea.R
import com.example.deepsea.data.api.UserProfileService
import com.example.deepsea.data.model.course.path.UserPathDto
import com.example.deepsea.data.model.user.FriendSuggestion
import com.example.deepsea.data.model.course.language.LanguageOptionRequest
import com.example.deepsea.data.model.course.path.PathOptionRequest
import com.example.deepsea.data.model.goal.DailyGoalRequest
import com.example.deepsea.data.model.leaderboard.LeaderboardEntry
import com.example.deepsea.data.model.leaderboard.LeaderboardRankResponse
import com.example.deepsea.data.model.survey.SurveyOption
import com.example.deepsea.data.model.survey.SurveyOptionRequest
import com.example.deepsea.data.model.survey.SurveyOptionResponse
import com.example.deepsea.data.model.user.UserProfileData
import com.example.deepsea.data.repository.UserProfileRepository
import com.example.deepsea.ui.viewmodel.survey.SurveySelectionViewModel
import com.example.deepsea.utils.SessionManager
import retrofit2.Response

@Composable
fun SurveySelectionPage(
    navController: NavController,
    surveySelectionViewModel: SurveySelectionViewModel,
    sessionManager: SessionManager
) {
    val userId by sessionManager.userId.collectAsState(initial = null)

    // State to track selected survey options
    val selectedSurveys by surveySelectionViewModel.selectedSurveys.collectAsState()
    val scrollState = rememberScrollState()

    // Purple color for selected state
    val purpleColor = Color(0xFF6750A4)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
    ) {
        // Top bar with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(45.dp)
                    .clickable { navController.popBackStack() }
            )

            LinearProgressIndicator(
                progress = 1f,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                color = Color(0xFF8CC83C),
                trackColor = Color(0xFFEEEEEE)
            )
        }

        // Mascot and speech bubble
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Green owl mascot
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Mascot",
                    modifier = Modifier
                        .size(100.dp)
                )

                // Speech bubble
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "How did you hear about DeepSea? (Select all that apply)",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 18.sp
                    )
                }
            }
        }

        // Header
        Text(
            text = "Select your answers",
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        // Selected options counter
        if (selectedSurveys.isNotEmpty()) {
            Text(
                text = "${selectedSurveys.size} option${if (selectedSurveys.size > 1) "s" else ""} selected",
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                fontSize = 16.sp,
                color = purpleColor
            )
        }

        // Map of survey options to their corresponding drawable resources
        val surveyIconMap = mapOf(
            SurveyOption.FRIENDS to R.drawable.ic_friends,
            SurveyOption.TV to R.drawable.ic_tv,
            SurveyOption.TIKTOK to R.drawable.ic_tiktok,
            SurveyOption.NEWS to R.drawable.ic_news,
            SurveyOption.YOUTUBE to R.drawable.ic_youtube,
            SurveyOption.SOCIAL to R.drawable.ic_social,
            SurveyOption.OTHER to R.drawable.ic_other
        )

        // Render all survey options
        SurveyOption.values().forEach { option ->
            SurveyOptionItem(
                option = option,
                iconResId = surveyIconMap[option] ?: R.drawable.ic_other, // Fallback icon
                isSelected = selectedSurveys.contains(option),
                onSelect = { surveySelectionViewModel.toggleSurveySelection(option) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Continue button - changes color when at least one option is selected
        val isAnyOptionSelected = selectedSurveys.isNotEmpty()
        Button(
            onClick = {
                if (isAnyOptionSelected) {
                    surveySelectionViewModel.saveSurveySelections(userId = userId)
                    navController.navigate("home")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAnyOptionSelected) purpleColor else Color.LightGray
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = isAnyOptionSelected
        ) {
            Text("CONTINUE", fontSize = 16.sp)
        }
    }
}

@Composable
fun SurveyOptionItem(
    option: SurveyOption,
    iconResId: Int,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val purpleColor = Color(0xFF6750A4)
    val cardModifier = if (isSelected) {
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(2.dp, purpleColor, RoundedCornerShape(16.dp))
            .clickable { onSelect() }
    } else {
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onSelect() }
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = option.displayName,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            // Survey option name
            Text(
                text = option.displayName,
                modifier = Modifier.padding(start = 16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            // Add a checkmark icon when selected
            if (isSelected) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = "Selected",
                    tint = purpleColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SurveySelectionPreview() {
    // For preview only - mock objects
    val mockNavController = rememberNavController()
    val mockService = MockUserProfileService()
    val mockRepository = UserProfileRepository(mockService)
    val viewModel = SurveySelectionViewModel(mockRepository)
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    SurveySelectionPage(
        navController = mockNavController,
        surveySelectionViewModel = viewModel,
        sessionManager = sessionManager)
}

// Mock class for preview
class MockUserProfileService : UserProfileService {
    override suspend fun getUserProfileById(id: Long?): UserProfileData {
        TODO("Not yet implemented")
    }

    override suspend fun getFriendSuggestions(): List<FriendSuggestion> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserProfile(userId: Long): Response<UserProfileData> {
        throw NotImplementedError("Preview mock")
    }

    override fun updateSurveyOption(updateRequest: SurveyOptionRequest): Response<SurveyOptionResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun saveSurveySelections(request: SurveyOptionRequest): UserProfileData {
        return UserProfileData(
            name = "Preview User",
            username = "preview_user",
            followers = 0,
            following = 0,
            dayStreak = 0,
            totalXp = 0,
            currentLeague = "Bronze",
            topFinishes = 0
        )
    }

    override suspend fun saveLanguageSelections(request: LanguageOptionRequest): UserProfileData {
        TODO("Not yet implemented")
    }

    override suspend fun savePath(request: PathOptionRequest): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserPaths(userId: Long): List<UserPathDto> {
        TODO("Not yet implemented")
    }

    override suspend fun updateDailyGoal(
        profileId: Long?,
        dailyGoal: DailyGoalRequest
    ): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getTopLeaderboard(): List<LeaderboardEntry> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllUsersSortedByXp(): List<UserProfileData> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserRank(userId: Long): LeaderboardRankResponse {
        TODO("Not yet implemented")
    }
}