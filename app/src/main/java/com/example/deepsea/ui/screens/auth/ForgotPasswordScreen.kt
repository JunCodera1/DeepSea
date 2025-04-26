package com.example.deepsea.ui.screens.auth

// Nếu bạn dùng thêm material icons thì import Icon hoặc painterResource nếu dùng drawable ảnh

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.deepsea.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordPage(navController: NavController) {
    var currentStep by remember { mutableStateOf(ForgotPasswordStep.EMAIL) }
    var email by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf(List(5) { "" }) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image that fills the entire screen
        Image(
            painter = painterResource(id = R.drawable.background_login),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f)
        )

        // Scaffold with content on top of the background
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(onClick = {
                            when (currentStep) {
                                ForgotPasswordStep.EMAIL -> navController.popBackStack()
                                ForgotPasswordStep.VERIFY -> currentStep = ForgotPasswordStep.EMAIL
                                ForgotPasswordStep.RESET -> currentStep = ForgotPasswordStep.VERIFY
                                ForgotPasswordStep.SUCCESS -> {} // No back from success
                            }
                        }) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            modifier = Modifier.zIndex(1f)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (currentStep) {
                    ForgotPasswordStep.EMAIL -> {
                        EmailScreen(
                            email = email,
                            onEmailChange = { email = it },
                            onResetClicked = {
                                if (email.isNotBlank()) {
                                    currentStep = ForgotPasswordStep.VERIFY
                                }
                            }
                        )
                    }
                    ForgotPasswordStep.VERIFY -> {
                        VerifyCodeScreen(
                            email = email,
                            verificationCode = verificationCode,
                            onCodeChange = { index, value ->
                                val newList = verificationCode.toMutableList()
                                newList[index] = value
                                verificationCode = newList
                            },
                            onVerifyClicked = {
                                // Verify if at least 3 digits are filled for this example
                                if (verificationCode.count { it.isNotBlank() } >= 3) {
                                    currentStep = ForgotPasswordStep.RESET
                                }
                            },
                            onResendClicked = { /* Implementation for resend */ }
                        )
                    }
                    ForgotPasswordStep.RESET -> {
                        SetNewPasswordScreen(
                            newPassword = newPassword,
                            confirmPassword = confirmPassword,
                            onNewPasswordChange = { newPassword = it },
                            onConfirmPasswordChange = { confirmPassword = it },
                            onUpdateClicked = {
                                if (newPassword.isNotBlank() && newPassword == confirmPassword) {
                                    currentStep = ForgotPasswordStep.SUCCESS
                                }
                            }
                        )
                    }
                    ForgotPasswordStep.SUCCESS -> {
                        SuccessScreen(
                            onContinueClicked = {
                                // Navigate to login or directly log the user in
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
