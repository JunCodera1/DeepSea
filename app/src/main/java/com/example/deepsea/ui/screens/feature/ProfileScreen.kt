package com.example.deepsea.ui.profile

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deepsea.R
import com.example.deepsea.data.model.user.UserProfileData
import com.example.deepsea.utils.SessionManager

@Composable
fun ProfilePage(sessionManager: SessionManager,
                viewModel: UserProfileViewModel = viewModel(),
                paddingValues: PaddingValues, onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current

    val userId by sessionManager.userId.collectAsState(initial = null)

    val userProfile = viewModel.userProfileData.value

    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()


    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.fetchUserProfile(userId!!)
        }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F7F9)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(paddingValues)
        ) {
            // Profile Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFF4DB6FF)
                    )
                }
            }

            // User basic info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF673AB7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userProfile?.name?.firstOrNull()?.uppercase() ?: "",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // User info
                    Column {
                        Text(
                            text = userProfile?.name ?: "",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "@${userProfile?.username}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Joined ${userProfile?.joinDate}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Friends count
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Divider(
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(4.dp),
                                color = Color(0xFF4DB6FF)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${userProfile?.followers} Friends",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Statistics section
            Text(
                text = "Languages",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier= Modifier.height(16.dp))
            Text(
                text = "Statistics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Stats cards
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // Day streak stat
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFFFF3E0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile?.dayStreak.toString()?: "R",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9800)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Current",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Streak",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    // XP stat
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val painter = getRankIconFromXp(userProfile?.totalXp)
                        Icon(
                            painter = painter,
                            contentDescription = "Rank Icon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Total XP",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    // Top 3 finishes stat
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFE3F2FD), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${userProfile?.topFinishes}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Top 3 Finishes",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Friend suggestions
            Text(
                text = "Friend suggestions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Friend suggestion card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Friend avatar
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lorem",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Lorem knows some others",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    // Add button
                    Button(
                        onClick = { /* Add friend */ },
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4DB6FF))
                    ) {
                        Text(text = "+ ADD")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Dismiss button
                    IconButton(
                        onClick = { /* Dismiss suggestion */ },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Friends section with tabs
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

            // Invite friends card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mascot icon placeholder
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8BC34A))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Invite friends",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "For each friend it's free and fun to learn languages in Duolingo",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { /* Invite friends */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4DB6FF))
                ) {
                    Text(
                        text = "INVITE FRIENDS",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Achievements section
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
            achievementCard(
                title = "Wildfire",
                description = "Reach a 3 day streak",
                progress = "1/3",
                backgroundColor = Color(0xFFFFCDD2),
                iconColor = Color(0xFFF44336)
            )

            Spacer(modifier = Modifier.height(8.dp))

            achievementCard(
                title = "Sage",
                description = "Earn 1000 XP",
                progress = "${userProfile?.totalXp}/1000",
                backgroundColor = Color(0xFFDCEDC8),
                iconColor = Color(0xFF8BC34A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            achievementCard(
                title = "Scholar",
                description = "Learn 100 new words in a single course",
                progress = "100/175",
                backgroundColor = Color(0xFFFFECB3),
                iconColor = Color(0xFFFFA000)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun achievementCard(
    title: String,
    description: String,
    progress: String,
    backgroundColor: Color,
    iconColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Achievement icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(iconColor)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = progress,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun LanguageFlags(
    userData: UserProfileData?,
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
            // Display existing languages
            userData?.courses?.forEach { language ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = language.flagResId),
                        contentDescription = language.displayName,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = language.displayName,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Add language button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                Text(
                    text = "Add",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
@Composable
fun getRankIconFromXp(xp: Int?): Painter {
    return when (xp ?: 0) {
        in 0..699 -> painterResource(id = R.drawable.bronze)
        in 700..1499 -> painterResource(id = R.drawable.silver)
        in 1500..2499 -> painterResource(id = R.drawable.gold)
        in 2500..3999 -> painterResource(id = R.drawable.platinum)
        in 4000..5999 -> painterResource(id = R.drawable.diamond)
        in 6000..7999 -> painterResource(id = R.drawable.master)
        in 8000..14999 -> painterResource(id = R.drawable.master)
        in 15000..35999 -> painterResource(id = R.drawable.grandmaster)
        else -> painterResource(id = R.drawable.challenger)
    }
}



@Preview(showBackground = true)
@Composable
fun UserProfilePreview() {

}
