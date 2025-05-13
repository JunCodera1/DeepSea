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
import com.example.deepsea.data.api.RetrofitClient
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    private val _userState = MutableStateFlow<UserState>(UserState.NotLoggedIn)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

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
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading
                // Gọi API đăng nhập với Google
                // authRepository.signInWithGoogle(idToken)
                _loginState.value = LoginState.Success
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Google sign in failed")
            }
        }
    }

    // Facebook login
    fun signInWithFacebook(token: String) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading
                // Gọi API đăng nhập với Facebook
                // authRepository.signInWithFacebook(token)
                _loginState.value = LoginState.Success
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Facebook sign in failed")
            }
        }
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }

    fun signup(name:String , username: String, email: String, password: String, avatar: Uri? = null) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val avatarUrl = if (avatar != null) {
                    uploadAvatarAndGetUrl(avatar)
                } else {
                    null
                }

                val registerRequest = RegisterRequest(
                    name= name,
                    username = username,
                    password = password,
                    email = email,
                    avatarUrl = avatarUrl,
                )

                val response = RetrofitClient.authApi.register(registerRequest)

                if (response.isSuccessful && response.body() != null) {
                    _registerState.value = RegisterState.Success(response.body()!!.message + "Vjp Bro")
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
            val contentResolver = getApplication<Application>().contentResolver
            val file = File(getRealPathFromURI(getApplication(), uri) ?: return null)

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

            val imagePart = MultipartBody.Part.createFormData("avatar", file.name, requestFile)

            val response = RetrofitClient.authApi.uploadAvatar(imagePart)
            if (response.isSuccessful && response.body() != null) {
                return response.body()!!.url
            }
            null
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Avatar upload failed: ${e.message}")
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
}