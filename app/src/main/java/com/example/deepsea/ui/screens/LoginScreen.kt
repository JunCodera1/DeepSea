package com.example.deepsea.ui.screens

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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
fun LoginPage( navController: DeepSeaNavController,
               onLoginSuccess: () -> Unit,
               authViewModel: AuthViewModel) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No Scope found")
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalStateException("No Scope found")
    val scrollState = rememberScrollState()
    val backgroundPainter = painterResource(id = R.drawable.background_login)

    val loginState by authViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess()
            authViewModel.resetLoginState()
        }
    }
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
            val imageModifier = Modifier
                .size(200.dp)
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentScale = ContentScale.Fit,
                modifier = imageModifier,
                contentDescription = ""

            )

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(8.dp))

            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            LoginTextField(
                value = email,
                onValueChange = { email = it },
                placeHolder = "Enter your email",
                label = "Email",
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
                onClick = {}
            ) {
                Text("Forgot password ?")
            }

            Spacer(modifier = Modifier.height(24.dp))

            DeepSeaButton(
                onClick = {
                    // Handle login logic here
                },
                modifier = Modifier.width(90.dp),
            ) {
                Text(text = "Login")
            }
            Spacer(modifier = Modifier.height(24.dp))

            DeepSeaDivider(Modifier.align(Alignment.CenterHorizontally))

            Spacer(modifier = Modifier.height(18.dp))
            val facebookIcon = painterResource(id = R.drawable.facebook_icon)
            val googleIcon = painterResource(id = R.drawable.google_icon)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                ImageButton(
                    image = facebookIcon,
                    text = "",
                    onClick = { /* your action */ }
                )
                ImageButton(
                    image = googleIcon,
                    text = "",
                    onClick = { /* your action */ }
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
//    LoginPage(navController = deepSeaNavController)
}
