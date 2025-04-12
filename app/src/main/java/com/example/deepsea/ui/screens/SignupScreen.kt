package com.example.deepsea.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.deepsea.R
import com.example.deepsea.ui.components.SignupTextField
import com.example.deepsea.ui.theme.DeepSeaTheme

@Composable
fun SignupPage(
    onSignUpClick: () -> Unit = {},
    onSignInClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val backgroundPainter = painterResource(id = R.drawable.background_login)

    // Các state cho form
    var name by remember { mutableStateOf("") }
    var emailOrPhone by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    DeepSeaTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = backgroundPainter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "CREATE YOUR ACCOUNT",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(32.dp))

                SignupTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeHolder = "Full Name",
                    label = "Full Name",
                )

                Spacer(modifier = Modifier.height(16.dp))

                SignupTextField(
                    value = emailOrPhone,
                    onValueChange = { emailOrPhone = it },
                    placeHolder = "Phone or Email",
                    label = "Phone or Email",
                )

                Spacer(modifier = Modifier.height(16.dp))

                SignupTextField(
                    value = username,
                    onValueChange = { username = it },
                    placeHolder = "Username",
                    label = "Username",
                )

                Spacer(modifier = Modifier.height(16.dp))

                SignupTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeHolder = "Password",
                    label = "Password",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                SignupTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeHolder = "Confirm Password",
                    label = "Confirm Password",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onSignUpClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "SIGN UP")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Text("Already have an account? ")
                    Text(
                        text = "Sign In",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(onClick = onSignInClick)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SignupScreenPreview() {
    SignupPage()
}