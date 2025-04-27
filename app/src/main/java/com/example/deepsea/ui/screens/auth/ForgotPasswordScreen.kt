package com.example.deepsea.ui.screens.auth

// Nếu bạn dùng thêm material icons thì import Icon hoặc painterResource nếu dùng drawable ảnh

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.deepsea.R
import com.example.deepsea.ui.viewmodel.auth.PasswordResetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordPage(
    navController: NavController,
    viewModel: PasswordResetViewModel = viewModel()
) {
    var currentStep by remember { mutableStateOf(ForgotPasswordStep.EMAIL) }
    var email by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf(List(5) { "" }) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Loading states
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Observe ViewModel results
    val emailRequestResult = viewModel.emailRequestResult.observeAsState()
    val verifyCodeResult = viewModel.verifyCodeResult.observeAsState()
    val resetPasswordResult = viewModel.resetPasswordResult.observeAsState()

    // Handle email request result
    LaunchedEffect(emailRequestResult.value) {
        emailRequestResult.value?.let { result ->
            isLoading = false
            result.fold(
                onSuccess = { response ->
                    if (response.success) {
                        currentStep = ForgotPasswordStep.VERIFY
                        errorMessage = ""
                    } else {
                        errorMessage = response.message
                    }
                },
                onFailure = {
                    errorMessage = "Failed to connect to server. Please try again."
                }
            )
        }
    }

    // Handle verify code result
    LaunchedEffect(verifyCodeResult.value) {
        verifyCodeResult.value?.let { result ->
            isLoading = false
            result.fold(
                onSuccess = { response ->
                    if (response.success) {
                        currentStep = ForgotPasswordStep.RESET
                        errorMessage = ""
                    } else {
                        errorMessage = response.message
                    }
                },
                onFailure = {
                    errorMessage = "Failed to verify code. Please try again."
                }
            )
        }
    }

    // Handle reset password result
    LaunchedEffect(resetPasswordResult.value) {
        resetPasswordResult.value?.let { result ->
            isLoading = false
            result.fold(
                onSuccess = { response ->
                    if (response.success) {
                        currentStep = ForgotPasswordStep.SUCCESS
                        errorMessage = ""
                    } else {
                        errorMessage = response.message
                    }
                },
                onFailure = {
                    errorMessage = "Failed to reset password. Please try again."
                }
            )
        }
    }

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
                // Display error message if any
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Show loading indicator
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp)
                    )
                }
                when (currentStep) {
                    ForgotPasswordStep.EMAIL -> {
                        EmailScreen(
                            email = email,
                            onEmailChange = { email = it },
                            onResetClicked = {
                                if (email.isNotBlank()) {
                                    isLoading = true
                                    errorMessage = ""
                                    viewModel.requestPasswordReset(email)
                                }
                            },
                            isLoading = isLoading
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
                                if (verificationCode.joinToString("").length == 5) {
                                    isLoading = true
                                    errorMessage = ""
                                    viewModel.verifyCode(email, verificationCode)
                                }
                            },
                            onResendClicked = {
                                isLoading = true
                                errorMessage = ""
                                viewModel.requestPasswordReset(email)
                            },
                            isLoading = isLoading
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
                                    isLoading = true
                                    errorMessage = ""
                                    viewModel.resetPassword(email, verificationCode, newPassword)
                                } else if (newPassword != confirmPassword) {
                                    errorMessage = "Passwords do not match"
                                }
                            },
                            isLoading = isLoading
                        )
                    }
                    ForgotPasswordStep.SUCCESS -> {
                        SuccessScreen(
                            onContinueClicked = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
