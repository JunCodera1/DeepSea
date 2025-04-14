package com.example.deepsea.utils

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val message: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val message: String) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

sealed class UserState {
    object NotLoggedIn : UserState()
    data class LoggedIn(val username: String, val email: String) : UserState()
}