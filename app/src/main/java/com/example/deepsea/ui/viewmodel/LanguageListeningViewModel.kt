package com.example.deepsea.ui.viewmodel
import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.HearingExercise
import com.example.deepsea.utils.AudioPlaybackManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class LanguageListeningViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentExercise = MutableStateFlow(
        HearingExercise(
            id = "1",
            audio = "audio_url",
            correctAnswer = "ください",
            options = listOf("ください", "おちゃ", "ごはん", "と")
        )
    )
    val currentExercise: StateFlow<HearingExercise> = _currentExercise.asStateFlow()

    private val _userProgress = MutableStateFlow(0.2f) // 20% progress
    val userProgress: StateFlow<Float> = _userProgress.asStateFlow()

    private val _hearts = MutableStateFlow(5)
    val hearts: StateFlow<Int> = _hearts.asStateFlow()

    private val _selectedOption = MutableStateFlow<String?>(null)
    val selectedOption: StateFlow<String?> = _selectedOption.asStateFlow()

    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    private val _isSpellingPlaying = MutableStateFlow(false)
    val isSpellingPlaying: StateFlow<Boolean> = _isSpellingPlaying.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private val apiService = RetrofitClient.authApi
    private val audioPlaybackManager = AudioPlaybackManager(application.applicationContext)

    init {
        loadExercise()
    }

    fun loadExercise() {
        viewModelScope.launch {
            try {
                val response = apiService.getExercise()
                if (response.isSuccessful && response.body() != null) {
                    _currentExercise.value = response.body()!!
                }
            } catch (e: Exception) {
                // Handle network errors
                e.printStackTrace()
            }
        }
    }

    fun playAudio() {
        viewModelScope.launch {
            _isAudioPlaying.value = true

            try {
                val audioUrl = currentExercise.value.audio
                audioPlaybackManager.playAudioFromUrl(audioUrl)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isAudioPlaying.value = false
            }
        }
    }

    fun playSlowAudio() {
        viewModelScope.launch {
            _isAudioPlaying.value = true

            try {
                val audioUrl = currentExercise.value.audio
                audioPlaybackManager.playAudioFromUrl(audioUrl, 0.5f)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isAudioPlaying.value = false
            }
        }
    }

    fun playWordSpelling(word: String) {
        viewModelScope.launch {
            _isSpellingPlaying.value = true

            try {
                // Gọi API để lấy thông tin phát âm từng chữ
                val response = apiService.getWordAudio(word)
                if (response.isSuccessful && response.body() != null) {
                    val audioResponse = response.body()!!

                    // Lấy thông tin từng chữ cái
                    val spellingResponse = apiService.getSpelling(word)
                    if (spellingResponse.isSuccessful && spellingResponse.body() != null) {
                        val spelling = spellingResponse.body()!!

                        // Phát âm từng chữ cái
                        for (audioUrl in spelling.audioUrls) {
                            audioPlaybackManager.playAudioFromUrl(audioUrl)
                            delay(800) // Đợi giữa mỗi âm tiết
                        }

                        // Phát âm toàn bộ từ
                        delay(500)
                        audioPlaybackManager.playAudioFromUrl(audioResponse.audioUrl)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isSpellingPlaying.value = false
            }
        }
    }

    fun checkAnswer(answer: String) {
        _selectedOption.value = answer

        // Phát âm từ được chọn
        playWordSpelling(answer)
    }

    fun checkExercise() {
        val currentAnswer = _selectedOption.value
        if (currentAnswer == currentExercise.value.correctAnswer) {
            // Correct answer
            _userProgress.value += 0.1f // Increase progress
            loadNextExercise()
        } else {
            // Wrong answer
            _hearts.value -= 1
        }
        _selectedOption.value = null
    }

    private fun loadNextExercise() {
        loadExercise()
    }

    override fun onCleared() {
        super.onCleared()
        audioPlaybackManager.releaseMediaPlayer()
    }
}

