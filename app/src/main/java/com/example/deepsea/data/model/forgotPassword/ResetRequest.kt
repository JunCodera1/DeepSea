package com.example.deepsea.data.model.forgotPassword

data class ResetRequest(val email: String, val code: String, val newPassword: String)