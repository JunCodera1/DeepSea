package com.example.deepsea.AI_assistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

private const val TAG = "VoiceAssistant"

@Composable
fun VoiceAssistantScreen() {
    val context = LocalContext.current
    var outputText by remember { mutableStateOf("Ask me anything and I'll try to help!") }
    var inputText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }


    val apiKey = "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"
    val endpoint = "https://api.openai.com/v1/chat/completions"

    // Initialize TextToSpeech
    val isTtsReady = remember { mutableStateOf(false) }

    val textToSpeech = remember {
        TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                isTtsReady.value = true
            }
        }
    }

    // Initialize SpeechRecognizer
    val recognizerIntent = remember {
        RecognizerIntent.getVoiceDetailsIntent(context).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        }
    }

    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onResults(bundle: Bundle) {
                    val matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    matches?.let {
                        if (it.isNotEmpty()) {
                            val query = it[0]
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
                                        textToSpeech.speak(response, TextToSpeech.QUEUE_FLUSH, null, null)
                                    }
                                },
                                onError = { error ->
                                    outputText = "Error: $error"
                                    isProcessing = false
                                }
                            )
                        }
                    }
                }

                // Required implementations for RecognitionListener
                override fun onReadyForSpeech(bundle: Bundle) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(v: Float) {}
                override fun onBufferReceived(bytes: ByteArray) {}
                override fun onEndOfSpeech() { isListening = false }
                override fun onError(i: Int) { isListening = false }
                override fun onPartialResults(bundle: Bundle) {}
                override fun onEvent(i: Int, bundle: Bundle) {}
            })
        }
    }

    // Request permissions
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as ComponentActivity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PackageManager.PERMISSION_GRANTED
            )
        }
    }

    // Clean up resources when the screen is closed
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
        Text(
            text = "AI Voice Assistant",
            fontSize = 24.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Input display
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            label = { Text("Your question") },
            readOnly = true
        )

        // Output display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Text(
                    text = outputText,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }

        // Control button
        Button(
            onClick = {
                if (textToSpeech.isSpeaking) {
                    textToSpeech.stop()
                } else if (!isListening) {
                    isListening = true
                    speechRecognizer.startListening(recognizerIntent)
                } else {
                    speechRecognizer.stopListening()
                    isListening = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isListening) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary
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
    context: android.content.Context,
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

    // Set timeout
    jsonRequest.retryPolicy = DefaultRetryPolicy(
        60000,
        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
    )

    // Add to queue
    Volley.newRequestQueue(context).add(jsonRequest)
}