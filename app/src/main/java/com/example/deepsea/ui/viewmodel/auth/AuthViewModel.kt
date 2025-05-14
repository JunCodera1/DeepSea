package com.example.deepsea.ui.viewmodel.auth

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)
    private val cloudinaryUploadService = CloudinaryUploadService()

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
            val token = sessionManager.authToken.first()
            if (token != null) {
                _userState.value = UserState.LoggedIn(
                    username = sessionManager.username.first() ?: "",
                    email = sessionManager.email.first() ?: ""
                )
                loadDashboard()
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
                    Log.d("AuthViewModel", "Login response: $jwtResponse")

                    val username = jwtResponse.username
                    val userEmail = jwtResponse.email

                    sessionManager.saveAuthToken(
                        token = jwtResponse.token,
                        username = username,
                        userId = jwtResponse.id,
                        email = userEmail,
                        profileId = jwtResponse.profile_id
                    )

                    _userState.value = UserState.LoggedIn(username, userEmail)
                    _loginState.value = LoginState.Success

                    if (jwtResponse.firstLogin == true) {
                        navController.navigate("language-selection") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }

                    Log.d("AuthViewModel", "Login successful, state updated to Success")
                } else {
                    _loginState.value = LoginState.Error("Login failed: ${response.message()}")
                    Log.e("AuthViewModel", "Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error: ${e.message}")
                Log.e("AuthViewModel", "Login exception: ${e.message}", e)
            }
        }
    }

    // Google login
    fun signInWithGoogle(idToken: String, navController: NavController) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading
                Log.d("AuthViewModel", "Processing Google sign-in with token: ${idToken.take(10)}...")

                // Gọi API đăng nhập với Google
                val response = RetrofitClient.authApi.loginWithGoogle(GoogleTokenRequest(idToken))

                if (response.isSuccessful && response.body() != null) {
                    val jwtResponse = response.body()!!
                    Log.d("AuthViewModel", "Google login response: $jwtResponse")

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
                        }
                    } else {
                        navController.navigate("home") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }

                    Log.d("AuthViewModel", "Google login successful, navigating...")
                } else {
                    val errorMsg = "Google sign in failed: ${response.message()}"
                    Log.e("AuthViewModel", errorMsg)
                    _loginState.value = LoginState.Error(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Google sign in error: ${e.message}"
                Log.e("AuthViewModel", errorMsg, e)
                _loginState.value = LoginState.Error(errorMsg)
            }
        }
    }

    // Facebook login
    fun signInWithFacebook(token: String, navController: NavController) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading
                Log.d("AuthViewModel", "Processing Facebook sign-in...")

                // Gọi API đăng nhập với Facebook
                // TODO: Implement actual Facebook login API call
                // val response = RetrofitClient.authApi.loginWithFacebook(FacebookTokenRequest(token))

                // Temporary placeholder response handling - replace with actual API call
                _loginState.value = LoginState.Error("Facebook login not implemented yet")
            } catch (e: Exception) {
                val errorMsg = "Facebook sign in failed: ${e.message}"
                Log.e("AuthViewModel", errorMsg, e)
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
            Log.e("AuthViewModel", "Avatar upload failed: ${e.message}")
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

    fun loadDashboard() {
        viewModelScope.launch {
            _dashboardState.value = DashboardState.Loading
            try {
                val token = sessionManager.authToken.first()
                if (token != null) {
                    val response = RetrofitClient.authApi.getDashboard("Bearer $token")
                    if (response.isSuccessful && response.body() != null) {
                        _dashboardState.value = DashboardState.Success(response.body()!!.message)
                    } else {
                        if (response.code() == 401) {
                            logout()
                        }
                        _dashboardState.value = DashboardState.Error("Failed to load dashboard: ${response.message()}")
                    }
                } else {
                    _dashboardState.value = DashboardState.Error("You are not logged in")
                    _userState.value = UserState.NotLoggedIn
                }
            } catch (e: Exception) {
                _dashboardState.value = DashboardState.Error("Error: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearAuthData()
            _userState.value = UserState.NotLoggedIn
            _loginState.value = LoginState.Idle
            _dashboardState.value = DashboardState.Loading
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }
}

sealed class AvatarUploadState {
    object Idle : AvatarUploadState()
    object Loading : AvatarUploadState()
    data class Success(val url: String) : AvatarUploadState()
    data class Error(val message: String) : AvatarUploadState()
}