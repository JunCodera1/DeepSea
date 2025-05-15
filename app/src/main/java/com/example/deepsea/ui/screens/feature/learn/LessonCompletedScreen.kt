package com.example.deepsea.ui.screens.feature.learn

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.api.UserProfileService
import com.example.deepsea.data.repository.UserProfileRepository
import com.example.deepsea.repository.CourseRepository
import com.example.deepsea.ui.viewmodel.home.HomeViewModel
import com.example.deepsea.ui.viewmodel.home.HomeViewModelFactory
import com.example.deepsea.ui.viewmodel.learn.LessonViewModel
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun LessonCompletedScreenPreview() {
    val navController = rememberNavController()

    MaterialTheme {
        LessonCompletedScreen(
            navController = navController,
            lessonResult = LessonResult(xp = 80, time = "2:30", accuracy = 95),
            lessonId = 1
        )
    }
}


@Composable
fun LessonCompletedScreen(
    navController: NavController,
    lessonResult: LessonResult,
    lessonId: Long
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(CourseRepository(RetrofitClient.courseApiService))
    )
    val lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.LessonViewModelFactory())

    LaunchedEffect(lessonId) {
        lessonViewModel.getLessonResult(lessonId)
    }

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

            Text(
                text = "Perfect lesson!",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = YellowPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Take a bow!",
                fontSize = 24.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCard(
                    title = "TOTAL XP",
                    value = "${lessonResult.xp}",
                    icon = R.drawable.ic_xp_bolt,
                    backgroundColor = PurpleLight,
                    iconTint = Purple
                )
                StatCard(
                    title = "BLAZING",
                    value = lessonResult.time,
                    icon = R.drawable.ic_timer,
                    backgroundColor = BlueLight,
                    iconTint = Blue
                )
                StatCard(
                    title = "AMAZING",
                    value = "${lessonResult.accuracy}%",
                    icon = R.drawable.ic_target,
                    backgroundColor = GreenLight,
                    iconTint = Green
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            homeViewModel.completeUnit(lessonId, earnedXp = lessonResult.xp)
                            navController.navigate("home") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        } catch (e: Exception) {
                            println("Error: ${e.message}")
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
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
