package com.example.deepsea.ui.screens.feature.listen

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

// Model class representing a shadow listening session
data class ShadowListeningSession(
    val id: String,
    val title: String,
    val difficulty: String,
    val duration: String,
    val transcript: String,
    val keyPhrases: List<String>
)

// Simulated ViewModel with updated data for Japanese sessions
class ListenViewModel {
    fun getShadowListeningSessions(): List<ShadowListeningSession> {
        return listOf(
            // Dữ liệu cũ (giữ nguyên)
            ShadowListeningSession(
                id = "1",
                title = "Greetings in Japanese",
                difficulty = "Beginner",
                duration = "1:00",
                transcript = "こんにちは、私は田中です。よろしくお願いします。",
                keyPhrases = listOf("こんにちは", "よろしくお願いします")
            ),
            ShadowListeningSession(
                id = "2",
                title = "Ordering Food",
                difficulty = "Intermediate",
                duration = "1:30",
                transcript = "すみません、ラーメンと餃子をください。お水もお願いします。",
                keyPhrases = listOf("すみません", "ラーメンと餃子", "お水も")
            ),
            ShadowListeningSession(
                id = "3",
                title = "Asking for Directions",
                difficulty = "Advanced",
                duration = "2:00",
                transcript = "駅に行きたいのですが、どうやって行きますか？この道をまっすぐ行って、右に曲がってください。",
                keyPhrases = listOf("駅に行きたい", "どうやって", "右に曲がって")
            ),
            ShadowListeningSession(
                id = "4",
                title = "Shopping in Japan",
                difficulty = "Beginner",
                duration = "1:20",
                transcript = "このシャツはいくらですか？3000円です。かしこまりました。",
                keyPhrases = listOf("いくらですか", "3000円", "かしこまりました")
            ),
            ShadowListeningSession(
                id = "5",
                title = "Talking About Hobbies",
                difficulty = "Intermediate",
                duration = "1:40",
                transcript = "趣味は何ですか？私はアニメを見るのが好きです。週末によく見ます。",
                keyPhrases = listOf("趣味は何", "アニメを見る", "週末に")
            ),
            ShadowListeningSession(
                id = "6",
                title = "Making Plans",
                difficulty = "Advanced",
                duration = "2:10",
                transcript = "来週の土曜日に映画を見に行きませんか？いいですね、何時に会いましょうか？午後2時でどうですか？",
                keyPhrases = listOf("映画を見に行きませんか", "何時に会いましょう", "午後2時")
            ),

            // Dữ liệu mới
            ShadowListeningSession(
                id = "7",
                title = "Visiting the Doctor",
                difficulty = "Intermediate",
                duration = "1:50",
                transcript = "先生、最近頭が痛いです。熱はありませんが、疲れやすいです。薬をください。",
                keyPhrases = listOf("頭が痛いです", "疲れやすい", "薬をください")
            ),
            ShadowListeningSession(
                id = "8",
                title = "Buying Train Tickets",
                difficulty = "Advanced",
                duration = "2:20",
                transcript = "新幹線のチケットをください。東京から大阪まで、明日の朝9時頃の便でお願いします。指定席でお願いします。",
                keyPhrases = listOf("新幹線のチケット", "東京から大阪", "指定席で")
            ),
            ShadowListeningSession(
                id = "9",
                title = "Introducing Family",
                difficulty = "Beginner",
                duration = "1:10",
                transcript = "私の家族を紹介します。父と母と妹がいます。妹は学生です。",
                keyPhrases = listOf("家族を紹介します", "父と母", "妹は学生")
            ),
            ShadowListeningSession(
                id = "10",
                title = "Talking About Weather",
                difficulty = "Intermediate",
                duration = "1:30",
                transcript = "今日の天気はどうですか？晴れていますが、少し寒いです。傘は要らないですね。",
                keyPhrases = listOf("今日の天気", "晴れています", "少し寒い")
            ),
            ShadowListeningSession(
                id = "11",
                title = "At the Airport",
                difficulty = "Advanced",
                duration = "2:30",
                transcript = "成田空港行きのリムジンバスはどこですか？12時発の便に乗ります。パスポートを準備してください。",
                keyPhrases = listOf("成田空港行き", "12時発の便", "パスポートを準備")
            ),
            ShadowListeningSession(
                id = "12",
                title = "Making a Phone Call",
                difficulty = "Intermediate",
                duration = "1:40",
                transcript = "もしもし、山田です。明日の会議についてお話ししたいです。午後3時はいかがですか？",
                keyPhrases = listOf("もしもし", "会議について", "午後3時はいかが")
            )
        )
    }
}

@Composable
fun ListenScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val viewModel = remember { ListenViewModel() }
    val sessions = remember { viewModel.getShadowListeningSessions() }
    var selectedSession by remember { mutableStateOf<ShadowListeningSession?>(null) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        if (selectedSession == null) {
            SessionSelectionScreen(
                sessions = sessions,
                onSessionSelected = { selectedSession = it },
                onBackClick = onBackClick
            )
        } else {
            ShadowListeningScreen(
                session = selectedSession!!,
                onBackClick = { selectedSession = null }
            )
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
            }
        }
    }
}

@Composable
fun ShadowListeningScreen(
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
                // Không sử dụng tts trực tiếp trong lambda, thay vào đó xử lý sau
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

    // Cleanup TTS on composable disposal
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
                tts.stop() // Stop TTS when going back
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

        // Audio visualization card (now for TTS)
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
                                text = "Listen to the Japanese text and shadow it in real-time",
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
                                        delay(3000) // Adjust delay based on transcript length
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

        // Transcript card
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Transcript",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = session.transcript,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Key Phrases",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

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
fun ListenScreenPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        ListenScreen()
    }
}