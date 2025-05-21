package com.example.deepsea.AI_assistant

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "VoiceAssistant"

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: Uri? = null,
    val imageBitmap: Bitmap? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceAssistantScreen() {
    val context = LocalContext.current
    var chatMessages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var isTtsEnabled by remember { mutableStateOf(true) }
    var currentModel by remember { mutableStateOf("gemini-1.5-flash") }
    var showSettings by remember { mutableStateOf(false) }
    var showImageOptions by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Replace with your actual Gemini API key
    val apiKey = "AIzaSyAn5wxvHlI3AFK_YCQ5-qbnwRLJeBqeu5g"

    // Image capture/selection launchers
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImage = it
            selectedBitmap = loadBitmapFromUri(context, it)
        }
    }

    val cameraUri = remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraUri.value?.let { uri ->
                selectedImage = uri
                selectedBitmap = loadBitmapFromUri(context, uri)
            }
        }
    }

    // Initialize TextToSpeech
    val isTtsReady = remember { mutableStateOf(false) }
    val textToSpeech = remember {
        TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                isTtsReady.value = true
            }
        }
    }

    // Monitor TTS speaking state
    LaunchedEffect(isTtsReady.value) {
        if (isTtsReady.value) {
            while (true) {
                isSpeaking = textToSpeech.isSpeaking
                delay(100)
            }
        }
    }

    // Initialize SpeechRecognizer
    val recognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 3000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500)
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
                        }
                    }
                    isListening = false
                }

                override fun onError(error: Int) {
                    isListening = false
                    Log.e(TAG, "Speech recognition error: $error")
                }

                override fun onReadyForSpeech(bundle: Bundle) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(v: Float) {}
                override fun onBufferReceived(bytes: ByteArray) {}
                override fun onEndOfSpeech() { isListening = false }
                override fun onPartialResults(bundle: Bundle) {}
                override fun onEvent(i: Int, bundle: Bundle) {}
            })
        }
    }

    fun sendMessage(message: String, imageUri: Uri? = null, imageBitmap: Bitmap? = null) {
        if (message.isBlank() && imageUri == null) return

        val userMessage = ChatMessage(
            content = message.ifBlank { "Hãy mô tả hình ảnh này" },
            isUser = true,
            imageUri = imageUri,
            imageBitmap = imageBitmap
        )
        chatMessages = chatMessages + userMessage
        inputText = ""
        selectedImage = null
        selectedBitmap = null

        scope.launch {
            listState.animateScrollToItem(chatMessages.size - 1)
        }

        callGeminiAPI(
            context = context,
            messages = chatMessages,
            model = currentModel,
            apiKey = apiKey,
            onStart = { isProcessing = true },
            onResponse = { response ->
                val assistantMessage = ChatMessage(content = response, isUser = false)
                chatMessages = chatMessages + assistantMessage
                isProcessing = false

                scope.launch {
                    listState.animateScrollToItem(chatMessages.size - 1)
                }

                if (isTtsEnabled && !textToSpeech.isSpeaking) {
                    textToSpeech.speak(response, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            },
            onError = { error ->
                val errorMessage = ChatMessage(content = "Error: $error", isUser = false)
                chatMessages = chatMessages + errorMessage
                isProcessing = false
            }
        )
    }

    // Request permissions
    LaunchedEffect(Unit) {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    context as ComponentActivity,
                    permissions,
                    PackageManager.PERMISSION_GRANTED
                )
            }
        }
    }

    // Clean up resources
    DisposableEffect(Unit) {
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
            speechRecognizer.destroy()
        }
    }

    // Image Options Dialog
    if (showImageOptions) {
        AlertDialog(
            onDismissRequest = { showImageOptions = false },
            title = { Text("Chọn ảnh") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            // Create image file for camera
                            val imageFile = File(context.cacheDir, "camera_image_${System.currentTimeMillis()}.jpg")
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                imageFile
                            )
                            cameraUri.value = uri
                            cameraLauncher.launch(uri)
                            showImageOptions = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Camera, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chụp ảnh")
                    }

                    TextButton(
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                            showImageOptions = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Photo, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chọn từ thư viện")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showImageOptions = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    // Settings Dialog
    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            title = { Text("Settings") },
            text = {
                Column {
                    Text("AI Model", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    listOf("gemini-1.5-flash", "gemini-1.5-pro", "gemini-1.0-pro-vision").forEach { model ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { currentModel = model }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentModel == model,
                                onClick = { currentModel = model }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(model)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Text-to-Speech", fontWeight = FontWeight.Bold)
                        Switch(
                            checked = isTtsEnabled,
                            onCheckedChange = { isTtsEnabled = it }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettings = false }) {
                    Text("Close")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    "Gemini Voice Assistant",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            actions = {
                IconButton(onClick = { showSettings = true }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
                IconButton(onClick = {
                    chatMessages = emptyList()
                    textToSpeech.stop()
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear Chat")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Chat Messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (chatMessages.isEmpty()) {
                item {
                    WelcomeMessage()
                }
            }

            items(chatMessages) { message ->
                ChatMessageItem(message = message)
            }

            if (isProcessing) {
                item {
                    TypingIndicator()
                }
            }
        }

        // Selected Image Preview
        selectedBitmap?.let { bitmap ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Ảnh đã chọn:", fontWeight = FontWeight.Bold)
                        IconButton(onClick = {
                            selectedImage = null
                            selectedBitmap = null
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Remove image")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Selected image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // Input Area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Text Input
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Nhập tin nhắn hoặc chọn ảnh để hỏi...") },
                    trailingIcon = {
                        if (inputText.isNotBlank() || selectedImage != null) {
                            IconButton(onClick = {
                                sendMessage(inputText, selectedImage, selectedBitmap)
                            }) {
                                Icon(Icons.Default.Send, contentDescription = "Send")
                            }
                        }
                    },
                    shape = RoundedCornerShape(24.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Control Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Voice Input Button
                    AnimatedVoiceButton(
                        isListening = isListening,
                        onClick = {
                            if (!isListening) {
                                isListening = true
                                speechRecognizer.startListening(recognizerIntent)
                            } else {
                                speechRecognizer.stopListening()
                                isListening = false
                            }
                        }
                    )

                    // Image Button
                    FilledIconButton(
                        onClick = { showImageOptions = true },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(Icons.Default.Image, contentDescription = "Add Image")
                    }

                    // Stop Speaking Button
                    if (isSpeaking) {
                        FilledIconButton(
                            onClick = { textToSpeech.stop() },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop Speaking")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeMessage() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Assistant,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Welcome to Gemini Assistant!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Hỏi đáp bằng văn bản, giọng nói hoặc ảnh. Tận hưởng trải nghiệm AI đa phương tiện!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            Avatar(
                icon = Icons.Default.Assistant,
                backgroundColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        Card(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .animateContentSize(),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            shape = RoundedCornerShape(
                topStart = if (message.isUser) 16.dp else 4.dp,
                topEnd = if (message.isUser) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Display image if present
                message.imageBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Message image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = message.content,
                    color = if (message.isUser) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeFormat.format(Date(message.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (message.isUser) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    }
                )
            }
        }

        if (message.isUser) {
            Avatar(
                icon = Icons.Default.Person,
                backgroundColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun Avatar(
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Avatar(
            icon = Icons.Default.Assistant,
            backgroundColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 8.dp)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) { index ->
                    val infiniteTransition = rememberInfiniteTransition(label = "typing")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = index * 200),
                            repeatMode = RepeatMode.Reverse
                        ), label = "dot"
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedVoiceButton(
    isListening: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isListening) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    Box(
        modifier = Modifier
            .size(64.dp)
            .scale(scale)
    ) {
        if (isListening) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha),
                        shape = CircleShape
                    )
                    .padding(4.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha * 0.5f),
                        shape = CircleShape
                    )
            )
        }

        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (isListening) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        ) {
            Icon(
                if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                contentDescription = if (isListening) "Stop Listening" else "Start Listening",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// Utility functions
private fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        Log.e(TAG, "Error loading bitmap from URI: ${e.message}")
        null
    }
}

private fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

private fun callGeminiAPI(
    context: Context,
    messages: List<ChatMessage>,
    model: String,
    apiKey: String,
    onStart: () -> Unit,
    onResponse: (String) -> Unit,
    onError: (String) -> Unit
) {
    onStart()
    Log.d(TAG, "Calling Gemini API with model: $model")

    val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

    // Convert chat history to Gemini format
    val contentsArray = JSONArray()

    if (messages.isNotEmpty()) {
        val lastMessage = messages.last()
        val partsArray = JSONArray()

        // Add text content
        partsArray.put(JSONObject().put("text", lastMessage.content))

        // Add image if present
        lastMessage.imageBitmap?.let { bitmap ->
            val base64Image = bitmapToBase64(bitmap)
            partsArray.put(JSONObject().apply {
                put("inline_data", JSONObject().apply {
                    put("mime_type", "image/jpeg")
                    put("data", base64Image)
                })
            })
        }

        contentsArray.put(JSONObject().apply {
            put("parts", partsArray)
        })
    }

    val jsonObject = JSONObject().apply {
        put("contents", contentsArray)
        put("generationConfig", JSONObject().apply {
            put("temperature", 0.7)
            put("maxOutputTokens", 1000)
            put("topP", 0.8)
            put("topK", 40)
        })
        put("safetySettings", JSONArray().apply {
            put(JSONObject().apply {
                put("category", "HARM_CATEGORY_HARASSMENT")
                put("threshold", "BLOCK_MEDIUM_AND_ABOVE")
            })
            put(JSONObject().apply {
                put("category", "HARM_CATEGORY_HATE_SPEECH")
                put("threshold", "BLOCK_MEDIUM_AND_ABOVE")
            })
            put(JSONObject().apply {
                put("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT")
                put("threshold", "BLOCK_MEDIUM_AND_ABOVE")
            })
            put(JSONObject().apply {
                put("category", "HARM_CATEGORY_DANGEROUS_CONTENT")
                put("threshold", "BLOCK_MEDIUM_AND_ABOVE")
            })
        })
    }

    val jsonRequest = object : JsonObjectRequest(
        Method.POST, endpoint, jsonObject,
        { response ->
            try {
                val candidates = response.getJSONArray("candidates")
                if (candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    if (parts.length() > 0) {
                        val text = parts.getJSONObject(0).getString("text")
                        Log.d(TAG, "Response received: $text")
                        onResponse(text)
                    } else {
                        onError("No text in response")
                    }
                } else {
                    onError("No candidates in response")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing response: ${e.message}")
                onError("Error parsing response: ${e.message}")
            }
        },
        { error ->
            Log.e(TAG, "API Error: ${error.message}")
            val errorMessage = when (error.networkResponse?.statusCode) {
                401 -> "API key invalid"
                403 -> "API quota exceeded"
                404 -> "Model not found"
                429 -> "Rate limit exceeded"
                500 -> "Server error"
                else -> "Network error: ${error.message}"
            }
            onError(errorMessage)
        }
    ) {
        override fun getHeaders(): Map<String, String> {
            return mapOf(
                "Content-Type" to "application/json; charset=UTF-8"
            )
        }
    }

    // Set retry policy
    jsonRequest.retryPolicy = DefaultRetryPolicy(
        30000, // 30 seconds timeout
        0, // No retries
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
    )

    // Add request to queue
    val requestQueue = Volley.newRequestQueue(context)
    requestQueue.add(jsonRequest)
}


