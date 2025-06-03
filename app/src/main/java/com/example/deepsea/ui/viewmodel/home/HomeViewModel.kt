package com.example.deepsea.ui.viewmodel.home

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.dto.UserProgressDto
import com.example.deepsea.data.model.daily.DayStreakRequest
import com.example.deepsea.data.repository.CourseRepository
import com.example.deepsea.ui.components.SectionData
import com.example.deepsea.ui.components.UnitData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    private val _currentSectionIndex = mutableIntStateOf(0)
    val currentSectionIndex = _currentSectionIndex

    private val _currentUnitIndex = mutableIntStateOf(0)
    val currentUnitIndex = _currentUnitIndex

    // Saved progress in each section
    private val _sectionProgress = MutableStateFlow<Map<Long, Float>>(emptyMap())
    val sectionProgress: StateFlow<Map<Long, Float>> = _sectionProgress.asStateFlow()

    // Completed units
    private val _completedUnits = MutableStateFlow<Set<Long>>(emptySet())
    val completedUnits: StateFlow<Set<Long>> = _completedUnits.asStateFlow()

    // Completed stars - Map of unitId to set of completed star indices
    private val _completedStars = MutableStateFlow<Map<Long, Set<Int>>>(emptyMap())
    val completedStars: StateFlow<Map<Long, Set<Int>>> = _completedStars.asStateFlow()

    // User's total XP
    private val _totalXp = MutableStateFlow(0)
    val totalXp: StateFlow<Int> = _totalXp.asStateFlow()

    // User's streak
    private val _dailyStreak = MutableStateFlow(0)
    val dailyStreak: StateFlow<Int> = _dailyStreak.asStateFlow()

    // Store context for use throughout the ViewModel
    private var appContext: Context? = null

    // Navigation events
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent

    fun initialize(context: Context) {
        appContext = context.applicationContext
        loadCourseData()
    }

    // Overloaded version for backward compatibility
    fun loadCourseData() {
        appContext?.let {
            loadCourseData(it)
        } ?: run {
            _uiState.value = CourseUiState.Error("Context not initialized. Call initialize() first.")
            Timber.tag("HomeViewModel").e("Context not initialized. Call initialize() first.")
        }
    }

    fun loadCourseData(context: Context) {
        // Store context for future use
        if (appContext == null) {
            appContext = context.applicationContext
        }

        viewModelScope.launch {
            _uiState.value = CourseUiState.Loading
            Timber.tag("HomeViewModel").d("Starting to load course data")

            try {
                // Get sections and units
                val sectionsResult = courseRepository.getAllSections(context)

                if (sectionsResult.isSuccess) {
                    val sections = sectionsResult.getOrNull() ?: emptyList()
                    Timber.tag("HomeViewModel").d("Loaded sections: ${sections.size}")

                    if (sections.isEmpty()) {
                        Timber.tag("HomeViewModel").w("Sections list is empty from API")
                        useFallbackData()
                        return@launch
                    }

                    val units = sections.map { it.units }
                    Timber.tag("HomeViewModel").d("Units extracted from sections")

                    // Get user progress
                    val userProgressResult = courseRepository.getUserProgress(currentUserId)
                    val userProgress = userProgressResult.getOrNull()

                    if (userProgress != null) {
                        Timber.tag("HomeViewModel").d("User progress loaded successfully")
                        // Update completed units
                        _completedUnits.value = userProgress.completedUnits.toSet()
                        Timber.tag("HomeViewModel")
                            .d("Completed units: ${_completedUnits.value.size}")

                        // Load completed stars from user progress if available
                        // In a real app, this would come from the API
                        loadCompletedStars()

                        // Update XP and streak
                        _totalXp.value = userProgress.totalXp
                        _dailyStreak.value = userProgress.dailyStreak

                        // Find the appropriate section and unit
                        try {
                            // Extract section ID from "Section X: Title" format or use sectionId directly
                            val sectionIndex = findSectionIndex(sections, userProgress.currentSectionId)
                            if (sectionIndex != -1) {
                                _currentSectionIndex.intValue = sectionIndex
                                Timber.tag("HomeViewModel")
                                    .d("Set current section index to $sectionIndex")
                            } else {
                                Timber.tag("HomeViewModel")
                                    .w("Could not find matching section for ID ${userProgress.currentSectionId}")
                            }

                            // Similarly for unit index
                            if (sectionIndex != -1 && sectionIndex < units.size) {
                                val unitIndex = findUnitIndex(units[sectionIndex], userProgress.currentUnitId)
                                if (unitIndex != -1) {
                                    _currentUnitIndex.intValue = unitIndex
                                    Timber.tag("HomeViewModel")
                                        .d("Set current unit index to $unitIndex")
                                } else {
                                    Timber.tag("HomeViewModel")
                                        .w("Could not find matching unit for ID ${userProgress.currentUnitId}")
                                }
                            }
                        } catch (e: Exception) {
                            Timber.tag("HomeViewModel").e(e, "Error setting indices from user progress")
                        }
                    } else {
                        Timber.tag("HomeViewModel").w("User progress could not be loaded")
                    }

                    _uiState.value = CourseUiState.Success(
                        sections = sections,
                        units = units,
                        userProgress = userProgress
                    )
                    Timber.tag("HomeViewModel").d("UI state updated with loaded data")

                    // Calculate section progress after UI state is updated
                    updateSectionProgress()
                } else {
                    Timber.tag("HomeViewModel")
                        .w("Failed to load sections from API: ${sectionsResult.exceptionOrNull()?.message}")
                    useFallbackData()
                }
            } catch (e: Exception) {
                Timber.tag("HomeViewModel").e(e, "Exception during data loading")
                useFallbackData()
            }
        }
    }

    private fun loadCompletedStars() {
        // This is a sample implementation - in a real app this would come from the API
        // For now, we'll just create some random completed stars for demonstration

        val starsMap = mutableMapOf<Long, Set<Int>>()

        // For each completed unit, mark the first star as completed
        _completedUnits.value.forEach { unitId ->
            starsMap[unitId] = setOf(0)
        }

        // For the first unit (if any), complete more stars to demonstrate progression
        _completedUnits.value.firstOrNull()?.let { firstUnitId ->
            starsMap[firstUnitId] = setOf(0, 1, 2)
        }

        _completedStars.value = starsMap
        Timber.tag("HomeViewModel").d("Loaded completed stars: $starsMap")
    }

    private fun useFallbackData() {
        Timber.tag("HomeViewModel").d("Using fallback data")
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
        Timber.tag("HomeViewModel").d("Section progress updated: $progressMap")
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
        _currentSectionIndex.intValue = index
        Timber.tag("HomeViewModel").d("Current section updated to $index")
    }

    fun updateCurrentUnit(index: Int) {
        _currentUnitIndex.intValue = index
        Timber.tag("HomeViewModel").d("Current unit updated to $index")
    }

    fun completeStar(unitId: Long, starIndex: Int, earnedXp: Int = 5) {
        viewModelScope.launch {
            Timber.tag("HomeViewModel")
                .d("Completing star $starIndex for unit $unitId with $earnedXp XP")

            val currentStarsMap = _completedStars.value.toMutableMap()
            val unitStars = currentStarsMap[unitId]?.toMutableSet() ?: mutableSetOf()

            if (!unitStars.contains(starIndex)) {
                unitStars.add(starIndex)
                currentStarsMap[unitId] = unitStars
                _completedStars.value = currentStarsMap

                // Add XP
                _totalXp.value = _totalXp.value + earnedXp
                Timber.tag("HomeViewModel")
                    .d("Star $starIndex of unit $unitId completed, total XP now: ${_totalXp.value}")

                // If all stars for this unit are completed, mark the unit as completed
                if (unitStars.size == 5) { // Assuming 5 stars per unit
                    completeUnit(unitId, 0) // No additional XP since we already awarded it per star
                }

                // In a real app, you would send this update to the server
                // courseRepository.updateUserStarProgress(...)
            }
        }
    }

    fun completeUnit(unitId: Long, earnedXp: Int = 10) {
        viewModelScope.launch {
            Timber.tag("HomeViewModel").d("Completing unit $unitId with $earnedXp XP")
            val currentCompleted = _completedUnits.value.toMutableSet()
            if (!currentCompleted.contains(unitId)) {
                currentCompleted.add(unitId)
                _completedUnits.value = currentCompleted
                _totalXp.value = _totalXp.value + earnedXp
                Timber.tag("HomeViewModel")
                    .d("Unit $unitId completed, total XP now: ${_totalXp.value}")

                // Update streak if this is the first activity today
                updateDailyStreak()

                // Update section progress
                updateSectionProgress()
            }
        }
    }

    fun updateDailyStreak() {
        viewModelScope.launch {
            try {
                // Fetch user profile to get last login
                val userProgressResult = courseRepository.getUserProgress(currentUserId)
                val userProgress = userProgressResult.getOrNull()

                val lastLogin = userProgress?.lastLogin?.let {
                    LocalDate.parse(it.toString(), DateTimeFormatter.ISO_LOCAL_DATE)
                } ?: LocalDate.now().minusDays(1)

                val today = LocalDate.now()
                val newStreak = when {
                    lastLogin == today.minusDays(1) -> _dailyStreak.value + 1 // Continue streak
                    lastLogin == today -> _dailyStreak.value // Same day, no change
                    else -> 1 // Reset streak
                }

                // Update local streak
                _dailyStreak.value = newStreak
                Timber.tag("HomeViewModel").d("Daily streak updated to $newStreak")

                // Call API to update streak
                val response = RetrofitClient.userProfileService.updateDayStreak(
                    userId = currentUserId,
                    dayStreakRequest = DayStreakRequest(newStreak)
                )

                if (response.isSuccessful) {
                    Timber.tag("HomeViewModel").d("Streak updated on server: $newStreak")
                } else {
                    Timber.tag("HomeViewModel")
                        .e("Failed to update streak on server: ${response.message()}")
                }
            } catch (e: Exception) {
                Timber.tag("HomeViewModel").e(e, "Error updating streak: ${e.message}")
            }
        }
    }

    fun isUnitCompleted(unitId: Long): Boolean {
        return _completedUnits.value.contains(unitId)
    }

    fun isStarCompleted(unitId: Long, starIndex: Int): Boolean {
        return _completedStars.value[unitId]?.contains(starIndex) == true
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

    fun navigateToGuideBook(unitId: Long) {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.ToGuideBook(unitId))
        }
    }
}

// Navigation events
sealed class NavigationEvent {
    data class ToGuideBook(val unitId: Long) : NavigationEvent()
    // Add other navigation events as needed
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