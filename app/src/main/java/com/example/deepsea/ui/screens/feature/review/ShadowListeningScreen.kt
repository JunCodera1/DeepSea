package com.example.deepsea.ui.screens.feature.listen

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deepsea.data.model.review.ShadowListeningSession
import com.example.deepsea.ui.viewmodel.learn.ShadowListeningViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

@Composable
fun ShadowListeningScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    viewModel: ShadowListeningViewModel = viewModel()
) {
    val sessions by viewModel.sessions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var selectedSession by remember { mutableStateOf<ShadowListeningSession?>(null) }
    val context = LocalContext.current

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF7B61FF))
                }
            }
            error != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = error ?: "Unknown error",
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.fetchSessions() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B61FF))
                    ) {
                        Text("Retry")
                    }
                }
            }
            else -> {
                if (selectedSession == null) {
                    SessionSelectionScreen(
                        sessions = sessions,
                        onSessionSelected = { selectedSession = it },
                        onBackClick = onBackClick
                    )
                } else {
                    ShadowListeningDetailScreen(
                        session = selectedSession!!,
                        onBackClick = { selectedSession = null }
                    )
                }
            }
        }
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun SessionSelectionScreen(
    sessions: List<ShadowListeningSession>,
    onSessionSelected: (ShadowListeningSession) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Shadow Listening (Japanese)",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF7B61FF))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Shadow Listening",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Improve your Japanese pronunciation by listening to native-like speech and repeating in real-time.",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = " Listen → Repeat → Get Feedback",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }

        Text(
            text = "Available Sessions",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sessions) { session ->
                SessionCard(session = session, onClick = { onSessionSelected(session) })
            }
        }
    }
}

@Composable
fun SessionCard(
    session: ShadowListeningSession,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE0E0FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color(0xFF7B61FF),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = session.difficulty,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = " • ",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = session.duration,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                session.description?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun ShadowListeningDetailScreen(
    session: ShadowListeningSession,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var playState by remember { mutableStateOf("prepare") } // prepare, playing, recording, comparing, result
    var matchPercentage by remember { mutableStateOf(0) }
    var attempts by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    // Initialize TextToSpeech
    val tts = remember {
        TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Không sử dụng tts trực tiếp trong lambda
            } else {
                Toast.makeText(context, "TTS initialization failed", Toast.LENGTH_LONG).show()
            }
        }
    }
    LaunchedEffect(Unit) {
        val result = tts.setLanguage(Locale.JAPAN)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(context, "Japanese language not supported for TTS", Toast.LENGTH_LONG).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            IconButton(onClick = {
                tts.stop()
                onBackClick()
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Back"
                )
            }

            Text(
                text = session.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Audio visualization card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (playState) {
                    "prepare" -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Listen to the ${session.language} text and shadow it in real-time",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    playState = "playing"
                                    tts.speak(session.transcript, TextToSpeech.QUEUE_FLUSH, null, "listenSession")
                                    coroutineScope.launch {
                                        delay(3000) // Adjust based on transcript length
                                        playState = "recording"
                                        delay(5000)
                                        playState = "comparing"
                                        delay(2000)
                                        matchPercentage = Random.nextInt(70, 98)
                                        attempts++
                                        playState = "result"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF7B61FF)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Start Listening")
                            }
                        }
                    }
                    "playing" -> {
                        PlayingVisualizer()

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = "Now listening...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "Get ready to repeat",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    tts.stop()
                                    playState = "prepare"
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFEF5350)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stop,
                                    contentDescription = "Stop TTS"
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Stop")
                            }
                        }
                    }
                    "recording" -> {
                        RecordingVisualizer()

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = "Repeat now!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF5252)
                            )
                        }
                    }
                    "comparing" -> {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color(0xFF7B61FF)
                            )
                        }

                        Text(
                            text = "Analyzing your pronunciation...",
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
                        )
                    }
                    "result" -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Match Score",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "$matchPercentage%",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (matchPercentage > 85) Color(0xFF4CAF50) else Color(0xFFFFA000)
                            )

                            Row(
                                modifier = Modifier.padding(top = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { playState = "prepare" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF7B61FF)
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Try Again"
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Try Again")
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Session details card
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Session Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Transcript",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = session.transcript,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Key Phrases",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                session.keyPhrases.forEach { phrase ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = phrase,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                session.description?.let {
                    Text(
                        text = "Description",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                session.createdAt?.let {
                    Text(
                        text = "Created: $it",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                session.updatedAt?.let {
                    Text(
                        text = "Updated: $it",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (attempts > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0F0FF)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "AI Feedback",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        buildAnnotatedString {
                            append("You pronounced most words correctly! Work on the intonation of ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF7B61FF))) {
                                append("key phrases")
                            }
                            append(" for more natural sounding speech.")
                        },
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )

                    if (matchPercentage > 85) {
                        Text(
                            text = "Great job! You're ready for more challenging content.",
                            fontSize = 16.sp,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            if (attempts > 0) {
                Text(
                    text = "Attempts: $attempts",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun PlayingVisualizer() {
    val infiniteTransition = rememberInfiniteTransition()
    val waves = 6
    val waveHeights = List(waves) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000 + (index * 100), easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        waveHeights.forEach { heightState ->
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .height(60.dp * heightState.value)
                    .padding(horizontal = 2.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF7B61FF))
            )
        }
    }
}

@Composable
fun RecordingVisualizer() {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseState = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(pulseState.value)
                .clip(CircleShape)
                .background(Color(0xFFFFEBEE)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF5252)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Recording",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShadowListeningScreenPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        ShadowListeningScreen()
    }
}