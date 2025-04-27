package com.example.deepsea.ui.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.model.forgotPassword.PasswordResetResponse
import com.example.deepsea.data.repository.PasswordResetRepository
import kotlinx.coroutines.launch

class PasswordResetViewModel : ViewModel() {
    private val repository = PasswordResetRepository()

    private val _emailRequestResult = MutableLiveData<Result<PasswordResetResponse>>()
    val emailRequestResult: LiveData<Result<PasswordResetResponse>> = _emailRequestResult

    private val _verifyCodeResult = MutableLiveData<Result<PasswordResetResponse>>()
    val verifyCodeResult: LiveData<Result<PasswordResetResponse>> = _verifyCodeResult

    private val _resetPasswordResult = MutableLiveData<Result<PasswordResetResponse>>()
    val resetPasswordResult: LiveData<Result<PasswordResetResponse>> = _resetPasswordResult

    fun requestPasswordReset(email: String) {
        viewModelScope.launch {
            val result = repository.requestPasswordReset(email)
            _emailRequestResult.value = result
        }
    }

    fun verifyCode(email: String, code: List<String>) {
        viewModelScope.launch {
            val codeString = code.joinToString("")
            val result = repository.verifyCode(email, codeString)
            _verifyCodeResult.value = result
        }
    }

    fun resetPassword(email: String, code: List<String>, newPassword: String) {
        viewModelScope.launch {
            val codeString = code.joinToString("")
            val result = repository.resetPassword(email, codeString, newPassword)
            _resetPasswordResult.value = result
        }
    }
}