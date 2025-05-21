package com.example.deepsea.ui.screens.path

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.deepsea.R
import com.example.deepsea.ui.components.ImageButton
import com.example.deepsea.ui.navigation.DeepSeaNavController
import com.example.deepsea.ui.theme.DeepSeaTheme
import com.example.deepsea.ui.viewmodel.auth.AuthViewModel
import com.example.deepsea.utils.LoginState
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun WelcomePage(
    deepseaNavController: NavController,
    authViewModel: AuthViewModel
) {
    val scrollState = rememberScrollState()
    val backgroundPainter = painterResource(id = R.drawable.background_login)
    val context = LocalContext.current
    val loginState by authViewModel.loginState.collectAsState()
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Google Sign-In Client
    val googleSignInClient by remember {
        mutableStateOf(
            GoogleSignIn.getClient(
                context,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.google_client_id))
                    .requestEmail()
                    .build()
            )
        )
    }

    // Facebook Callback Manager
    val callbackManager = remember { CallbackManager.Factory.create() }

    // Xử lý kết quả đăng nhập từ Google
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("WelcomePage", "Google sign-in result received: ${result.resultCode}")
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { token ->
                Log.d("WelcomePage", "Google ID token obtained: ${token.take(10)}...")
                authViewModel.signInWithGoogle(token, deepseaNavController)
            } ?: run {
                Log.e("WelcomePage", "Google sign-in failed: ID token is null")
                showError = true
                errorMessage = "Google sign-in failed: Couldn't get ID token"
            }
        } catch (e: ApiException) {
            Log.e("WelcomePage", "Google sign-in failed: ${e.statusCode} - ${e.message}")
            showError = true
            errorMessage = when (e.statusCode) {
                10 -> "Google sign-in failed: Developer error (check Client ID, SHA-1, or package name)"
                12501 -> "" // User cancellation, no error message
                else -> "Google sign-in failed: ${e.statusMessage ?: "Unknown error"} (Status: ${e.statusCode})"
            }
        } catch (e: Exception) {
            Log.e("WelcomePage", "Unexpected error during Google sign-in: ${e.message}", e)
            showError = true
            errorMessage = "Unexpected error: ${e.message}"
        }
    }

    // Xử lý trạng thái đăng nhập
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                Log.d("WelcomePage", "Login successful, navigating...")
                deepseaNavController.navigate("home") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
            is LoginState.Error -> {
                Log.e("WelcomePage", "Login error: ${(loginState as LoginState.Error).message}")
                showError = true
                errorMessage = (loginState as LoginState.Error).message
                authViewModel.resetLoginState()
            }
            else -> {}
        }
    }

    DeepSeaTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background with gradient overlay
            Image(
                painter = backgroundPainter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 40.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header với logo và tiêu đề
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App Logo (giả sử có icon app)
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                Color.White,
                                CircleShape
                            )
                            .shadow(8.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Placeholder cho logo app
                        Text(
                            text = "🌟",
                            fontSize = 40.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // App name
                    Text(
                        text = "DeepSea",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subtitle
                    Text(
                        text = "Explore the World of Languages",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }

                // Main content card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Welcome message
                        Text(
                            text = "Welcome Back!",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Continue your language learning journey",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Sign In Button
                        Button(
                            onClick = { deepseaNavController.navigate("login") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = "SIGN IN",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sign Up Button
                        OutlinedButton(
                            onClick = { deepseaNavController.navigate("signup") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = "CREATE ACCOUNT",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Divider với text
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(Color.Gray.copy(alpha = 0.3f))
                            )

                            Text(
                                text = "or sign in with",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(Color.Gray.copy(alpha = 0.3f))
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Social media buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Facebook button
                            SocialLoginButton(
                                icon = painterResource(id = R.drawable.facebook_icon),
                                backgroundColor = Color(0xFF1877F2),
                                onClick = {
                                    Log.d("WelcomePage", "Facebook login button clicked")
                                    LoginManager.getInstance().logInWithReadPermissions(
                                        context as ComponentActivity,
                                        callbackManager,
                                        listOf("public_profile", "email")
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )

                            // Google button
                            SocialLoginButton(
                                icon = painterResource(id = R.drawable.google_icon),
                                backgroundColor = Color.White,
                                onClick = {
                                    Log.d("WelcomePage", "Google sign-in button clicked")
                                    try {
                                        val signInIntent = googleSignInClient.signInIntent
                                        googleSignInLauncher.launch(signInIntent)
                                    } catch (e: Exception) {
                                        Log.e("WelcomePage", "Failed to launch Google sign-in: ${e.message}")
                                        showError = true
                                        errorMessage = "Failed to launch Google sign-in: ${e.message}"
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Loading indicator
                        if (loginState is LoginState.Loading) {
                            Spacer(modifier = Modifier.height(24.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Bottom features
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎯 Master new languages",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "📚 Interactive lessons",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🏆 Track your progress",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Error message
            if (showError) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        Button(onClick = { showError = false }) {
                            Text("Dismiss")
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(errorMessage)
                }
            }
        }
    }
}

@Composable
fun SocialLoginButton(
    icon: androidx.compose.ui.graphics.painter.Painter,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}