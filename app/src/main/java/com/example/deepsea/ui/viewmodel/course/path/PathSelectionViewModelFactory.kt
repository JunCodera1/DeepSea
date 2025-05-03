package com.example.deepsea.ui.viewmodel.course.path

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.deepsea.data.api.UserProfileService
import com.example.deepsea.utils.SessionManager

class PathSelectionViewModelFactory(
    private val pathService: UserProfileService,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PathSelectionViewModel::class.java)) {
            return PathSelectionViewModel(pathService, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
