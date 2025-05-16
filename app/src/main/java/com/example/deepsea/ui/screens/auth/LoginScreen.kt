package com.example.deepsea.ui.screens.auth

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    // Lấy scope của transition
    LocalSharedTransitionScope.current ?: error("No SharedTransitionScope found")
    LocalNavAnimatedVisibilityScope.current ?: error("No NavAnimatedVisibilityScope found")
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
                        // Gọi viewModel để xử lý đăng nhập với Facebook
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
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App logo",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                Text("Forgot password?")
            }

            Spacer(modifier = Modifier.height(24.dp))

            DeepSeaButton(
                onClick = { onSignInClick(email, password) },
                modifier = Modifier.width(90.dp),
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(24.dp))

            DeepSeaDivider(Modifier.align(Alignment.CenterHorizontally))

            Spacer(modifier = Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                ImageButton(
                    image = painterResource(id = R.drawable.facebook_icon),
                    text = "",
                    onClick = {
                        // Xử lý đăng nhập Facebook
                        LoginManager.getInstance().logInWithReadPermissions(
                            context as ActivityComponentActivity,
                            callbackManager,
                            listOf("public_profile", "email")
                        )
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                ImageButton(
                    image = painterResource(id = R.drawable.google_icon),
                    text = "",
                    onClick = {
                        // Xử lý đăng nhập Google
                        val signInIntent = googleSignInClient.signInIntent
                        googleSignInLauncher.launch(signInIntent)
                    }
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            TextButton(
                onClick = {
                    deepseaNavController.navigate("signup")
                }
            ) {
                Text("Don't have account? Sign Up.")
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
