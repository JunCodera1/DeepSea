package com.example.deepsea.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.R

data class UserRankInfo(
    val rank: Int,
    val name: String,
    val xp: Int,
    val avatarResId: Int,
    val medalResId: Int? = null
)

@Composable
fun RankPage() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LeagueHeader(
                leagueName = "Diamond League",
                daysLeft = 7,
                message = "The next Tournament begins soon"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sample users for the leaderboard
            val users = listOf(
                UserRankInfo(1, "Jason", 597, R.drawable.avatar_placeholder, R.drawable.gold_medal),
                UserRankInfo(2, "Cindy", 252, R.drawable.avatar_placeholder, R.drawable.silver_medal),
                UserRankInfo(3, "Ashley", 224, R.drawable.avatar_placeholder, R.drawable.bronze_medal),
                UserRankInfo(4, "Tiffany", 153, R.drawable.avatar_placeholder),
                UserRankInfo(5, "Sergio", 156, R.drawable.avatar_placeholder)
            )

            users.forEach { user ->
                UserRankItem(
                    userInfo = user,
                    isCurrentUser = user.rank == 4 // Highlighting Tiffany as the current user
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun LeagueHeader(leagueName: String, daysLeft: Int, message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFC0CB))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF505050))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xB0A0F0EF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pencil),
                        contentDescription = "Diamond League",
                        tint = Color(0xFF008080),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = leagueName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = "$daysLeft days",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFFFFA500)
            )
        }
    }
}

@Composable
fun UserRankItem(userInfo: UserRankInfo, isCurrentUser: Boolean) {
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
            // Rank number with medal if applicable
            Box(
                modifier = Modifier.width(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${userInfo.rank}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

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

            // XP points
            Text(
                text = "${userInfo.xp} XP",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF77AA77)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RankScreenPreview() {
    MaterialTheme {
        RankPage()
    }
}