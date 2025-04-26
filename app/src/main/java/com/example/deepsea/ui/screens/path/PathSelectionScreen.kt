package com.example.deepsea.ui.screens.path

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.deepsea.R

@Composable
fun PathSelectionPage(navController: NavController) {
    var selectedPath by remember { mutableStateOf<String?>(null) }
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
                    .clickable { navController.popBackStack() }
            )

            LinearProgressIndicator(
                progress = 0.8f,
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
        PathOption(
            path = "beginner",
            imageResId = R.drawable.ic_beginner,
            title = "Learning French for the first time?",
            subtitle = "Start from scratch!",
            isSelected = selectedPath == "beginner",
            onSelect = { selectedPath = "beginner" }
        )

        // Option: Already know some
        PathOption(
            path = "professor",
            imageResId = R.drawable.ic_professor,
            title = "Already know some French?",
            subtitle = "Check your level here!",
            isSelected = selectedPath == "professor",
            onSelect = { selectedPath = "professor" }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Continue button
        Button(
            onClick = {
                selectedPath?.let {
                    navController.navigate("home")
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
fun PathOption(
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
    PathSelectionPage(navController = rememberNavController())
}

