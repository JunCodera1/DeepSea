package com.example.deepsea.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deepsea.ui.components.DeepSeaButton
import com.example.deepsea.ui.theme.DeepSeaTheme
import com.example.deepsea.R
import com.example.deepsea.ui.components.DeepSeaDivider
import com.example.deepsea.ui.components.LoginTextField

@Composable
fun LoginPage() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val backgroundPainter = painterResource(id = R.drawable.background_login)
    DeepSeaTheme {
        Image(
            painter = backgroundPainter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize() // phủ toàn bộ Box
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val imageModifier = Modifier
                .size(200.dp)
                .border(BorderStroke(1.dp, Color.Black))
                .background(Color.Yellow)
            Image(
                painter = painterResource(id = R.drawable.evangelion),
                contentScale = ContentScale.Fit,
                modifier = imageModifier,
                contentDescription = ""

            )
            Text(text = "Welcome back!", color = Color.Green, style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(8.dp))

            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            LoginTextField(
                value = email,
                onValueChange = { email = it },
                placeHolder = "Enter your email",
                label = "Email",
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginTextField(
                value = password,
                onValueChange = { password = it },
                placeHolder = "Enter your password",
                label = "Password",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            DeepSeaButton(
                onClick = {
                    // Handle login logic here
                },
                modifier = Modifier.width(90.dp),
            ) {
                Text(text = "Login")
            }
            Spacer(modifier = Modifier.height(24.dp))

            DeepSeaDivider(Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginPage()
}
