package com.example.deepsea.ui.screens.feature.learn

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.R
import com.example.deepsea.data.dto.KeyPhraseDto
import com.example.deepsea.data.dto.TipDto
import com.example.deepsea.data.dto.UnitGuideDto
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitGuideBookScreen(
    guideData: UnitGuideDto,
    onBack: () -> Unit,
    onPlayAudio: (String) -> Unit
) {
    // Initialize TextToSpeech
    val context = LocalContext.current
    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsInitialized by remember { mutableStateOf(false) }

    // Set up TextToSpeech
    DisposableEffect(Unit) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.JAPAN // Set to Japanese for key phrases
                isTtsInitialized = true
            }
        }

        onDispose {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
            isTtsInitialized = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = guideData.title,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "Close"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Unit Description
            item {
                Text(
                    text = guideData.description,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // Key Phrases Section
            item {
                Text(
                    text = "KEY PHRASES",
                    color = Color(0xFF31C5F9),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (guideData.keyPhrases.isNotEmpty()) {
                    Text(
                        text = "Describe belongings",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }

            // Key Phrases List
            items(guideData.keyPhrases) { phrase ->
                KeyPhraseItem(
                    phrase = phrase,
                    onPlayAudio = onPlayAudio,
                    onPlayTts = { text ->
                        if (isTtsInitialized) {
                            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                    }
                )
            }

            // Tips Section
            if (guideData.tips.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE3F8FF))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "TIP",
                                color = Color(0xFF31C5F9),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            guideData.tips.forEach { tip ->
                                TipItem(tip = tip)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KeyPhraseItem(
    phrase: KeyPhraseDto,
    onPlayAudio: (String) -> Unit,
    onPlayTts: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (phrase.audioUrl != null) {
                        onPlayAudio(phrase.audioUrl)
                    } else if (phrase.originalText.isNotBlank()) {
                        onPlayTts(phrase.originalText)
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_audio),
                    contentDescription = "Play audio",
                    tint = Color(0xFF31C5F9)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = phrase.originalText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = phrase.translatedText,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun TipItem(tip: TipDto) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = tip.title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.DarkGray
        )

        Text(
            text = tip.content,
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}