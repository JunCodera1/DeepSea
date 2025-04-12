package com.example.deepsea.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.deepsea.ui.components.ImageButton
import com.example.deepsea.ui.components.LoginTextField

@Composable
fun LoginPage() {
    var email by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
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
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val imageModifier = Modifier
                .size(200.dp)
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentScale = ContentScale.Fit,
                modifier = imageModifier,
                contentDescription = ""

            )

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

            TextButton(
                modifier = Modifier.align(Alignment.End),
                onClick = {}
            ) {
                Text("Forgot password ?")
            }

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

            Spacer(modifier = Modifier.height(18.dp))
            val facebookIcon = painterResource(id = R.drawable.facebook_icon)
            val googleIcon = painterResource(id = R.drawable.google_icon)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                ImageButton(
                    image = facebookIcon,
                    text = "",
                    onClick = { /* your action */ }
                )
                ImageButton(
                    image = googleIcon,
                    text = "",
                    onClick = { /* your action */ }
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            TextButton(
                onClick = {}
            ) {
                Text("Don't have account? Sign Up.")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginPage()
}
