package com.example.deepsea.ui.viewmodel.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.review.ShadowListeningSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShadowListeningViewModel : ViewModel() {
    private val _sessions = MutableStateFlow<List<ShadowListeningSession>>(emptyList())
    val sessions: StateFlow<List<ShadowListeningSession>> = _sessions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchSessions()
    }

    fun fetchSessions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.shadowListeningApiService.getSessions()
                _sessions.value = response
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load sessions: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}