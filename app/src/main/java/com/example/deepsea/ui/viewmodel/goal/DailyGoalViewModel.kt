package com.example.deepsea.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.goal.DailyGoalOption
import com.example.deepsea.data.model.goal.DailyGoalRequest
import com.example.deepsea.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DailyGoalViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val _selectedGoal = MutableStateFlow<DailyGoalOption?>(null)
    val selectedGoal: StateFlow<DailyGoalOption?> = _selectedGoal

    fun selectGoal(goal: DailyGoalOption) {
        _selectedGoal.value = goal
    }

    fun submitGoal(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val profileId = sessionManager.profileId.first() ?: return@launch
            val selected = _selectedGoal.value ?: return@launch

            val request = DailyGoalRequest(goal = selected, userId = profileId)

            try {
                val response = RetrofitClient.userProfileService.updateDailyGoal(
                    profileId = profileId,
                    dailyGoal = request
                )

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Failed with status ${response.code()}")
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}
