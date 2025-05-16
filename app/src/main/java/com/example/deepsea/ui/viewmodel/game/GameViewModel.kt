package com.example.deepsea.ui.viewmodel.game

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.game.*
import com.example.deepsea.ui.screens.feature.game.Question as UiQuestion // Alias for UI Question model
import kotlinx.coroutines.launch
import retrofit2.Response

class GameViewModel : ViewModel() {
    var currentPlayer = mutableStateOf<Player?>(null)
    private val apiService = RetrofitClient.gameApiService

    fun startMatch(request: GameStartRequest, onSuccess: (Match) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response: Response<Match> = apiService.startMatch(request)
                if (response.isSuccessful) {
                    response.body()?.let { match ->
                        onSuccess(match)
                    } ?: onError("Empty response body")
                } else {
                    onError("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
            }
        }
    }

    fun getMatchQuestions(matchId: Long, onSuccess: (List<UiQuestion>) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response: Response<List<Question>> = apiService.getMatchQuestions(matchId)
                if (response.isSuccessful) {
                    response.body()?.let { dataQuestions ->
                        // Map data model to UI model
                        val uiQuestions = dataQuestions.map { dataQuestion ->
                            UiQuestion(
                                id = dataQuestion.id,
                                text = dataQuestion.text,
                                options = dataQuestion.options,
                                correctAnswer = dataQuestion.correctAnswer,
                                gameMode = dataQuestion.gameMode,
                                language = dataQuestion.language,
                                explanation = "TODO()"
                            )
                        }
                        onSuccess(uiQuestions)
                    } ?: onError("Empty response body")
                } else {
                    onError("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
            }
        }
    }

    fun submitAnswer(request: GameAnswerRequest, onSuccess: (AnswerResponse) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response: Response<AnswerResponse> = apiService.submitAnswer(request)
                if (response.isSuccessful) {
                    response.body()?.let { answerResponse ->
                        onSuccess(answerResponse)
                    } ?: onError("Empty response body")
                } else {
                    onError("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
            }
        }
    }

    // Placeholder Player model (adjust based on your UI needs)
    data class Player(
        val id: String,
        val name: String,
        val level: Int,
        val xp: Int
    )
}