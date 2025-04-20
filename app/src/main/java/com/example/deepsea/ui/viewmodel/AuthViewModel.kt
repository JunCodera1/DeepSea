package com.example.deepsea.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.JwtResponse
import com.example.deepsea.data.model.LoginRequest
import com.example.deepsea.data.model.MessageResponse
import com.example.deepsea.data.model.RegisterRequest
import com.example.deepsea.utils.DashboardState
import com.example.deepsea.utils.LoginState
import com.example.deepsea.utils.RegisterState
import com.example.deepsea.utils.SessionManager
import com.example.deepsea.utils.UserState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    // Login states
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    // Register states
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    // Dashboard states
    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    // User info states
    private val _userState = MutableStateFlow<UserState>(UserState.NotLoggedIn)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    init {
        // Kiểm tra xem người dùng đã đăng nhập chưa khi khởi tạo ViewModel
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

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = RetrofitClient.authApi.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val jwtResponse = response.body()!!
                    Log.d("AuthViewModel", "Login response: $jwtResponse")

                    // Extract username and email from JWT token if they're empty in the response
                    val username = if (jwtResponse.username.isNullOrEmpty()) {
                        // You can extract username from token or use a default/placeholder value
                        "user" // or decode from token
                    } else {
                        jwtResponse.username
                    }

                    val userEmail = if (jwtResponse.email.isNullOrEmpty()) {
                        // Use the email that was used for login
                        email
                    } else {
                        jwtResponse.email
                    }

                    // Save token and user info
                    sessionManager.saveAuthToken(
                        jwtResponse.token,
                        username,
                        jwtResponse.id ?: 0, // Provide default if null
                        userEmail
                    )

                    _userState.value = UserState.LoggedIn(username, userEmail)
                    _loginState.value = LoginState.Success
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

    fun signup(name:String , username: String, email: String, password: String, avatar: Uri? = null) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                // Handle avatar upload if present
                val avatarUrl = if (avatar != null) {
                    uploadAvatarAndGetUrl(avatar)
                } else {
                    null
                }

                // Create registration request with avatar URL
                val registerRequest = RegisterRequest(
                    name= name,
                    username = username,
                    password = password,
                    email = email,
                    avatarUrl = avatarUrl
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

    private suspend fun uploadAvatarAndGetUrl(uri: Uri): String? {
        return try {
            // Get the content resolver and file data
            val contentResolver = getApplication<Application>().contentResolver
            val inputStream = contentResolver.openInputStream(uri)

            // Create a MultipartBody.Part for the image
            val requestFile = inputStream?.readBytes()?.let {
                RequestBody.create("image/*".toMediaTypeOrNull(), it)
            }

            val imagePart = requestFile?.let {
                MultipartBody.Part.createFormData(
                    "avatar",
                    "avatar_image.jpg",
                    it
                )
            }

            // Upload the image
            if (imagePart != null) {
                val response = RetrofitClient.authApi.uploadAvatar(imagePart)
                if (response.isSuccessful && response.body() != null) {
                    return response.body()!!.url
                }
            }
            null
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Avatar upload failed: ${e.message}")
            null
        }
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
                        // Token hết hạn hoặc không hợp lệ
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