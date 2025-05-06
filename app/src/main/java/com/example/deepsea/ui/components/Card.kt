package com.example.deepsea.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.R
import com.example.deepsea.data.model.user.UserProfileData
import com.example.deepsea.ui.profile.StatisticItem
import com.example.deepsea.ui.screens.feature.daily.LanguageProgress
import com.example.deepsea.ui.screens.feature.game.Player
import com.example.deepsea.ui.screens.feature.daily.getLanguageLevel
import com.example.deepsea.ui.theme.DeepSeaTheme
import kotlin.random.Random

@Composable
fun DeepSeaCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    color: Color = DeepSeaTheme.colors.uiBackground,
    contentColor: Color = DeepSeaTheme.colors.textPrimary,
    border: BorderStroke? = null,
    elevation: Dp = 4.dp,
    content: @Composable () -> Unit
){
    DeepSeaSurface(
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
        elevation = elevation,
        border = border,
        content = content
    )
}
@Composable
fun InviteFriendsCard() {
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
}

@Composable
fun UserBasicInfoCard(userProfile: UserProfileData?) {
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
            // Avatar/Initial
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
}


@Composable
fun StatisticsCard(userProfile: UserProfileData?) {
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
            StatisticItem(
                value = userProfile?.dayStreak?.toString() ?: "0",
                label = "Current\nStreak",
                backgroundColor = Color(0xFFFFF3E0),
                textColor = Color(0xFFFF9800)
            )

            // XP stat
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Use painterResource directly in a composable context
                val painter = when (userProfile?.totalXp ?: 0) {
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
            StatisticItem(
                value = userProfile?.topFinishes?.toString() ?: "0",
                label = "Top 3\nFinishes",
                backgroundColor = Color(0xFFE3F2FD),
                textColor = Color(0xFF2196F3)
            )
        }
    }
}

@Composable
fun FriendSuggestionCard() {
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
}

@Composable
fun WeeklyStreakCard() {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val streak = remember {
        List(7) { Random.nextBoolean() }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Streak",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${streak.count { it }} days",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysOfWeek.forEachIndexed { index, day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (streak[index]) colorScheme.primary
                                    else colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (streak[index]) colorScheme.primary
                                    else colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (streak[index]) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = colorScheme.onPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = day,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageCard(language: LanguageProgress) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = language.name.first().toString(),
                            color = colorScheme.onPrimaryContainer,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = language.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = getLanguageLevel(language.overallProgress),
                            fontSize = 14.sp,
                            color = colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                Text(
                    text = "${language.overallProgress}%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = language.overallProgress / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = colorScheme.primary,
                trackColor = colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Skills breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SkillIndicator("Speaking", language.skills[0])
                SkillIndicator("Writing", language.skills[1])
                SkillIndicator("Reading", language.skills[2])
                SkillIndicator("Listening", language.skills[3])
            }
        }
    }
}

@Composable
fun AchievementCard(
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
fun FriendSuggestionCard(
    name: String,
    subtitle: String,
    onAddClick: () -> Unit,
    onDismissClick: () -> Unit
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
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Add button
            Button(
                onClick = onAddClick,
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4DB6FF))
            ) {
                Text(text = "+ ADD")
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Dismiss button
            IconButton(
                onClick = onDismissClick,
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
}

@Composable
fun GameModeCard(
    title: String,
    description: String,
    iconContent: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F7FF))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                iconContent()
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun PlayerScoreCard(
    player: Player,
    score: Int,
    isCurrentPlayer: Boolean
) {
    Card(
        modifier = Modifier.width(150.dp),
        shape = RoundedCornerShape(8.dp),
        border = if (isCurrentPlayer) BorderStroke(2.dp, Color(0xFF0078D7)) else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isCurrentPlayer) Color(0xFF0078D7) else Color(0xFFE1E8ED)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = if (isCurrentPlayer) Color.White else Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = player.name,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )

            Text(
                text = "Level ${player.level}",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = score.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = if (isCurrentPlayer) Color(0xFF0078D7) else Color.DarkGray
            )
        }
    }
}


@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun CardPreview() {
    DeepSeaTheme {
        DeepSeaCard {
            Text(text = "Demo", modifier = Modifier.padding(16.dp))
        }
    }
}