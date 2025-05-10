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

    // Update current league when pager page changes
    LaunchedEffect(pagerState.currentPage) {
        viewModel.setCurrentLeague(tiers[pagerState.currentPage])
    }

    // Collect state from Flows
    val topLeaderboard by viewModel.topLeaderboardState.collectAsState()
    val allUsers by viewModel.allUsersState.collectAsState()
    val filteredUsers by viewModel.filteredUsersState.collectAsState()
    val userRankMap by viewModel.userRankState.collectAsState()
    val userRank = remember(userRankMap) { userRankMap[currentUserId] }
    val currentLeague by viewModel.currentLeague.collectAsState()

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
                val leagueTier = tiers[page]
                LeagueHeaderWithAnimation(
                    leagueTier = leagueTier,
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

            // User's personal rank card if user is in the current league
            userRank?.let { user ->
                val userXp = user.totalXp
                val isUserInCurrentLeague = isUserInLeague(userXp, currentLeague)

                if (isUserInCurrentLeague) {
                    UserRankCard(
                        rank = user.rank,
                        username = user.username,
                        points = userXp,
                        isCurrentUser = true,
                        profilePicture = R.drawable.avatar_placeholder
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Info",
                                tint = Color(0xFFFFB300)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "You're currently in the ${getUserLeague(userXp).name} league with $userXp XP",
                                color = Color(0xFF5D4037)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // League leaderboard card title
            Text(
                text = "${currentLeague.name} League Leaderboard",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = currentLeague.color,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Leaderboard List
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    when {
                        isLoadingTop && currentTab.value == LeaderboardTab.TOP -> {
                            LoadingIndicator(currentLeague.color)
                        }
                        isLoadingAll && currentTab.value == LeaderboardTab.ALL -> {
                            LoadingIndicator(currentLeague.color)
                        }
                        filteredUsers.isEmpty() -> {
                            EmptyLeagueMessage(currentLeague)
                        }
                        else -> {
                            when (currentTab.value) {
                                LeaderboardTab.TOP -> {
                                    FilteredLeaderboardList(
                                        filteredUsers = filteredUsers,
                                        currentUserId = currentUserId,
                                        isLeaderboardEntry = true
                                    )
                                }
                                LeaderboardTab.ALL -> {
                                    FilteredLeaderboardList(
                                        filteredUsers = filteredUsers,
                                        currentUserId = currentUserId,
                                        isLeaderboardEntry = false
                                    )
                                }
                            }
                        }
                    }

                    // Refresh button
                    SmallFloatingActionButton(
                        onClick = { viewModel.refreshAllData(currentUserId) },
                        containerColor = currentLeague.color,
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
fun EmptyLeagueMessage(leagueTier: LeagueTier) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = leagueTier.iconResId),
            contentDescription = "Empty league",
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No users in ${leagueTier.name} league yet",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
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
fun FilteredLeaderboardList(
    filteredUsers: List<Any>,
    currentUserId: Long,
    isLeaderboardEntry: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 500.dp)
    ) {
        itemsIndexed(filteredUsers) { index, item ->
            when {
                isLeaderboardEntry && item is LeaderboardEntry -> {
                    LeaderboardEntryItem(
                        rank = index + 1,
                        username = item.username,
                        points = item.totalXp,
                        isCurrentUser = item.username == currentUserId.toString(), // Basic check, improve as needed
                        profilePicture = R.drawable.avatar_placeholder
                    )
                }
                !isLeaderboardEntry && item is UserProfileData -> {
                    LeaderboardEntryItem(
                        rank = index + 1,
                        username = item.username,
                        points = item.totalXp,
                        isCurrentUser = true,
                        profilePicture = R.drawable.avatar_placeholder
                    )
                }
            }
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

            // XP Range text
            val xpRange = getXpRangeDisplay(leagueTier)
            Text(
                text = xpRange,
                fontSize = 14.sp,
                color = Color.Gray
            )

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

            // Avatar with league icon
            Box {
                Image(
                    painter = painterResource(id = profilePicture),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )

                // League indicator
                val leagueIcon = getLeagueIconForXp(points)
                Image(
                    painter = painterResource(id = leagueIcon),
                    contentDescription = "League Icon",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.BottomEnd)
                        .background(Color.White, CircleShape)
                        .padding(2.dp)
                )
            }

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

        // Avatar with league indication
        Box {
            Image(
                painter = painterResource(id = profilePicture),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            // Small league icon
            val leagueIcon = getLeagueIconForXp(points)
            Image(
                painter = painterResource(id = leagueIcon),
                contentDescription = "League Icon",
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.BottomEnd)
                    .background(Color.White, CircleShape)
                    .padding(1.dp)
            )
        }

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

// Helper functions
fun getXpRangeDisplay(league: LeagueTier): String {
    return when (league) {
        LeagueTier.BRONZE -> "0 - 699 XP"
        LeagueTier.SILVER -> "700 - 1,499 XP"
        LeagueTier.GOLD -> "1,500 - 2,499 XP"
        LeagueTier.PLATINUM -> "2,500 - 3,999 XP"
        LeagueTier.DIAMOND -> "4,000 - 5,999 XP"
        LeagueTier.MASTER -> "6,000 - 7,999 XP"
        LeagueTier.GRANDMASTER -> "8,000 - 14,999 XP"
        LeagueTier.CHALLENGE -> "15,000+ XP"
    }
}

fun getLeagueIconForXp(xp: Int): Int {
    return when (xp) {
        in 0..699 -> R.drawable.bronze
        in 700..1499 -> R.drawable.silver
        in 1500..2499 -> R.drawable.gold
        in 2500..3999 -> R.drawable.platinum
        in 4000..5999 -> R.drawable.diamond
        in 6000..7999 -> R.drawable.master
        in 8000..14999 -> R.drawable.grandmaster
        else -> R.drawable.challenger
    }
}

enum class LeaderboardTab {
    TOP,    // Shows top 100 players
    ALL     // Shows all players
}

// Enum class for league tiers with their properties
enum class LeagueTier(
    val color: Color,
    val iconResId: Int
) {
    BRONZE(Color(0xFFCD7F32), R.drawable.bronze),
    SILVER(Color(0xFFC0C0C0), R.drawable.silver),
    GOLD(Color(0xFFFFD700), R.drawable.gold),
    PLATINUM(Color(0xFF9DE1E2), R.drawable.platinum),
    DIAMOND(Color(0xFF1E90FF), R.drawable.diamond),
    MASTER(Color(0xFFAD5CFF), R.drawable.master),
    GRANDMASTER(Color(0xFFFF5252), R.drawable.grandmaster),
    CHALLENGE(Color(0xFFFFD54F), R.drawable.challenger)
}

// Helper function to check if a user is in the current league
fun isUserInLeague(userXp: Int, league: LeagueTier): Boolean {
    return when (league) {
        LeagueTier.BRONZE -> userXp in 0..699
        LeagueTier.SILVER -> userXp in 700..1499
        LeagueTier.GOLD -> userXp in 1500..2499
        LeagueTier.PLATINUM -> userXp in 2500..3999
        LeagueTier.DIAMOND -> userXp in 4000..5999
        LeagueTier.MASTER -> userXp in 6000..7999
        LeagueTier.GRANDMASTER -> userXp in 8000..14999
        LeagueTier.CHALLENGE -> userXp >= 15000
    }
}

// Completing the getUserLeague function that was cut off
fun getUserLeague(xp: Int): LeagueTier {
    return when (xp) {
        in 0..699 -> LeagueTier.BRONZE
        in 700..1499 -> LeagueTier.SILVER
        in 1500..2499 -> LeagueTier.GOLD
        in 2500..3999 -> LeagueTier.PLATINUM
        in 4000..5999 -> LeagueTier.DIAMOND
        in 6000..7999 -> LeagueTier.MASTER
        in 8000..14999 -> LeagueTier.GRANDMASTER
        else -> LeagueTier.CHALLENGE
    }
}