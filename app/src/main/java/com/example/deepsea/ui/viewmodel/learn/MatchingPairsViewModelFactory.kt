package com.example.deepsea.ui.viewmodel.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.deepsea.ui.screens.feature.learn.MatchingPairsViewModel

class MatchingPairsViewModelFactory(
    private val sectionId: Long,
    private val unitId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MatchingPairsViewModel(sectionId, unitId) as T
    }
}
