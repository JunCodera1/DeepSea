package com.example.deepsea.ui.viewmodel.auth

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.deepsea.utils.CloudinaryUploadService
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.auth.GoogleTokenRequest
import com.example.deepsea.data.model.auth.LoginRequest
import com.example.deepsea.data.model.auth.RegisterRequest
import com.example.deepsea.utils.DashboardState
import com.example.deepsea.utils.LoginState
import com.example.deepsea.utils.RegisterState
import com.example.deepsea.utils.SessionManager
import com.example.deepsea.utils.UserState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)
    private val cloudinaryUploadService = CloudinaryUploadService()

    // Flag to prevent auto-navigation after logout
    var isLoggingOut by mutableStateOf(false)
        private set

    // Flag to skip automatic navigation when not needed
    var skipAutoNavigation by mutableStateOf(false)
        private set

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    private val _userState = MutableStateFlow<UserState>(UserState.NotLoggedIn)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    private val _avatarUploadState = MutableStateFlow<AvatarUploadState>(AvatarUploadState.Idle)
    val avatarUploadState: StateFlow<AvatarUploadState> = _avatarUploadState.asStateFlow()

    init {
        viewModelScope.launch {
            if (isLoggingOut) {
                Timber.d("Init: Skipping session check due to ongoing logout")
                return@launch
            }
            val token = sessionManager.authToken.first()
            Timber.d("Init: Token = $token")
            if (token != null && sessionManager.isSessionValid().first()) {
                val username = sessionManager.username.first() ?: ""
                val email = sessionManager.email.first() ?: ""
                _userState.value = UserState.LoggedIn(username, email)
                Timber.d("Init: Session valid, setting UserState.LoggedIn for $username")
            } else {
                sessionManager.clearAuthData()
                _userState.value = UserState.NotLoggedIn
                Timber.d("Init: Session invalid or no token, setting UserState.NotLoggedIn")
            }
        }
    }

    fun login(email: String, password: String, navController: NavController) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = RetrofitClient.authApi.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val jwtResponse = response.body()!!
                    Timber.d("Login response: $jwtResponse")

                    sessionManager.saveAuthToken(
                        token = jwtResponse.token,
                        username = jwtResponse.username,
                        userId = jwtResponse.id,
                        email = jwtResponse.email,
                        profileId = jwtResponse.profile_id
                    )

                    _userState.value = UserState.LoggedIn(jwtResponse.username, jwtResponse.email)
                    _loginState.value = LoginState.Success

                    if (jwtResponse.firstLogin == true) {
                        navController.navigate("language-selection") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    }

                    Timber.d("Login successful, state updated to Success")
                } else {
                    _loginState.value = LoginState.Error("Login failed: ${response.message()}")
                    Timber.e("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error: ${e.message}")
                Timber.e("Login exception: ${e.message}", e)
            }
        }
    }

    fun signInWithGoogle(idToken: String, navController: NavController) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading
                Timber.d("Processing Google sign-in with token: ${idToken.take(10)}...")

                val response = RetrofitClient.authApi.loginWithGoogle(GoogleTokenRequest(idToken))

                if (response.isSuccessful && response.body() != null) {
                    val jwtResponse = response.body()!!
                    Timber.d("Google login response: $jwtResponse")

                    sessionManager.saveAuthToken(
                        token = jwtResponse.token,
                        username = jwtResponse.username,
                        userId = jwtResponse.id,
                        email = jwtResponse.email,
                        profileId = jwtResponse.profile_id
                    )

                    _userState.value = UserState.LoggedIn(jwtResponse.username, jwtResponse.email)
                    _loginState.value = LoginState.Success

                    if (jwtResponse.firstLogin == true) {
                        navController.navigate("language-selection") {
                            popUpTo("welcome") { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate("home") {
                            popUpTo("welcome") { inclusive = true }
                            launchSingleTop = true
                        }
                    }

                    Timber.d("Google login successful, navigating...")
                } else {
                    val errorMsg = "Google sign in failed: ${response.message()}"
                    Timber.e(errorMsg)
                    _loginState.value = LoginState.Error(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Google sign in error: ${e.message}"
                Timber.e(errorMsg, e)
                _loginState.value = LoginState.Error(errorMsg)
            }
        }
    }

    fun signInWithFacebook(token: String, navController: NavController) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading
                Timber.d("Processing Facebook sign-in...")
                _loginState.value = LoginState.Error("Facebook login not implemented yet")
            } catch (e: Exception) {
                val errorMsg = "Facebook sign in failed: ${e.message}"
                Timber.e(errorMsg, e)
                _loginState.value = LoginState.Error(errorMsg)
            }
        }
    }

    fun signup(name: String, username: String, email: String, password: String, avatar: Uri? = null) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val avatarUrl = if (avatar != null) {
                    uploadAvatarToCloudinary(avatar)
                } else {
                    null
                }

                val registerRequest = RegisterRequest(
                    name = name,
                    username = username,
                    password = password,
                    email = email,
                    avatarUrl = avatarUrl,
                )

                val response = RetrofitClient.authApi.register(registerRequest)

                if (response.isSuccessful && response.body() != null) {
                    _registerState.value = RegisterState.Success(response.body()!!.message)
                } else {
                    _registerState.value = RegisterState.Error("Registration failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Error: ${e.message}")
            }
        }
    }

    private suspend fun uploadAvatarToCloudinary(uri: Uri): String? {
        return try {
            _avatarUploadState.value = AvatarUploadState.Loading
            val context = getApplication<Application>().applicationContext
            val uploadedUrl = cloudinaryUploadService.uploadImage(context, uri)
            _avatarUploadState.value = AvatarUploadState.Success(uploadedUrl)
            uploadedUrl
        } catch (e: Exception) {
            Timber.e("Avatar upload failed: ${e.message}")
            _avatarUploadState.value = AvatarUploadState.Error("Failed to upload avatar: ${e.message}")
            null
        }
    }

    fun getRealPathFromURI(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        val path = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return path
    }

    fun logout(navController: NavController) {
        viewModelScope.launch {
            isLoggingOut = true
            skipAutoNavigation = true
            Timber.d("Logout: Starting logout process")

            // Clear auth data first
            sessionManager.clearAuthData()

            // Update states
            _userState.value = UserState.NotLoggedIn
            _loginState.value = LoginState.Idle
            _registerState.value = RegisterState.Idle
            _dashboardState.value = DashboardState.Loading

            Timber.d("Logout: Session cleared, UserState set to NotLoggedIn")

            // Wait for DataStore to persist
            delay(300)

            val tokenAfterClear = sessionManager.authToken.first()
            Timber.d("Logout: Token after clear = $tokenAfterClear")

            // Navigate to welcome screen and clear backstack
            navController.navigate("welcome") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }

            // Give the navigation time to complete
            delay(500)
            isLoggingOut = false

            // Keep skip flag true until next login
            Timber.d("Logout: Completed, navigated to welcome")
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }

    fun resetSkipAutoNavigation() {
        skipAutoNavigation = false
    }
}

sealed class AvatarUploadState {
    object Idle : AvatarUploadState()
    object Loading : AvatarUploadState()
    data class Success(val url: String) : AvatarUploadState()
    data class Error(val message: String) : AvatarUploadState()
}