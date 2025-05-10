package com.example.deepsea.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.dto.UserProgressDto
import com.example.deepsea.repository.CourseRepository
import com.example.deepsea.ui.components.SectionData
import com.example.deepsea.ui.components.UnitData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CourseUiState {
    object Loading : CourseUiState()
    data class Success(
        val sections: List<SectionData>,
        val units: List<List<UnitData>>,
        val userProgress: UserProgressDto? = null
    ) : CourseUiState()
    data class Error(val message: String) : CourseUiState()
}

class HomeViewModel(private val courseRepository: CourseRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<CourseUiState>(CourseUiState.Loading)
    val uiState: StateFlow<CourseUiState> = _uiState.asStateFlow()

    // Current user ID - in a real app, this would come from a user session or preferences
    private val currentUserId: Long = 1L

    // Current indices - Changed to MutableState for Compose compatibility
    private val _currentSectionIndex = mutableStateOf(0)
    val currentSectionIndex = _currentSectionIndex

    private val _currentUnitIndex = mutableStateOf(0)
    val currentUnitIndex = _currentUnitIndex

    // Saved progress in each section
    private val _sectionProgress = MutableStateFlow<Map<Long, Float>>(emptyMap())
    val sectionProgress: StateFlow<Map<Long, Float>> = _sectionProgress.asStateFlow()

    // Completed units
    private val _completedUnits = MutableStateFlow<Set<Long>>(emptySet())
    val completedUnits: StateFlow<Set<Long>> = _completedUnits.asStateFlow()

    // User's total XP
    private val _totalXp = MutableStateFlow(0)
    val totalXp: StateFlow<Int> = _totalXp.asStateFlow()

    // User's streak
    private val _dailyStreak = MutableStateFlow(0)
    val dailyStreak: StateFlow<Int> = _dailyStreak.asStateFlow()

    init {
        loadCourseData()
    }

    fun loadCourseData() {
        viewModelScope.launch {
            _uiState.value = CourseUiState.Loading
            Log.d("HomeViewModel", "Starting to load course data")

            try {
                // Get sections and units
                val sectionsResult = courseRepository.getAllSections()

                if (sectionsResult.isSuccess) {
                    val sections = sectionsResult.getOrNull() ?: emptyList()
                    Log.d("HomeViewModel", "Loaded sections: ${sections.size}")

                    if (sections.isEmpty()) {
                        Log.w("HomeViewModel", "Sections list is empty from API")
                        useFallbackData()
                        return@launch
                    }

                    val units = sections.map { it.units }
                    Log.d("HomeViewModel", "Units extracted from sections")

                    // Get user progress
                    val userProgressResult = courseRepository.getUserProgress(currentUserId)
                    val userProgress = userProgressResult.getOrNull()

                    if (userProgress != null) {
                        Log.d("HomeViewModel", "User progress loaded successfully")
                        // Update completed units
                        _completedUnits.value = userProgress.completedUnits?.toSet() ?: emptySet()
                        Log.d("HomeViewModel", "Completed units: ${_completedUnits.value.size}")

                        // Update XP and streak
                        _totalXp.value = userProgress.totalXp ?: 0
                        _dailyStreak.value = userProgress.dailyStreak ?: 0

                        // Find the appropriate section and unit
                        try {
                            // Extract section ID from "Section X: Title" format or use sectionId directly
                            val sectionIndex = findSectionIndex(sections, userProgress.currentSectionId)
                            if (sectionIndex != -1) {
                                _currentSectionIndex.value = sectionIndex
                                Log.d("HomeViewModel", "Set current section index to $sectionIndex")
                            } else {
                                Log.w("HomeViewModel", "Could not find matching section for ID ${userProgress.currentSectionId}")
                            }

                            // Similarly for unit index
                            if (sectionIndex != -1 && sectionIndex < units.size) {
                                val unitIndex = findUnitIndex(units[sectionIndex], userProgress.currentUnitId)
                                if (unitIndex != -1) {
                                    _currentUnitIndex.value = unitIndex
                                    Log.d("HomeViewModel", "Set current unit index to $unitIndex")
                                } else {
                                    Log.w("HomeViewModel", "Could not find matching unit for ID ${userProgress.currentUnitId}")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("HomeViewModel", "Error setting indices from user progress", e)
                        }
                    } else {
                        Log.w("HomeViewModel", "User progress could not be loaded")
                    }

                    _uiState.value = CourseUiState.Success(
                        sections = sections,
                        units = units,
                        userProgress = userProgress
                    )
                    Log.d("HomeViewModel", "UI state updated with loaded data")

                    // Calculate section progress after UI state is updated
                    updateSectionProgress()
                } else {
                    Log.w("HomeViewModel", "Failed to load sections from API: ${sectionsResult.exceptionOrNull()?.message}")
                    useFallbackData()
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception during data loading", e)
                useFallbackData()
            }
        }
    }

    private fun useFallbackData() {
        Log.d("HomeViewModel", "Using fallback data")
        val fallbackSections = courseRepository.getFallbackSectionData()
        val fallbackUnits = fallbackSections.map { it.units }

        _uiState.value = CourseUiState.Success(
            sections = fallbackSections,
            units = fallbackUnits
        )

        // Calculate section progress for fallback data
        updateSectionProgress()
    }

    private fun updateSectionProgress() {
        val currentState = _uiState.value
        if (currentState !is CourseUiState.Success) return

        val progressMap = mutableMapOf<Long, Float>()

        // For each section, calculate and store its progress
        currentState.sections.forEachIndexed { index, section ->
            val sectionId = extractSectionId(section.title)
            if (sectionId != null) {
                progressMap[sectionId] = calculateSectionProgressInternal(index, currentState.units)
            }
        }

        _sectionProgress.value = progressMap
        Log.d("HomeViewModel", "Section progress updated: $progressMap")
    }

    private fun extractSectionId(sectionTitle: String): Long? {
        // Try to extract a section ID from formats like "Section 1: Title" or "Section1: Title"
        val regex = "Section\\s*(\\d+)".toRegex(RegexOption.IGNORE_CASE)
        val matchResult = regex.find(sectionTitle)
        return matchResult?.groupValues?.getOrNull(1)?.toLongOrNull()
    }

    private fun findSectionIndex(sections: List<SectionData>, sectionId: Long): Int {
        // First try exact matching by section ID in title
        val exactIndex = sections.indexOfFirst {
            val extractedId = extractSectionId(it.title)
            extractedId == sectionId
        }

        if (exactIndex != -1) return exactIndex

        // If exact matching fails, try progressive matching (section 1 = index 0, etc.)
        return if (sectionId > 0 && sectionId <= sections.size) {
            (sectionId - 1).toInt()
        } else {
            -1
        }
    }

    private fun findUnitIndex(units: List<UnitData>, unitId: Long): Int {
        // Try to find by exact unit ID in title
        val exactIndex = units.indexOfFirst { unit ->
            val extractedId = extractUnitId(unit.title)
            extractedId == unitId
        }

        if (exactIndex != -1) return exactIndex

        // If exact matching fails, try progressive matching
        return if (unitId > 0 && unitId <= units.size) {
            (unitId - 1).toInt()
        } else {
            -1
        }
    }

    private fun extractUnitId(unitTitle: String): Long? {
        // Try to extract a unit ID from formats like "Unit 1" or "Unit1"
        val regex = "Unit\\s*(\\d+)".toRegex(RegexOption.IGNORE_CASE)
        val matchResult = regex.find(unitTitle)
        return matchResult?.groupValues?.getOrNull(1)?.toLongOrNull()
    }

    fun updateCurrentSection(index: Int) {
        _currentSectionIndex.value = index
        Log.d("HomeViewModel", "Current section updated to $index")
    }

    fun updateCurrentUnit(index: Int) {
        _currentUnitIndex.value = index
        Log.d("HomeViewModel", "Current unit updated to $index")
    }

    fun completeUnit(unitId: Long, earnedXp: Int = 10) {
        viewModelScope.launch {
            Log.d("HomeViewModel", "Completing unit $unitId with $earnedXp XP")
            val currentCompleted = _completedUnits.value.toMutableSet()
            if (!currentCompleted.contains(unitId)) {
                currentCompleted.add(unitId)
                _completedUnits.value = currentCompleted
                _totalXp.value = _totalXp.value + earnedXp
                Log.d("HomeViewModel", "Unit $unitId completed, total XP now: ${_totalXp.value}")

                // Update section progress after completing a unit
                updateSectionProgress()

                // In a real app, you would send this update to the server
                // courseRepository.updateUserProgress(...)
            }
        }
    }

    fun updateDailyStreak() {
        viewModelScope.launch {
            _dailyStreak.value = _dailyStreak.value + 1
            Log.d("HomeViewModel", "Daily streak updated to ${_dailyStreak.value}")

            // In a real app, you would send this update to the server
            // courseRepository.updateUserStreak(...)
        }
    }

    fun isUnitCompleted(unitId: Long): Boolean {
        return _completedUnits.value.contains(unitId)
    }

    fun calculateSectionProgress(sectionId: Long): Float {
        return _sectionProgress.value[sectionId] ?: 0f
    }

    private fun calculateSectionProgressInternal(sectionIndex: Int, allUnits: List<List<UnitData>>): Float {
        // Get units for the specified section
        val sectionUnits = allUnits.getOrNull(sectionIndex) ?: return 0f

        // If no units, return 0
        if (sectionUnits.isEmpty()) return 0f

        // Count completed units
        var completedCount = 0
        for (unit in sectionUnits) {
            val unitId = extractUnitId(unit.title)
            if (unitId != null && _completedUnits.value.contains(unitId)) {
                completedCount++
            }
        }

        // Calculate percentage
        return completedCount.toFloat() / sectionUnits.size
    }
}

// Factory class for creating HomeViewModel with dependencies
class HomeViewModelFactory(private val courseRepository: CourseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(courseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}