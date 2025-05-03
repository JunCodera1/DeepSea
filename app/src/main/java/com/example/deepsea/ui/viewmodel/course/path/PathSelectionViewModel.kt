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
import com.example.deepsea.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PathSelectionViewModel(
    private val pathService: UserProfileService,
    private val sessionManager: SessionManager
) : ViewModel() {

    var userPaths by mutableStateOf<Map<LanguageOption, PathOption>>(emptyMap())
        private set

    private val selectedPaths = mutableMapOf<LanguageOption, PathOption>()

    val currentSelections: Map<LanguageOption, PathOption>
        get() = selectedPaths

    suspend fun fetchPaths(userId: Long) {
        val responseList = pathService.getUserPaths(userId)
        val fetchedPaths = responseList.associate { it.language to (it.path ?: PathOption.BEGINNER) }
        userPaths = fetchedPaths
        selectedPaths.clear()
        selectedPaths.putAll(fetchedPaths)
    }


    fun setPath(language: LanguageOption, path: PathOption) {
        selectedPaths[language] = path
    }

    fun getSelectedPath(language: LanguageOption): PathOption? = selectedPaths[language]

    fun saveAllPaths() {
        viewModelScope.launch {
            val profileId = sessionManager.profileId.first() ?: return@launch
            selectedPaths.forEach { (language, path) ->
                pathService.savePath(PathOptionRequest(profileId, language, path))
            }
        }
    }

}

