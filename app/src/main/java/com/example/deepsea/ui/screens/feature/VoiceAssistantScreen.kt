package com.example.deepsea.AI_assistant

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

private const val TAG = "VoiceAssistant"

class VoiceAssistantActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Yêu cầu quyền thu âm nếu chưa có
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PackageManager.PERMISSION_GRANTED
            )
        }

        setContent {
            VoiceAssistantScreen()
        }
    }
}

@Composable
fun DotIndicator(isActive: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 300
                -2f at 0
                2f at 150
                -2f at 300
            }
        )
    )

    Box(
        modifier = Modifier
            .size(12.dp)
            .offset(x = if (isActive) offsetX.dp else 0.dp)
            .clip(CircleShape)
            .background(
                if (isActive)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
    )
}

@Preview(showBackground = true)
@Composable
fun VoiceAssistantScreen() {
    val context = LocalContext.current
    var outputText by remember { mutableStateOf("Ask me anything and I'll try to help!") }
    var inputText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    // Thay 'YOUR_API_KEY' bằng OpenAI API key của bạn
    val apiKey = "YOUR_API_KEY"
    val endpoint = "https://api.openai.com/v1/chat/completions"

    // Khởi tạo TextToSpeech
    val isTtsReady = remember { mutableStateOf(false) }
    val textToSpeech = remember {
        TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                isTtsReady.value = true
            }
        }
    }

    // Khởi tạo SpeechRecognizer
    val recognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500)
        }
    }

    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() { isListening = false }
                override fun onError(error: Int) { isListening = false }
                override fun onResults(bundle: Bundle?) {
                    val matches = bundle
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    matches?.firstOrNull()?.let { query ->
                        inputText = query
                        callChatGPT(
                            context = context,
                            input = query,
                            apiKey = apiKey,
                            endpoint = endpoint,
                            onStart = { isProcessing = true },
                            onResponse = { response ->
                                outputText = response
                                isProcessing = false
                                if (!textToSpeech.isSpeaking) {
                                    textToSpeech.speak(response, TextToSpeech.QUEUE_FLUSH, null, "tts1")
                                }
                            },
                            onError = { error ->
                                outputText = "Error: $error"
                                isProcessing = false
                            }
                        )
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    // Dọn dẹp khi đóng
    DisposableEffect(Unit) {
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
            speechRecognizer.destroy()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "AI Voice Assistant",
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(8.dp))
            DotIndicator(isActive = isProcessing || textToSpeech.isSpeaking)
        }

        // Hiển thị input
        TextField(
            value = inputText,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            label = { Text("Your question") }
        )

        // Hiển thị output
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            if (isProcessing) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Text(text = outputText, fontSize = 16.sp)
            }
        }

        // Nút điều khiển
        Button(
            onClick = {
                when {
                    textToSpeech.isSpeaking -> textToSpeech.stop()
                    isListening -> {
                        speechRecognizer.stopListening()
                        isListening = false
                    }
                    else -> {
                        isListening = true
                        speechRecognizer.startListening(recognizerIntent)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isListening)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = when {
                    textToSpeech.isSpeaking -> "Stop Speaking"
                    isListening -> "Stop Listening"
                    else -> "Start Voice Assistant"
                }
            )
        }
    }
}

private fun callChatGPT(
    context: Context,
    input: String,
    apiKey: String,
    endpoint: String,
    onStart: () -> Unit,
    onResponse: (String) -> Unit,
    onError: (String) -> Unit
) {
    onStart()
    Log.d(TAG, "Calling ChatGPT with: $input")

    val jsonObject = JSONObject().apply {
        put("model", "gpt-3.5-turbo")
        put("messages", JSONArray().apply {
            put(JSONObject().apply {
                put("role", "user")
                put("content", input)
            })
        })
    }

    val jsonRequest = object : JsonObjectRequest(
        Method.POST, endpoint, jsonObject,
        { response ->
            try {
                val output = response.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                Log.d(TAG, "Response received: $output")
                onResponse(output)
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing response: ${e.message}")
                onError("Failed to parse response: ${e.message}")
            }
        },
        { error ->
            Log.e(TAG, "Error: ${error.message}")
            onError("Network error: ${error.message ?: "Unknown error"}")
        }
    ) {
        override fun getHeaders(): Map<String, String> {
            return mapOf(
                "Authorization" to "Bearer $apiKey",
                "Content-Type" to "application/json"
            )
        }
    }

    jsonRequest.retryPolicy = DefaultRetryPolicy(
        60000,
        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
    )

    Volley.newRequestQueue(context).add(jsonRequest)
}
