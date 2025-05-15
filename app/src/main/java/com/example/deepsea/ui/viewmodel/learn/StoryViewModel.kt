package com.example.deepsea.ui.viewmodel.learn

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.model.review.Story
import com.example.deepsea.data.repository.StoryRepository
import kotlinx.coroutines.launch

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    var storiesState by mutableStateOf<StoriesState>(StoriesState.Loading)
        private set

    fun loadStories() {
        viewModelScope.launch {
            storiesState = StoriesState.Loading
            val result = storyRepository.getAllStories()
            storiesState = when {
                result.isSuccess -> StoriesState.Success(result.getOrNull() ?: emptyList())
                else -> StoriesState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
}

sealed class StoriesState {
    object Loading : StoriesState()
    data class Success(val stories: List<Story>) : StoriesState()
    data class Error(val message: String) : StoriesState()
}