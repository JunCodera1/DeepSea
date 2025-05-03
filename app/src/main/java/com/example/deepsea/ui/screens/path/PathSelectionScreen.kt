package com.example.deepsea.ui.screens.path

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.deepsea.R
import com.example.deepsea.data.api.UserProfileService
import com.example.deepsea.data.model.course.path.PathOption
import com.example.deepsea.ui.viewmodel.course.path.PathSelectionViewModel
import com.example.deepsea.ui.viewmodel.course.path.PathSelectionViewModelFactory
import com.example.deepsea.utils.SessionManager

@Composable
fun PathSelectionPage(
    languageName: String,
    onPathSelected: (PathOption) -> Unit,
    onBack: () -> Unit
) {
    var selectedPath by remember { mutableStateOf<PathOption?>(null) }
    val purpleColor = Color(0xFF6750A4)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top progress bar
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
                    .size(24.dp)
                    .clickable { onBack() }
            )

            LinearProgressIndicator(
                progress = 0.5f,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                color = Color(0xFFFFC107),
                trackColor = Color(0xFFEEEEEE)
            )
        }

        Text(
            text = "Choose your path",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            textAlign = TextAlign.Center
        )

        // Option: Learning for the first time
        PathOptionItem(
            path = "beginner",
            imageResId = R.drawable.ic_beginner,
            title = "Learning $languageName for the first time?",
            subtitle = "Start from scratch!",
            isSelected = selectedPath == PathOption.BEGINNER,
            onSelect = { selectedPath = PathOption.BEGINNER }
        )

        // Option: Already know some
        PathOptionItem(
            path = "professor",
            imageResId = R.drawable.ic_professor,
            title = "Already know some $languageName?",
            subtitle = "Check your level here!",
            isSelected = selectedPath == PathOption.PROFESSOR,
            onSelect = { selectedPath = PathOption.PROFESSOR }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Continue button
        Button(
            onClick = {
                selectedPath?.let {path ->
                    onPathSelected(path)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedPath != null) purpleColor else Color.LightGray
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = selectedPath != null
        ) {
            Text("CONTINUE", fontSize = 16.sp)
        }
    }
}

@Composable
fun PathSelectionFlowPage(
    navController: NavController,
    pathService: UserProfileService,
    sessionManager: SessionManager
) {
    val profileId by sessionManager.profileId.collectAsState(initial = null)
    val factory = remember { PathSelectionViewModelFactory(pathService, sessionManager) }
    val viewModel: PathSelectionViewModel = viewModel(factory = factory)
    // Khi profileId có giá trị, gọi fetchPaths một lần
    LaunchedEffect(profileId) {
        profileId?.let {
            viewModel.fetchPaths(it)
        }
    }

    val userPaths = viewModel.userPaths
    val selectedLanguages = userPaths.keys.toList()

    var currentIndex by remember { mutableStateOf(0) }
    val currentLanguage = selectedLanguages.getOrNull(currentIndex)
    val isLast = currentIndex == selectedLanguages.lastIndex

    if (selectedLanguages.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No languages selected. Please go back and select languages.")
        }
        return
    }

    currentLanguage?.let { language ->
        PathSelectionPage(
            languageName = language.displayName,
            onPathSelected = { path ->
                viewModel.setPath(language, path)

                if (isLast) {
                    viewModel.saveAllPaths()
                    navController.navigate("daily-goal-selection") {
                        popUpTo("language_flow") { inclusive = true }
                    }
                } else {
                    currentIndex++
                }
            },
            onBack = {
                if (currentIndex > 0) currentIndex-- else navController.popBackStack()
            },
        )
    }
}



@Composable
fun PathOptionItem(
    path: String,
    imageResId: Int,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFEDE7F6) else Color.White
    val borderColor = if (isSelected) Color(0xFF6750A4) else Color.LightGray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 8.dp)
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = if (isSelected) Color(0xFF311B92) else Color.Black
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PathSelectionPagePreview() {
    PathSelectionPage(
        languageName = "Spanish",
        onPathSelected = {},
        onBack = {}
    )
}

