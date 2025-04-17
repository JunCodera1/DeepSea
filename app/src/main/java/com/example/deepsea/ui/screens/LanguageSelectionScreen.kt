package com.example.deepsea.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.deepsea.R

@Composable
fun LanguageSelectionPage(navController: NavController) {
    // State to track selected language
    var selectedLanguage by remember { mutableStateOf<String?>(null) }

    // Purple color for selected state
    val purpleColor = Color(0xFF6750A4)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top bar with back button
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
                    .size(45.dp)
                    .clickable { navController.popBackStack() }
            )

            LinearProgressIndicator(
                progress = 0.1f,
                modifier = Modifier
                    .weight(0.3f)
                    .padding(start = 16.dp),
                color = Color(0xFF8CC83C),
                trackColor = Color(0xFFEEEEEE)
            )
        }

        // Mascot and speech bubble
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Green owl mascot
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Mascot",
                    modifier = Modifier
                        .size(100.dp)
                )

                // Speech bubble
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "What would you like to learn?",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 18.sp
                    )
                }
            }
        }

        // Header "For English speakers"
        Text(
            text = "For English speakers",
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        // Language options
        LanguageOption(
            language = "English",
            flagResId = R.drawable.flag_england,
            isSelected = selectedLanguage == "English",
            onSelect = { selectedLanguage = "English" }
        )
        LanguageOption(
            language = "Spanish",
            flagResId = R.drawable.flag_spain,
            isSelected = selectedLanguage == "Spanish",
            onSelect = { selectedLanguage = "Spanish" }
        )
        LanguageOption(
            language = "French",
            flagResId = R.drawable.flag_france,
            isSelected = selectedLanguage == "French",
            onSelect = { selectedLanguage = "French" }
        )
        LanguageOption(
            language = "German",
            flagResId = R.drawable.flag_germany,
            isSelected = selectedLanguage == "German",
            onSelect = { selectedLanguage = "German" }
        )
        LanguageOption(
            language = "Italian",
            flagResId = R.drawable.flag_italy,
            isSelected = selectedLanguage == "Italian",
            onSelect = { selectedLanguage = "Italian" }
        )
        LanguageOption(
            language = "Japanese",
            flagResId = R.drawable.flag_japan,
            isSelected = selectedLanguage == "Japanese",
            onSelect = { selectedLanguage = "Japanese" }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Continue button - changes color when an option is selected
        val isLanguageSelected = selectedLanguage != null
        Button(
            onClick = {
                if (isLanguageSelected) {
                    navController.navigate("home") // Replace with your next screen route
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLanguageSelected) purpleColor else Color.LightGray
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = isLanguageSelected
        ) {
            Text("CONTINUE", fontSize = 16.sp)
        }
    }
}

@Composable
fun LanguageOption(
    language: String,
    flagResId: Int,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val purpleColor = Color(0xFF6750A4)
    val cardModifier = if (isSelected) {
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(2.dp, purpleColor, RoundedCornerShape(16.dp))
            .clickable { onSelect() }
    } else {
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onSelect() }
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flag
            Image(
                painter = painterResource(id = flagResId),
                contentDescription = language,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            // Language name
            Text(
                text = language,
                modifier = Modifier.padding(start = 16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageSelectionPreview() {
    LanguageSelectionPage(navController = rememberNavController())
}