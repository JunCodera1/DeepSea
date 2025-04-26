package com.example.deepsea.ui.screens.feature

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.R
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable

data class UserRankInfo(
    val rank: Int,
    val name: String,
    val xp: Int,
    val avatarResId: Int,
    val medalResId: Int? = null
)

enum class LeagueTier(val title: String, val color: Color, val iconResId: Int) {
    BRONZE("Silver League", Color(0xFFC0C0C0), R.drawable.silver),
    SILVER("Gold League", Color(0xFFCD7F32), R.drawable.gold),
    GOLD("Diamond League", Color(0xFF9EE6F5), R.drawable.diamond),
    DIAMOND("Master League", Color(0xFF4F0762), R.drawable.master), // Replace with diamond icon
    RUBY("GrandMaster League", Color(0xFFE0115F), R.drawable.grandmaster), // Replace with ruby icon
    LEGENDARY("Challenger League", Color(0xFF5556CE), R.drawable.challenger) // Replace with legendary icon
}

@Preview(showBackground = true)
@Composable
fun RankPage() {
    val tiers = LeagueTier.values()
    val pagerState = rememberPagerState(pageCount = { tiers.size }, initialPage = 3) // Start with Diamond
    val coroutineScope = rememberCoroutineScope()

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
                            .clickable { coroutineScope.launch { pagerState.animateScrollToPage(index) } }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sample users for the leaderboard - different users per league
            val users = when (tiers[pagerState.currentPage]) {
                LeagueTier.BRONZE -> listOf(
                    UserRankInfo(1, "Rookie1", 197, R.drawable.avatar_placeholder, R.drawable.gold_medal),
                    UserRankInfo(2, "Beginner2", 152, R.drawable.avatar_placeholder, R.drawable.silver_medal),
                    UserRankInfo(3, "Novice3", 124, R.drawable.avatar_placeholder, R.drawable.bronze_medal),
                    // More bronze league users
                )
                LeagueTier.SILVER -> listOf(
                    UserRankInfo(1, "Silver1", 297, R.drawable.avatar_placeholder, R.drawable.gold_medal),
                    UserRankInfo(2, "Silver2", 252, R.drawable.avatar_placeholder, R.drawable.silver_medal),
                    UserRankInfo(3, "Silver3", 224, R.drawable.avatar_placeholder, R.drawable.bronze_medal),
                    // More silver league users
                )
                LeagueTier.GOLD -> listOf(
                    UserRankInfo(1, "Gold1", 397, R.drawable.avatar_placeholder, R.drawable.gold_medal),
                    UserRankInfo(2, "Gold2", 352, R.drawable.avatar_placeholder, R.drawable.silver_medal),
                    UserRankInfo(3, "Gold3", 324, R.drawable.avatar_placeholder, R.drawable.bronze_medal),
                    // More gold league users
                )
                LeagueTier.DIAMOND -> listOf(
                    UserRankInfo(1, "Jason", 597, R.drawable.avatar_placeholder, R.drawable.gold_medal),
                    UserRankInfo(2, "Cindy", 552, R.drawable.avatar_placeholder, R.drawable.silver_medal),
                    UserRankInfo(3, "Ashley", 524, R.drawable.avatar_placeholder, R.drawable.bronze_medal),
                    UserRankInfo(4, "Tiffany", 453, R.drawable.avatar_placeholder),
                    UserRankInfo(5, "Sergio", 356, R.drawable.avatar_placeholder),
                )
                LeagueTier.RUBY -> listOf(
                    UserRankInfo(1, "Ruby1", 797, R.drawable.avatar_placeholder, R.drawable.gold_medal),
                    UserRankInfo(2, "Ruby2", 752, R.drawable.avatar_placeholder, R.drawable.silver_medal),
                    UserRankInfo(3, "Ruby3", 724, R.drawable.avatar_placeholder, R.drawable.bronze_medal),
                    // More ruby league users
                )
                LeagueTier.LEGENDARY -> listOf(
                    UserRankInfo(1, "Legend1", 997, R.drawable.avatar_placeholder, R.drawable.gold_medal),
                    UserRankInfo(2, "Legend2", 952, R.drawable.avatar_placeholder, R.drawable.silver_medal),
                    UserRankInfo(3, "Legend3", 924, R.drawable.avatar_placeholder, R.drawable.bronze_medal),
                    // More legendary league users
                )
            }

            // 1. Fix the unused page parameter in AnimatedContent
            AnimatedContent(
                targetState = pagerState.currentPage,
                transitionSpec = {
                    slideInHorizontally(
                        animationSpec = tween(300),
                        initialOffsetX = { if (targetState > initialState) it else -it }
                    ) togetherWith slideOutHorizontally(
                        animationSpec = tween(300),
                        targetOffsetX = { if (targetState > initialState) -it else it }
                    )
                },
                label = "LeaderboardAnimation"
            ) { currentPage ->  // Renamed to make it clear we're using it
                val usersForCurrentPage = when (tiers[currentPage]) {  // Using the parameter here
                    LeagueTier.BRONZE -> listOf(/* bronze users */)
                    LeagueTier.SILVER -> listOf(/* silver users */)
                    // and so on...
                    else -> users  // Default to the previously defined users list
                }

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(users) { user ->
                        UserRankItem(
                            userInfo = user,
                            isCurrentUser = user.rank == 4
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { /* Navigate to another screen */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "League Info"
            )
        }
    }
}

@Composable
fun LeagueHeaderWithAnimation(leagueTier: LeagueTier, daysLeft: Int, message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),


    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            // League tier badge with enhanced visual effect
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(leagueTier.color.copy(alpha = 0.2f))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(leagueTier.color.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = leagueTier.iconResId),
                        contentDescription = leagueTier.title,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = leagueTier.title,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = leagueTier.color
            )

            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.padding(top = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4E0))
            ) {
                Text(
                    text = "$daysLeft days left",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFFFA500),
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
        }
    }
}

// Keep original UserRankItem function
@Composable
fun UserRankItem(userInfo: UserRankInfo, isCurrentUser: Boolean) {
    // The implementation remains the same
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) Color(0xFFEAFFEA) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank number
            Box(
                modifier = Modifier.width(24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "${userInfo.rank}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // Medal icon if applicable
            Box(
                modifier = Modifier.size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                userInfo.medalResId?.let {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = "Medal",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // User avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = userInfo.avatarResId),
                    contentDescription = "Avatar",
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // User name
            Text(
                text = userInfo.name,
                modifier = Modifier.weight(1f),
                fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Normal
            )

            // XP points with colored background
            Box(
                modifier = Modifier
                    .background(
                        color = if (isCurrentUser) Color(0xFF90EE90).copy(alpha = 0.3f) else Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${userInfo.xp} XP",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}