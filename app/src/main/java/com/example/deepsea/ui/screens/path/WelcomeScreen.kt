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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deepsea.R
import com.example.deepsea.ui.components.ImageButton
import com.example.deepsea.ui.navigation.DeepSeaNavController
import com.example.deepsea.ui.theme.DeepSeaTheme
import com.example.deepsea.ui.viewmodel.auth.AuthViewModel
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.activity.ComponentActivity as ActivityComponentActivity


@Composable
fun WelcomePage(
    navController: DeepSeaNavController,
    authViewModel: AuthViewModel
) {
    val scrollState = rememberScrollState()
    val backgroundPainter = painterResource(id = R.drawable.background_login)
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
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { token ->
                    // Gọi viewModel để xử lý đăng nhập với Google
                    authViewModel.signInWithGoogle(token)
                }
            } catch (e: ApiException) {
                Log.e("LoginPage", "Google sign-in failed: ${e.message}")
            }
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
                    onClick = { navController.navController.navigate("login") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("SIGN IN")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nút Sign Up
                Button(
                    onClick = { navController.navController.navigate("signup")},
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
                            LoginManager.getInstance().logInWithReadPermissions(
                            context as ActivityComponentActivity,
                            callbackManager,
                            listOf("public_profile", "email")
                        ) }
                    )
                    ImageButton(
                        image = googleIcon,
                        text = "",
                        onClick = {
                            val signInIntent = googleSignInClient.signInIntent
                            googleSignInLauncher.launch(signInIntent)
                        })
                }
            }
        }
    }
}


@Composable
@Preview
fun PreviewWelcomeScreen(){
    WelcomePage(
        navController = TODO(),
        authViewModel = TODO()
    )
}
