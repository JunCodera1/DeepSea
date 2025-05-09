package com.example.deepsea.ui.screens.feature.leaderboard

import LeaderboardViewModelFactory
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deepsea.R
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.leaderboard.LeaderboardEntry
import com.example.deepsea.data.model.user.UserProfileData
import com.example.deepsea.ui.viewmodel.leaderboard.LeaderboardViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable


@Composable
fun LeaderboardPage(currentUserId: Long = 4) {
    val tiers = LeagueTier.values()
    val pagerState = rememberPagerState(pageCount = { tiers.size }, initialPage = 3) // Start with Diamond
    val coroutineScope = rememberCoroutineScope()
    val viewModel: LeaderboardViewModel = viewModel(
        factory = LeaderboardViewModelFactory(RetrofitClient.userProfileService)
    )

    // Tab state
    val currentTab = remember { mutableStateOf(LeaderboardTab.TOP) }

    // Load data when the screen appears
    LaunchedEffect(key1 = Unit) {
        viewModel.refreshAllData(currentUserId)
    }

    // Collect state from Flows
    val topLeaderboard by viewModel.topLeaderboardState.collectAsState()
    val allUsers by viewModel.allUsersState.collectAsState()
    val userRankMap by viewModel.userRankState.collectAsState()
    val userRank = remember(userRankMap) { userRankMap[currentUserId] }

    // Loading states
    val isLoadingTop by viewModel.isLoadingTop.collectAsState()
    val isLoadingAll by viewModel.isLoadingAll.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Show error message if present
            errorMessage?.let { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = message,
                            color = Color.Red,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { viewModel.refreshAllData(currentUserId) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            // League Tiers Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val currentLeague = tiers[page]
                LeagueHeaderWithAnimation(
                    leagueTier = currentLeague,
                    daysLeft = 7,
                    message = "The next Tournament begins soon"
                )
            }

            // Rank Indicators
            Row(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                tiers.forEachIndexed { index, _ ->
                    val isSelected = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isSelected) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) tiers[pagerState.currentPage].color
                                else Color.Gray.copy(alpha = 0.3f)
                            )
                            .clickable(onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            })
                    )
                }
            }

            // Tab selector
            LeaderboardTabs(
                currentTab = currentTab.value,
                onTabSelected = { currentTab.value = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // User's personal rank card
            userRank?.let { user ->
                UserRankCard(
                    rank = user.rank,
                    username = user.username,
                    points = user.totalXp,
                    isCurrentUser = true,
                    profilePicture = R.drawable.avatar_placeholder // Use default avatar for all users
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Leaderboard List
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    when {
                        isLoadingTop && currentTab.value == LeaderboardTab.TOP -> {
                            LoadingIndicator(tiers[pagerState.currentPage].color)
                        }
                        isLoadingAll && currentTab.value == LeaderboardTab.ALL -> {
                            LoadingIndicator(tiers[pagerState.currentPage].color)
                        }
                        else -> {
                            when (currentTab.value) {
                                LeaderboardTab.TOP -> {
                                    TopLeaderboardList(
                                        leaderboard = topLeaderboard,
                                        currentUserId = currentUserId
                                    )
                                }
                                LeaderboardTab.ALL -> {
                                    AllUsersList(
                                        users = allUsers,
                                        currentUserId = currentUserId
                                    )
                                }
                            }
                        }
                    }

                    // Refresh button
                    SmallFloatingActionButton(
                        onClick = { viewModel.refreshAllData(currentUserId) },
                        containerColor = tiers[pagerState.currentPage].color,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingIndicator(color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = color)
    }
}

@Composable
fun TopLeaderboardList(
    leaderboard: List<LeaderboardEntry>,
    currentUserId: Long
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 500.dp)
    ) {
        itemsIndexed(leaderboard) { index, entry ->
            LeaderboardEntryItem(
                rank = index + 1, // đánh số thứ hạng bắt đầu từ 1
                username = entry.username,
                points = entry.totalXp,
                isCurrentUser = true,
                profilePicture = R.drawable.avatar_placeholder
            )
        }
    }

}

@Composable
fun AllUsersList(
    users: List<UserProfileData>,
    currentUserId: Long
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 500.dp)
    ) {
        items(users.indices.toList()) { index ->
            val user = users[index]
            LeaderboardEntryItem(
                rank = index + 1,
                username = user.username,
                points = user.totalXp, // Use totalXp instead of experiencePoints
                isCurrentUser = false, // We don't have user IDs to compare in the current model
                profilePicture = R.drawable.avatar_placeholder // Use default avatar consistently
            )
        }
    }
}

@Composable
fun LeaderboardTabs(
    currentTab: LeaderboardTab,
    onTabSelected: (LeaderboardTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFEEEEEE), RoundedCornerShape(24.dp)),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LeaderboardTabItem(
            title = "Top 100",
            isSelected = currentTab == LeaderboardTab.TOP,
            onClick = { onTabSelected(LeaderboardTab.TOP) }
        )
        LeaderboardTabItem(
            title = "All Players",
            isSelected = currentTab == LeaderboardTab.ALL,
            onClick = { onTabSelected(LeaderboardTab.ALL) }
        )
    }
}

@Composable
fun LeaderboardTabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0xFF2196F3) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else Color.Black.copy(alpha = 0.6f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun LeagueHeaderWithAnimation(
    leagueTier: LeagueTier,
    daysLeft: Int,
    message: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // League Tier Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(leagueTier.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = leagueTier.iconResId),
                    contentDescription = leagueTier.name,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // League Name
            AnimatedContent(
                targetState = leagueTier.name,
                transitionSpec = {
                    slideInHorizontally(animationSpec = tween(300)) { width -> width } togetherWith
                            slideOutHorizontally(animationSpec = tween(300)) { width -> -width }
                }
            ) { league ->
                Text(
                    text = league,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = leagueTier.color
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Countdown text
            Text(
                text = "$daysLeft days left",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Message
            Text(
                text = message,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun UserRankCard(
    rank: Int,
    username: String,
    points: Int,
    isCurrentUser: Boolean,
    profilePicture: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) Color(0xFFE3F2FD) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rank.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Avatar
            Image(
                painter = painterResource(id = profilePicture),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Username and Points
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = username,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "$points points",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Current user indicator
            if (isCurrentUser) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Current User",
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun LeaderboardEntryItem(
    rank: Int,
    username: String,
    points: Int,
    isCurrentUser: Boolean,
    profilePicture: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(
                if (isCurrentUser) Color(0xFFE3F2FD)
                else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank
        Text(
            text = rank.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.width(30.dp)
        )

        // Avatar
        Image(
            painter = painterResource(id = profilePicture),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Username
        Text(
            text = username,
            modifier = Modifier.weight(1f),
            fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Normal
        )

        // Points
        Text(
            text = "$points pts",
            fontWeight = FontWeight.Medium
        )
    }
}

enum class LeaderboardTab {
    TOP, ALL
}

enum class LeagueTier(val color: Color, val iconResId: Int) {
    BRONZE(Color(0xFFCD7F32), R.drawable.bronze),
    SILVER(Color(0xFFC0C0C0), R.drawable.silver),
    GOLD(Color(0xFFFFD700), R.drawable.gold),
    PLATINUM(Color(0xFF90E4C1), R.drawable.platinum),
    DIAMOND(Color(0xFF4DC9FF), R.drawable.diamond),
    MASTER(Color(0xFFFF57B9), R.drawable.master),
    GRANDMASTER(Color(0xFFFF5252), R.drawable.grandmaster),
    CHALLENGE(Color(0xFF4DC9FF), R.drawable.challenger)
}