package com.example.deepsea.ui.screens

// Nếu bạn dùng thêm material icons thì import Icon hoặc painterResource nếu dùng drawable ảnh

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
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
                        containerColor = androidx.compose.ui.graphics.Color.Transparent
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

@Composable
fun EmailScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    onResetClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Forgot password",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            text = "Please enter your email to reset the password",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "Your Email",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            placeholder = { Text("Enter your email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        Button(
            onClick = onResetClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (email.isNotBlank()) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        ) {
            Text("Reset Password")
        }
    }
}

@Composable
fun VerifyCodeScreen(
    email: String,
    verificationCode: List<String>,
    onCodeChange: (Int, String) -> Unit,
    onVerifyClicked: () -> Unit,
    onResendClicked: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    // Create focus requesters for each input field
    val focusRequesters = List(5) { FocusRequester() }

    // Create focus manager to handle focus changes
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Check your email",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            text = "We sent a reset link to $email\nenter 5 digit code that mentioned in the email",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 0 until 5) {
                OutlinedTextField(
                    value = verificationCode[i],
                    onValueChange = { value ->
                        if (value.length <= 1 && value.all { it.isDigit() }) {
                            onCodeChange(i, value)

                            // Auto-advance to next field if current field is filled
                            if (value.isNotEmpty() && i < 4) {
                                focusRequesters[i + 1].requestFocus()
                            }
                        }
                    },
                    modifier = Modifier
                        .width(56.dp)
                        .height(56.dp)
                        .focusRequester(focusRequesters[i])
                        .onKeyEvent { keyEvent ->
                            // Handle backspace to go to previous field
                            if (keyEvent.key == Key.Backspace && verificationCode[i].isEmpty() && i > 0) {
                                focusRequesters[i - 1].requestFocus()
                                return@onKeyEvent true
                            }
                            false
                        },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center
                    )
                )
            }
        }

        // Handle pasted verification code
        DisposableEffect(Unit) {
            onDispose {
                // Clean up if needed
            }
        }

        Button(
            onClick = onVerifyClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (verificationCode.any { it.isNotBlank() })
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        ) {
            Text("Verify Code")
        }

        TextButton(
            onClick = onResendClicked,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        ) {
            Text("Haven't got the email yet? Resend email")
        }

        // Additional feature: allow pasting the entire code
        TextButton(
            onClick = {
                clipboardManager.getText()?.text?.let { clipboardText ->
                    val digits = clipboardText.filter { it.isDigit() }.take(5)
                    if (digits.isNotEmpty()) {
                        digits.forEachIndexed { index, c ->
                            if (index < 5) {
                                onCodeChange(index, c.toString())
                            }
                        }
                        // Focus on the field after the last entered digit or the last field
                        val nextFocusIndex = minOf(digits.length, 4)
                        focusRequesters[nextFocusIndex].requestFocus()
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp)
        ) {
            Text("Paste code from clipboard")
        }
    }

    // Request focus on the first field when the screen is shown
    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }
}

@Composable
fun SetNewPasswordScreen(
    newPassword: String,
    confirmPassword: String,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onUpdateClicked: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Set a new password",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            text = "Create a new password. Ensure it differs from previous ones for security",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "Password",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Enter your new password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    val icon = if (passwordVisible) R.drawable.visibility_off_eye
                    else R.drawable.visibility_eye
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        )

        Text(
            text = "Confirm Password",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            placeholder = { Text("Re-enter password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    val icon = if (confirmPasswordVisible) R.drawable.visibility_off_eye
                    else R.drawable.visibility_eye
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                    )
                }
            }
        )

        Button(
            onClick = onUpdateClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (newPassword.isNotBlank() && confirmPassword.isNotBlank())
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        ) {
            Text("Update Password")
        }
    }
}

@Composable
fun SuccessScreen(onContinueClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Successful",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Congratulations! Your password has been changed. Click continue to login",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = onContinueClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Update Password")
        }
    }
}

enum class ForgotPasswordStep {
    EMAIL,
    VERIFY,
    RESET,
    SUCCESS
}