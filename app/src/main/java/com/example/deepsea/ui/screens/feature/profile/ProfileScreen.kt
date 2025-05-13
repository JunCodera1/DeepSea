package com.example.deepsea.ui.screens.feature.profile

import UserProfileViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.api.UserProfileService
import com.example.deepsea.data.model.course.language.LanguageOption
import com.example.deepsea.data.model.course.path.PathOption
import com.example.deepsea.data.model.user.UserProfileData
import com.example.deepsea.ui.components.AchievementCard
import com.example.deepsea.ui.components.FriendSuggestionCard
import com.example.deepsea.ui.components.InviteFriendsCard
import com.example.deepsea.ui.components.ProfileHeader
import com.example.deepsea.ui.components.StatisticsCard
import com.example.deepsea.ui.components.UserBasicInfoCard
import com.example.deepsea.ui.viewmodel.course.path.PathSelectionViewModel
import com.example.deepsea.ui.viewmodel.course.path.PathSelectionViewModelFactory
import com.example.deepsea.utils.SessionManager

@Composable
fun ProfilePage(
    sessionManager: SessionManager,
    viewModel: UserProfileViewModel = viewModel(),
    paddingValues: PaddingValues,
    onNavigateToSettings: () -> Unit
) {
    val userId by sessionManager.userId.collectAsState(initial = null)
    val userProfile by viewModel.userProfileData
    val pathService: UserProfileService = RetrofitClient.userProfileService
    val profileId by sessionManager.profileId.collectAsState(initial = null)
    val factory = remember { PathSelectionViewModelFactory(pathService, sessionManager) }
    val pathViewModel: PathSelectionViewModel = viewModel(factory = factory)
    LaunchedEffect(profileId) {
        profileId?.let {
            pathViewModel.fetchPaths(it)
        }
    }

    val userPaths = pathViewModel.userPaths

    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.fetchUserProfile(userId!!)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F7F9)
    ) {
        // Thay thế verticalScroll bằng LazyColumn để cải thiện hiệu suất
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(paddingValues),
        ) {
            // Profile Header
            item {
                ProfileHeader(onNavigateToSettings)
            }

            // Basic Info Card - chỉ render lại khi userProfile thay đổi
            item {
                UserBasicInfoCard(userProfile)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                // Languages section
                Text(
                    text = "Languages",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Languages row
                LanguageFlags(userPaths) { /* Language selection handler */ }

                Spacer(modifier = Modifier.height(16.dp))

                // Statistics section
                Text(
                    text = "Statistics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Statistics Card
            item {
                StatisticsCard(userProfile)
            }

            // Friend suggestions section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Friend suggestions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                FriendSuggestionCard()
            }

            // Friends section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                FriendsSection()
            }

            // Invite friends section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                InviteFriendsCard()
            }

            // Achievements section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                AchievementsSection(userProfile)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun StatisticItem(
    value: String,
    label: String,
    backgroundColor: Color,
    textColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(backgroundColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}



@Composable
fun FriendsSection() {
    // Friends heading with add button
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Friends",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        TextButton(onClick = { /* Add friends */ }) {
            Text(
                text = "ADD FRIENDS",
                color = Color(0xFF4DB6FF),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    // Friend tabs
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "FOLLOWING",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4DB6FF)
        )
        Text(
            text = "FOLLOWERS",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }

    Divider(color = Color.LightGray)

    // Friends illustration card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Placeholder for friends illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFFE8EAF6), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Friends Illustration")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Learning is more fun and effective when you connect with others.",
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
        }
    }
}



@Composable
fun AchievementsSection(userProfile: UserProfileData?) {
    // Header
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Achievements",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        TextButton(onClick = { /* View all achievements */ }) {
            Text(
                text = "View all",
                color = Color(0xFF4DB6FF),
                fontSize = 14.sp
            )
        }
    }

    // Achievement cards
    AchievementCard(
        title = "Wildfire",
        description = "Reach a 3 day streak",
        progress = "1/3",
        backgroundColor = Color(0xFFFFCDD2),
        iconColor = Color(0xFFF44336)
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Calculate XP progress string
    val xpProgress = "${userProfile?.totalXp ?: 0}/1000"

    AchievementCard(
        title = "Sage",
        description = "Earn 1000 XP",
        progress = xpProgress,
        backgroundColor = Color(0xFFDCEDC8),
        iconColor = Color(0xFF8BC34A)
    )

    Spacer(modifier = Modifier.height(8.dp))

    AchievementCard(
        title = "Scholar",
        description = "Learn 100 new words in a single course",
        progress = "100/175",
        backgroundColor = Color(0xFFFFECB3),
        iconColor = Color(0xFFFFA000)
    )
}



@Composable
fun LanguageFlags(
    userPaths: Map<LanguageOption, PathOption>,
    onAddLanguage: (String) -> Unit
) {
    var showLanguageDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            userPaths.forEach { (langOption, pathOption) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = langOption.flagResId),
                        contentDescription = langOption.displayName,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = langOption.displayName,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = pathOption.name,
                        fontSize = 10.sp,
                        color = Color(0xFF4DB6FF),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Add language button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { showLanguageDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, Color.LightGray, CircleShape)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Language",
                        tint = Color(0xFF4DB6FF)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Add", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}



// Example ViewModel class for reference
/*
class UserProfileViewModel : ViewModel() {
    private val _userProfileData = mutableStateOf<UserProfileData?>(null)
    val userProfileData: State<UserProfileData?> = _userProfileData

    fun fetchUserProfile(userId: String) {
        // Thực hiện fetch và cập nhật _userProfileData
    }
}
*/