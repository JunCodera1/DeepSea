package com.example.deepsea.ui.viewmodel.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.deepsea.utils.SessionManager
import com.example.deepsea.viewmodel.DailyGoalViewModel

class DailyGoalViewModelFactory(
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyGoalViewModel::class.java)) {
            return DailyGoalViewModel(sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
