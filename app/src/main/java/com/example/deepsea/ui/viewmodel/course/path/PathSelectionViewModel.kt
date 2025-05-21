package com.example.deepsea.ui.viewmodel.course.path

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.UserProfileService
import com.example.deepsea.data.model.course.language.LanguageOption
import com.example.deepsea.data.model.course.path.PathOption
import com.example.deepsea.data.model.course.path.PathOptionRequest
import com.example.deepsea.utils.Resource
import com.example.deepsea.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PathSelectionViewModel(
    private val pathService: UserProfileService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _userPaths = MutableStateFlow<Resource<Map<LanguageOption, PathOption>>>(Resource.Loading())
    val userPaths: StateFlow<Resource<Map<LanguageOption, PathOption>>> = _userPaths

    private val selectedPaths = mutableMapOf<LanguageOption, PathOption>()

    val currentSelections: Map<LanguageOption, PathOption>
        get() = selectedPaths.toMap()

    suspend fun fetchPaths(userId: Long) {
        try {
            _userPaths.value = Resource.Loading()
            val responseList = pathService.getUserPaths(userId)
            val fetchedPaths = responseList.associate { it.language to (it.path ?: PathOption.BEGINNER) }
            selectedPaths.clear()
            selectedPaths.putAll(fetchedPaths)
            _userPaths.value = Resource.Success(fetchedPaths)
        } catch (e: Exception) {
            _userPaths.value = Resource.Error("Failed to fetch paths: ${e.message}")
        }
    }


    fun setPath(language: LanguageOption, path: PathOption) {
        selectedPaths[language] = path
    }

    fun getSelectedPath(language: LanguageOption): PathOption? = selectedPaths[language]

    fun saveAllPaths(onError: (String) -> Unit = {}, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val profileId = sessionManager.profileId.first() ?: return@launch
                selectedPaths.forEach { (language, path) ->
                    pathService.savePath(PathOptionRequest(profileId, language, path))
                }
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Unexpected error")
            }
        }
    }


}

