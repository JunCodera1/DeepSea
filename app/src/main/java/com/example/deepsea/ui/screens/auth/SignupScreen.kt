package com.example.deepsea.ui.screens.auth

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val context = LocalContext.current

    // State for form fields
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    var isButtonEnabled by remember { mutableStateOf(true) }

    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

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
                Toast.makeText(context, "Welcome to DeepSea! 🎉", Toast.LENGTH_LONG).show()
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
                Toast.makeText(context, "Failed to upload avatar: ${(avatarUploadState as AvatarUploadState.Error).message}", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    DeepSeaTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            // Enhanced gradient background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF6B73FF),  // Blue-purple
                                Color(0xFF9DEDFF),  // Light cyan
                                Color(0xFFFFFFFF)   // White
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // App Logo or Icon Section
                AnimatedVisibility(
                    visible = isVisible,
                    enter = scaleIn(animationSpec = tween(600)) + fadeIn(animationSpec = tween(600))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Language learning icon
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.9f),
                                    shape = CircleShape
                                )
                                .shadow(8.dp, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Language Learning",
                                tint = Color(0xFF6B73FF),
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Text(
                            text = "Join DeepSea",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        Text(
                            text = "Start your English journey today",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White.copy(alpha = 0.9f)
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card container for form
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Avatar selection with enhanced design
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFF6B73FF).copy(alpha = 0.1f),
                                                Color(0xFF6B73FF).copy(alpha = 0.05f)
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = 3.dp,
                                        brush = Brush.linearGradient(
                                            colors = listOf(Color(0xFF6B73FF), Color(0xFF9DEDFF))
                                        ),
                                        shape = CircleShape
                                    )
                                    .clickable { imagePicker.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    avatarUploadState is AvatarUploadState.Loading -> {
                                        CircularProgressIndicator(
                                            color = Color(0xFF6B73FF),
                                            modifier = Modifier.size(40.dp),
                                            strokeWidth = 3.dp
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
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                        )
                                    }
                                    else -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AddAPhoto,
                                                contentDescription = "Add avatar",
                                                tint = Color(0xFF6B73FF),
                                                modifier = Modifier.size(36.dp)
                                            )
                                            Text(
                                                text = "Add Photo",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color(0xFF6B73FF),
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Enhanced form fields with modern styling
                        EnhancedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Full Name",
                            icon = Icons.Default.Person,
                            placeholder = "Enter your full name"
                        )

                        EnhancedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email Address",
                            icon = Icons.Default.Email,
                            placeholder = "your.email@example.com"
                        )

                        EnhancedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = "Username",
                            icon = Icons.Default.AccountCircle,
                            placeholder = "Choose a username"
                        )

                        EnhancedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            icon = Icons.Default.Lock,
                            placeholder = "Create a strong password",
                            isPassword = true
                        )

                        EnhancedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm Password",
                            icon = Icons.Default.LockClock,
                            placeholder = "Confirm your password",
                            isPassword = true
                        )

                        // Form validation checks
                        val isFormValid = name.isNotEmpty() &&
                                email.isNotEmpty() &&
                                email.contains("@") &&
                                username.isNotEmpty() &&
                                password.isNotEmpty() &&
                                password.length >= 6 &&
                                password == confirmPassword

                        // Enhanced password requirements
                        if (password.isNotEmpty()) {
                            PasswordRequirements(
                                password = password,
                                confirmPassword = confirmPassword
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Enhanced Sign Up button
                        Button(
                            onClick = {
                                if (isFormValid) {
                                    isButtonEnabled = false
                                    authViewModel.signup(name, username, email, password, avatarUri)
                                } else {
                                    Toast.makeText(context, "Please check all requirements", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(
                                    elevation = if (isFormValid && isButtonEnabled) 8.dp else 0.dp,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            enabled = isButtonEnabled && isFormValid,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFormValid && isButtonEnabled) {
                                    Color(0xFF6B73FF)
                                } else {
                                    Color.Gray.copy(alpha = 0.3f)
                                }
                            )
                        ) {
                            if (registerState is RegisterState.Loading || avatarUploadState is AvatarUploadState.Loading) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Text(
                                        text = "Creating Account...",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonAdd,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Text(
                                        text = "CREATE ACCOUNT",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Already have account section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already have an account? ",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray
                            )
                        )
                        TextButton(
                            onClick = { navController.navigate("login") }
                        ) {
                            Text(
                                text = "Sign In",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF6B73FF),
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun EnhancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    placeholder: String,
    isPassword: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.Gray.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF6B73FF).copy(alpha = 0.7f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6B73FF),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                focusedLabelColor = Color(0xFF6B73FF),
                cursorColor = Color(0xFF6B73FF)
            ),
            singleLine = true,
            visualTransformation = if (isPassword) {
                androidx.compose.ui.text.input.PasswordVisualTransformation()
            } else {
                androidx.compose.ui.text.input.VisualTransformation.None
            }
        )
    }
}

@Composable
fun PasswordRequirements(
    password: String,
    confirmPassword: String
) {
    val requirements = listOf(
        "At least 6 characters" to (password.length >= 6),
        "Passwords match" to (password == confirmPassword && confirmPassword.isNotEmpty())
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        requirements.forEach { (requirement, isMet) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isMet) Color(0xFF4CAF50) else Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = requirement,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isMet) Color(0xFF4CAF50) else Color.Gray
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EnhancedSignupScreenPreview() {
    // Preview implementation would go here
    // SignupPage(...)
}