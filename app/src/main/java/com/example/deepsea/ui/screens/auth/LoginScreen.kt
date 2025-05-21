package com.example.deepsea.ui.screens.auth

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.deepsea.ui.LocalNavAnimatedVisibilityScope
import com.example.deepsea.ui.LocalSharedTransitionScope
import com.example.deepsea.ui.components.DeepSeaButton
import com.example.deepsea.ui.components.DeepSeaDivider
import com.example.deepsea.ui.components.ImageButton
import com.example.deepsea.ui.components.LoginTextField
import com.example.deepsea.ui.navigation.DeepSeaNavController
import com.example.deepsea.ui.navigation.rememberDeepSeaNavController
import com.example.deepsea.ui.theme.DeepSeaTheme
import com.example.deepsea.ui.viewmodel.auth.AuthViewModel
import com.example.deepsea.utils.LoginState
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.activity.ComponentActivity as ActivityComponentActivity

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LoginPage(
    deepseaNavController: NavController,
    onSignInClick: (email: String, password: String) -> Unit,
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel
) {
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val backgroundPainter = painterResource(id = R.drawable.background_login)
    val loginState by authViewModel.loginState.collectAsState()
    val context = LocalContext.current

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
        if (result.resultCode == Activity.RESULT_OK) {
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
                errorMessage = "Google sign-in failed: ${e.statusMessage}"
            }
        } else {
            Log.e("WelcomePage", "Google sign-in canceled or failed: ${result.resultCode}")
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                Log.e("WelcomePage", "ApiException status: ${e.statusCode} - ${e.message}")
                showError = true
                errorMessage = if (e.statusCode == 12501) {
                    "Google sign-in was canceled by user"
                } else {
                    "Google sign-in failed: ${e.statusMessage}"
                }
            }
        }
    }

    LaunchedEffect(loginState) {
        Log.d("LoginPage", "Login state changed: $loginState")
        if (loginState is LoginState.Success) {
            Log.d("LoginPage", "Login successful, calling onLoginSuccess()")
            onLoginSuccess()
            authViewModel.resetLoginState()
        }
    }

    // Xử lý callback từ Facebook Login SDK
    DisposableEffect(Unit) {
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY)
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    result.accessToken.token?.let { token ->
                        authViewModel.signInWithFacebook(token, navController = deepseaNavController)
                    }
                }

                override fun onCancel() {
                    Log.d("LoginPage", "Facebook login cancelled")
                }

                override fun onError(error: FacebookException) {
                    Log.e("LoginPage", "Facebook login error: ${error.message}")
                }
            })

        onDispose {
            LoginManager.getInstance().unregisterCallback(callbackManager)
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    DeepSeaTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background with gradient overlay
            Image(
                painter = backgroundPainter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Blue.copy(alpha = 0.3f),
                                Color.Blue.copy(alpha = 0.6f),
                                Color.Blue.copy(alpha = 0.8f)
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
                verticalArrangement = Arrangement.Center
            ) {
                // App branding section
                Card(
                    modifier = Modifier.padding(vertical = 32.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "App logo",
                            modifier = Modifier.size(120.dp),
                            contentScale = ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "DeepSea",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Dive into Learning",
                            fontSize = 16.sp,
                            color = Color(0xFF666666),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Welcome message
                Text(
                    text = "Welcome Back!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Continue your learning journey",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Login form card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LoginTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeHolder = "Enter your email",
                            label = "Email"
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
                            onClick = {
                                deepseaNavController.navigate("forgot-password")
                            }
                        ) {
                            Text(
                                "Forgot password?",
                                color = Color(0xFF1565C0)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { onSignInClick(email, password) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                "Sign In",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Divider(
                                modifier = Modifier.weight(1f),
                                color = Color.Gray.copy(alpha = 0.4f)
                            )
                            Text(
                                "or continue with",
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                            Divider(
                                modifier = Modifier.weight(1f),
                                color = Color.Gray.copy(alpha = 0.4f)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = {
                                    LoginManager.getInstance().logInWithReadPermissions(
                                        context as ActivityComponentActivity,
                                        callbackManager,
                                        listOf("public_profile", "email")
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF1877F2)
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.facebook_icon),
                                        contentDescription = "Facebook",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Facebook",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    val signInIntent = googleSignInClient.signInIntent
                                    googleSignInLauncher.launch(signInIntent)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFDB4437)
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.google_icon),
                                        contentDescription = "Google",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Google",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign up link
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    )
                ) {
                    TextButton(
                        onClick = {
                            deepseaNavController.navigate("signup")
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "Don't have an account? ",
                            color = Color.Gray
                        )
                        Text(
                            "Sign Up",
                            color = Color(0xFF1565C0),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Error message
                if (showError) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Red.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val deepSeaNavController = rememberDeepSeaNavController()
    // You can mock authViewModel for preview if needed
}