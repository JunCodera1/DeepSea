package com.example.deepsea.ui.screens.feature.learn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.deepsea.data.model.exercise.LessonResult
import com.example.deepsea.ui.theme.Blue
import com.example.deepsea.ui.theme.BlueLight
import com.example.deepsea.ui.theme.BluePrimary
import com.example.deepsea.ui.theme.Green
import com.example.deepsea.ui.theme.GreenLight
import com.example.deepsea.ui.theme.Purple
import com.example.deepsea.ui.theme.PurpleLight
import com.example.deepsea.ui.theme.YellowPrimary
import com.example.deepsea.R
import androidx.navigation.compose.rememberNavController

@Preview(showBackground = true)
@Composable
fun LessonCompletedScreenPreview() {
    val navController = rememberNavController()

    MaterialTheme {
        LessonCompletedScreen(
            navController = navController,
            lessonResult = LessonResult(xp = 80, time = "2:30", accuracy = 95)
        )
    }
}


@Composable
fun LessonCompletedScreen(
    navController: NavController,
    lessonResult: LessonResult = LessonResult(50, "1:11", 100)
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top decorative elements (fireworks)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_firework),
                    contentDescription = "Yellow firework",
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Character and owl
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Character with pink outfit
                    Image(
                        painter = painterResource(id = R.drawable.ic_character),
                        contentDescription = "Character",
                        modifier = Modifier.height(180.dp)
                    )

                    Image(
                        painter = painterResource(id = R.drawable.ic_dog),
                        contentDescription = "Owl",
                        modifier = Modifier.height(100.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Perfect lesson text
            Text(
                text = "Perfect lesson!",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = YellowPrimary,
                textAlign = TextAlign.Center
            )

            // Take a bow text
            Text(
                text = "Take a bow!",
                fontSize = 24.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Stats cards row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // XP Card
                StatCard(
                    title = "TOTAL XP",
                    value = "${lessonResult.xp}",
                    icon = R.drawable.ic_xp_bolt,
                    backgroundColor = PurpleLight,
                    iconTint = Purple
                )

                // Time Card
                StatCard(
                    title = "BLAZING",
                    value = lessonResult.time,
                    icon = R.drawable.ic_timer,
                    backgroundColor = BlueLight,
                    iconTint = Blue
                )

                // Accuracy Card
                StatCard(
                    title = "AMAZING",
                    value = "${lessonResult.accuracy}%",
                    icon = R.drawable.ic_target,
                    backgroundColor = GreenLight,
                    iconTint = Green
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Continue button
            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Text(
                    text = "CONTINUE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: Int,
    backgroundColor: Color,
    iconTint: Color
) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
