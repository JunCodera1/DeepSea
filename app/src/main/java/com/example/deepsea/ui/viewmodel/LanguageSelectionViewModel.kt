package com.example.deepsea.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.LanguageApiService
import com.example.deepsea.data.model.LanguageData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LanguageSelectionViewModel : ViewModel() {
    private val _availableLanguages = MutableStateFlow<List<LanguageData>>(emptyList())
    val availableLanguages = _availableLanguages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchAvailableLanguages() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Call to API to get available languages
                val languages = LanguageApiService.getAvailableLanguages()
                _availableLanguages.value = languages
            } catch (e: Exception) {
                // Handle error
                Log.e("LanguageVM", "Error fetching languages", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}