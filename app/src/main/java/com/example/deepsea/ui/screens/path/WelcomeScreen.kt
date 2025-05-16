package com.example.deepsea.ui.screens.path

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                // Tiêu đề
                Text(
                    text = "WELCOME BACK",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Nút Sign In
                OutlinedButton(
                    onClick = { deepseaNavController.navigate("login") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("SIGN IN")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nút Sign Up
                Button(
                    onClick = { deepseaNavController.navigate("signup")},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("SIGN UP")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Login with Social Media")

                Spacer(modifier = Modifier.height(12.dp))

                // Mạng xã hội
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val facebookIcon = painterResource(id = R.drawable.facebook_icon)
                    val googleIcon = painterResource(id = R.drawable.google_icon)

                    ImageButton(
                        image = facebookIcon,
                        text = "",
                        onClick = {
                            Log.d("WelcomePage", "Facebook login button clicked")
                            LoginManager.getInstance().logInWithReadPermissions(
                                context as ComponentActivity,
                                callbackManager,
                                listOf("public_profile", "email")
                            )
                        }
                    )
                    ImageButton(
                        image = googleIcon,
                        text = "",
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
                        }
                    )
                }

                // Loading indicator
                if (loginState is LoginState.Loading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
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
                    }
                ) {
                    Text(errorMessage)
                }
            }
        }
    }
}


@Composable
@Preview
fun PreviewWelcomeScreen(){
    // This is a preview, not intended for actual use
    // So using TODO() is acceptable here
    /*
    WelcomePage(
        navController = TODO(),
        authViewModel = TODO()
    )
    */
}