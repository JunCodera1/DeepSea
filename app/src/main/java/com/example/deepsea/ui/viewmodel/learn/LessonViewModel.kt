package com.example.deepsea.ui.viewmodel.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.model.exercise.LessonResult
import com.example.deepsea.data.repository.LessonRepository
import com.example.deepsea.data.repository.RepositoryFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LessonViewModel(
    private val repository: LessonRepository = RepositoryFactory.lessonRepository
) : ViewModel() {

    private val _lessonResult = MutableStateFlow<LessonResult?>(null)
    val lessonResult: StateFlow<LessonResult?> = _lessonResult

    fun saveLessonResult(xp: Int, time: String, accuracy: Int) {
        viewModelScope.launch {
            val result = LessonResult(xp, time, accuracy)
            repository.saveLessonResult(result)
            _lessonResult.value = result
        }
    }

    fun getLessonResult(lessonId: Long) {
        viewModelScope.launch {
            _lessonResult.value = repository.getLessonResult(lessonId)
        }
    }

    class LessonViewModelFactory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LessonViewModel::class.java)) {
                return LessonViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}