package com.example.deepsea.ui.screens.auth

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.deepsea.R
import com.example.deepsea.ui.components.SignupTextField
import com.example.deepsea.ui.navigation.DeepSeaNavController
import com.example.deepsea.ui.theme.DeepSeaTheme
import com.example.deepsea.ui.viewmodel.auth.AuthViewModel
import com.example.deepsea.ui.viewmodel.auth.AvatarUploadState
import com.example.deepsea.utils.RegisterState

@Composable
fun SignupPage(
    onSignUpClick:(name: String, username: String, email: String, password: String, avatar: Uri?) -> Unit,
    onSignInClick: () -> Unit = {},
    navController: NavController,
    authViewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit
) {
    val scrollState = rememberScrollState()
    val backgroundPainter = painterResource(id = R.drawable.background_login)
    val context = LocalContext.current

    // State for form fields
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    var isButtonEnabled by remember { mutableStateOf(true) }

    // Get states from view model
    val registerState by authViewModel.registerState.collectAsState()
    val avatarUploadState by authViewModel.avatarUploadState.collectAsState()

    // Image picker launcher
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        avatarUri = uri
    }

    // Handle registration state changes
    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Success -> {
                Toast.makeText(context, "Registration successful!", Toast.LENGTH_LONG).show()
                onRegisterSuccess()
                authViewModel.resetRegisterState()
            }
            is RegisterState.Error -> {
                Toast.makeText(context, (registerState as RegisterState.Error).message, Toast.LENGTH_LONG).show()
                isButtonEnabled = true
            }
            is RegisterState.Loading -> {
                isButtonEnabled = false
            }
            else -> {
                isButtonEnabled = true
            }
        }
    }

    // Handle avatar upload state changes
    LaunchedEffect(avatarUploadState) {
        when (avatarUploadState) {
            is AvatarUploadState.Error -> {
                Toast.makeText(context, (avatarUploadState as AvatarUploadState.Error).message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

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

                Spacer(modifier = Modifier.height(24.dp))

                // Avatar selection with upload status
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { imagePicker.launch("image/*") }
                ) {
                    when {
                        avatarUploadState is AvatarUploadState.Loading -> {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        avatarUri != null -> {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(context)
                                        .data(avatarUri)
                                        .crossfade(true)
                                        .build()
                                ),
                                contentDescription = "Selected avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        else -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Add avatar",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                                Text(
                                    text = "Add Avatar",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                SignupTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeHolder = "Full Name",
                    label = "Full Name",
                )

                Spacer(modifier = Modifier.height(16.dp))

                SignupTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeHolder = "Email",
                    label = "Email",
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

                // Form validation checks
                val isFormValid = name.isNotEmpty() &&
                        email.isNotEmpty() &&
                        email.contains("@") &&
                        username.isNotEmpty() &&
                        password.isNotEmpty() &&
                        password == confirmPassword

                // Error message for password mismatch
                if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
                    Text(
                        text = "Passwords do not match",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        if (isFormValid) {
                            isButtonEnabled = false
                            authViewModel.signup(name, username, email, password, avatarUri)
                        } else {
                            Toast.makeText(context, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = isButtonEnabled && isFormValid
                ) {
                    if (registerState is RegisterState.Loading || avatarUploadState is AvatarUploadState.Loading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(text = "SIGN UP")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    TextButton(onClick = { navController.navigate("login") }) {
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
}

@Preview(showBackground = true)
@Composable
fun SignupScreenPreview() {
    // Preview implementation would go here
    // SignupPage(...)
}