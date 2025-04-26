package com.example.deepsea.ui.screens.auth

import android.util.Log
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.example.deepsea.ui.viewmodel.AuthViewModel
import com.example.deepsea.utils.LoginState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LoginPage(
    navController: DeepSeaNavController,
    onSignInClick: (email: String, password: String) -> Unit,
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel
) {
    // Lấy scope của transition
    LocalSharedTransitionScope.current ?: error("No SharedTransitionScope found")
    LocalNavAnimatedVisibilityScope.current ?: error("No NavAnimatedVisibilityScope found")

    val scrollState = rememberScrollState()
    val backgroundPainter = painterResource(id = R.drawable.background_login)
    val loginState by authViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        Log.d("LoginPage", "Login state changed: $loginState")
        if (loginState is LoginState.Success) {
            Log.d("LoginPage", "Login successful, calling onLoginSuccess()")
            onLoginSuccess()
            authViewModel.resetLoginState()
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
                    navController.navController.navigate("forgot-password")
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
                    onClick = { /* TODO: Facebook login */ }
                )
                Spacer(modifier = Modifier.width(16.dp))
                ImageButton(
                    image = painterResource(id = R.drawable.google_icon),
                    text = "",
                    onClick = { /* TODO: Google login */ }
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            TextButton(
                onClick = {
                    navController.navController.navigate("signup")
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
