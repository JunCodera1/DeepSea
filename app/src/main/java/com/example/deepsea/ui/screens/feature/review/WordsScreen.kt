package com.example.deepsea.ui.screens.feature.review

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.review.Word
import com.example.deepsea.data.repository.WordRepository
import com.example.deepsea.ui.viewmodel.learn.WordViewModel
import com.example.deepsea.ui.viewmodel.learn.WordsState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// DataStore setup
val Context.wordDataStore: DataStore<Preferences> by preferencesDataStore(name = "word_progress")
val Context.favoriteWordsStore: DataStore<Preferences> by preferencesDataStore(name = "favorite_words")


enum class WordStatus { NEW, LEARNING, MASTERED }

enum class GameType { MATCHING, FLASHCARD }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordsScreen(
    viewModel: WordViewModel,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    var filterTheme by remember { mutableStateOf("All") }
    var filterLevel by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }
    var showGameDialog by remember { mutableStateOf(false) }
    var selectedGameType by remember { mutableStateOf<GameType?>(null) }
    var expandedWordId by remember { mutableStateOf<String?>(null) }

    val wordProgress = remember { mutableStateMapOf<String, WordStatus>() }
    val favoriteWords = remember { mutableStateMapOf<String, Boolean>() }
    val wordsState by remember { derivedStateOf { viewModel.wordsState } }

    // Load word progress and favorites from DataStore
    LaunchedEffect(wordsState) {
        if (wordsState is WordsState.Success) {
            val words = (wordsState as WordsState.Success).words
            words.forEach { word ->
                // Ensure word.id is not null before using it as a preference key
                if (word.id != null && word.id.isNotEmpty()) {
                    val status = context.wordDataStore.data
                        .map { it[stringPreferencesKey(word.id)] ?: WordStatus.NEW.name }
                        .first()
                    wordProgress[word.id] = WordStatus.valueOf(status)

                    val isFavorite = context.favoriteWordsStore.data
                        .map { it[stringPreferencesKey(word.id)] == "true" }
                        .first()
                    favoriteWords[word.id] = isFavorite
                } else {
                    // Generate a fallback ID for words with null IDs
                    val fallbackId = word.text?.hashCode()?.toString() ?: "fallback_${words.indexOf(word)}"
                    wordProgress[fallbackId] = WordStatus.NEW
                    favoriteWords[fallbackId] = false
                }
            }
        }
    }

    // Load words when theme changes
    LaunchedEffect(filterTheme) {
        viewModel.loadWords(filterTheme)
    }

    // Filter words by level and search query locally
    val filteredWords = when (val state = wordsState) {
        is WordsState.Success -> state.words.filter { word ->
            (filterLevel == "All" || word.level == filterLevel) &&
                    (searchQuery.isEmpty() ||
                            word.text.contains(searchQuery, ignoreCase = true) ||
                            word.reading.contains(searchQuery, ignoreCase = true) ||
                            word.meaning.contains(searchQuery, ignoreCase = true))
        }
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            "Japanese Vocabulary",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.semantics {
                                contentDescription = "Navigate back"
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showSearchBar = !showSearchBar },
                            modifier = Modifier.semantics {
                                contentDescription = "Toggle search"
                            }
                        ) {
                            Icon(Icons.Filled.Search, contentDescription = null)
                        }

                        IconButton(
                            onClick = {
                                selectedGameType = GameType.MATCHING
                                showGameDialog = true
                            },
                            modifier = Modifier.semantics {
                                contentDescription = "Start word game"
                            }
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null)
                        }
                    }
                )

                AnimatedVisibility(
                    visible = showSearchBar,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = { /* Nothing extra needed */ },
                        active = showSearchBar,
                        onActiveChange = { showSearchBar = it },
                        placeholder = { Text("Search words...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        // Search suggestions could go here
                    }
                }

                // Filter chips for themes
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = filterTheme == "All",
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                filterTheme = "All"
                            },
                            label = { Text("All") },
                            modifier = Modifier.semantics {
                                contentDescription = "Filter by All themes"
                            }
                        )
                    }

                    val themes = (wordsState as? WordsState.Success)?.words?.map { it.theme }?.distinct() ?: emptyList()
                    items(themes) { theme ->
                        FilterChip(
                            selected = filterTheme == theme,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                filterTheme = theme
                            },
                            label = { Text(theme) },
                            modifier = Modifier.semantics {
                                contentDescription = "Filter by $theme theme"
                            }
                        )
                    }
                }

                // Filter chips for levels
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = filterLevel == "All",
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                filterLevel = "All"
                            },
                            label = { Text("All Levels") },
                            modifier = Modifier.semantics {
                                contentDescription = "Filter by All levels"
                            }
                        )
                    }

                    val levels = (wordsState as? WordsState.Success)?.words?.map { it.level }?.distinct() ?: emptyList()
                    items(levels) { level ->
                        FilterChip(
                            selected = filterLevel == level,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                filterLevel = level
                            },
                            label = { Text(level) },
                            modifier = Modifier.semantics {
                                contentDescription = "Filter by $level level"
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF9F9F9))
        ) {
            // Word statistics
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WordStatistic(
                        count = wordProgress.count { it.value == WordStatus.NEW },
                        label = "New",
                        color = Color(0xFF9E9E9E)
                    )

                    WordStatistic(
                        count = wordProgress.count { it.value == WordStatus.LEARNING },
                        label = "Learning",
                        color = Color(0xFFFFB300)
                    )

                    WordStatistic(
                        count = wordProgress.count { it.value == WordStatus.MASTERED },
                        label = "Mastered",
                        color = Color(0xFF4CAF50)
                    )
                }
            }

            // Word list
            when (val state = wordsState) {
                is WordsState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is WordsState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Filled.Info,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Text(
                                text = "Error: ${state.message}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = Color.Red
                            )
                            Button(
                                onClick = { viewModel.loadWords(filterTheme) }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is WordsState.Success -> {
                    if (filteredWords.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.Gray
                                )
                                Text(
                                    text = "No words found with the current filters",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray
                                )
                                Button(
                                    onClick = {
                                        filterTheme = "All"
                                        filterLevel = "All"
                                        searchQuery = ""
                                    }
                                ) {
                                    Text("Clear Filters")
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredWords, key = { it.id }) { word ->
                                WordItem(
                                    word = word,
                                    status = wordProgress[word.id] ?: WordStatus.NEW,
                                    isFavorite = favoriteWords[word.id] ?: false,
                                    isExpanded = expandedWordId == word.id,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        if (expandedWordId == word.id) {
                                            expandedWordId = null
                                        } else {
                                            expandedWordId = word.id
                                        }
                                    },
                                    onStatusChange = {
                                        scope.launch {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            context.wordDataStore.edit { preferences ->
                                                val currentStatus = wordProgress[word.id] ?: WordStatus.NEW
                                                val nextStatus = when (currentStatus) {
                                                    WordStatus.NEW -> WordStatus.LEARNING
                                                    WordStatus.LEARNING -> WordStatus.MASTERED
                                                    WordStatus.MASTERED -> WordStatus.NEW
                                                }
                                                preferences[stringPreferencesKey(word.id)] = nextStatus.name
                                                wordProgress[word.id] = nextStatus
                                            }
                                        }
                                    },
                                    onToggleFavorite = {
                                        scope.launch {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            val newValue = !(favoriteWords[word.id] ?: false)
                                            context.favoriteWordsStore.edit { preferences ->
                                                preferences[stringPreferencesKey(word.id)] = newValue.toString()
                                            }
                                            favoriteWords[word.id] = newValue
                                        }
                                    },
                                    onPlayGame = {
                                        selectedGameType = GameType.FLASHCARD
                                        showGameDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Game dialog
        if (showGameDialog && selectedGameType != null && wordsState is WordsState.Success) {
            when (selectedGameType) {
                GameType.MATCHING -> {
                    WordMatchGameDialog(
                        words = filteredWords.take(6),
                        onDismiss = { showGameDialog = false }
                    )
                }
                GameType.FLASHCARD -> {
                    FlashcardGameDialog(
                        words = filteredWords.take(10),
                        onDismiss = { showGameDialog = false }
                    )
                }
                else -> {}
            }
        }
    }
}

// Rest of the code (WordStatistic, WordItem, WordMatchGameDialog, FlashcardGameDialog) remains unchanged
@Composable
fun WordStatistic(
    count: Int,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
                .border(2.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.DarkGray
        )
    }
}

@Composable
fun WordItem(
    word: Word,
    status: WordStatus,
    isFavorite: Boolean,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onStatusChange: () -> Unit,
    onToggleFavorite: () -> Unit,
    onPlayGame: () -> Unit
) {
    val scaleAnim by animateFloatAsState(
        targetValue = if (status == WordStatus.MASTERED) 1.05f else 1f,
        animationSpec = tween(300),
        label = "WordScaleAnimation"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scaleAnim)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = when (status) {
            WordStatus.MASTERED -> Color(0xFFE8F5E9)
            else -> Color.White
        },
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when (status) {
                                WordStatus.NEW -> Color(0xFF9E9E9E)
                                WordStatus.LEARNING -> Color(0xFFFFB300)
                                WordStatus.MASTERED -> Color(0xFF4CAF50)
                            }
                        )
                        .clickable { onStatusChange() }
                        .semantics {
                            contentDescription = "Change word status, currently ${status.name}"
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (status == WordStatus.MASTERED) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White
                        )
                    } else {
                        Text(
                            text = when (status) {
                                WordStatus.NEW -> "N"
                                WordStatus.LEARNING -> "L"
                                WordStatus.MASTERED -> "M"
                            },
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = word.text,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${word.reading} [${word.pronunciation}]",
                        fontSize = 16.sp,
                        color = Color(0xFF5D4037),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = word.meaning,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF006064),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.semantics {
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites"
                        }
                    ) {
                        if (isFavorite) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107)
                            )
                        } else {
                            Icon(
                                Icons.Outlined.Star,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    }

                    IconButton(
                        onClick = onPlayGame,
                        modifier = Modifier.semantics {
                            contentDescription = "Practice with flashcards"
                        }
                    ) {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Level",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = word.level,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Theme",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = word.theme,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Story",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = word.storyTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Example Sentence",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = word.exampleSentence,
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Context",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = word.context,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WordMatchGameDialog(
    words: List<Word>,
    onDismiss: () -> Unit
) {
    var wordsToMatch by remember { mutableStateOf(words.take(6)) }
    var targetWords by remember { mutableStateOf(wordsToMatch.shuffled()) }
    var selectedWord by remember { mutableStateOf<Word?>(null) }
    var selectedTarget by remember { mutableStateOf<Word?>(null) }
    var matches by remember { mutableStateOf(mapOf<String, String>()) }
    var score by remember { mutableStateOf(0) }
    val haptic = LocalHapticFeedback.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Match Words to Meanings",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Score: $score / ${wordsToMatch.size}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Text("Japanese Words:", fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    wordsToMatch.forEach { word ->
                        val isMatched = matches[word.text] != null
                        val isSelected = selectedWord == word

                        Card(
                            modifier = Modifier
                                .size(80.dp)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(enabled = !isMatched) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedWord = if (isSelected) null else word
                                    if (selectedTarget != null && !isSelected) {
                                        if (selectedWord?.text == selectedTarget?.text) {
                                            matches = matches + (selectedWord!!.text to selectedTarget!!.meaning)
                                            score++
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                        selectedWord = null
                                        selectedTarget = null
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isMatched -> Color(0xFF4CAF50)
                                    isSelected -> Color(0xFF2196F3).copy(alpha = 0.7f)
                                    else -> Color(0xFFE0E0E0)
                                }
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = word.text,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMatched) Color.White else Color.Black,
                                    modifier = Modifier.semantics {
                                        contentDescription = "Word ${word.text}"
                                    }
                                )
                            }
                        }
                    }
                }

                Text("Meanings:", fontWeight = FontWeight.Medium)
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    targetWords.forEach { word ->
                        val isMatched = matches.values.contains(word.meaning)
                        val isSelected = selectedTarget == word

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(enabled = !isMatched) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedTarget = if (isSelected) null else word
                                    if (selectedWord != null && !isSelected) {
                                        if (selectedWord?.text == selectedTarget?.text) {
                                            matches = matches + (selectedWord!!.text to selectedTarget!!.meaning)
                                            score++
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                        selectedWord = null
                                        selectedTarget = null
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isMatched -> Color(0xFF4CAF50)
                                    isSelected -> Color(0xFF2196F3).copy(alpha = 0.7f)
                                    else -> Color(0xFFF5F5F5)
                                }
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = word.meaning,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isMatched) Color.White else Color.Black,
                                    modifier = Modifier.semantics {
                                        contentDescription = "Meaning ${word.meaning}"
                                    }
                                )
                            }
                        }
                    }
                }

                if (score == wordsToMatch.size) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Congratulations! All matches completed!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            wordsToMatch = words.take(6)
                            targetWords = wordsToMatch.shuffled()
                            selectedWord = null
                            selectedTarget = null
                            matches = mapOf()
                            score = 0
                        },
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9E9E9E)
                        )
                    ) {
                        Text("Reset")
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).padding(start = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
fun FlashcardGameDialog(
    words: List<Word>,
    onDismiss: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(0) }
    var showFront by remember { mutableStateOf(true) }
    var knownWords by remember { mutableStateOf(0) }
    val haptic = LocalHapticFeedback.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Flashcards",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Card ${currentIndex + 1} of ${words.size}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                LinearProgressIndicator(
                    progress = (currentIndex + 1) / words.size.toFloat(),
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            showFront = !showFront
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (showFront) Color(0xFFE3F2FD) else Color(0xFFE8F5E9)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (showFront) {
                                Text(
                                    text = words[currentIndex].text,
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = words[currentIndex].reading,
                                    fontSize = 24.sp,
                                    color = Color(0xFF5D4037)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "Tap to reveal meaning",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    fontStyle = FontStyle.Italic
                                )
                            } else {
                                Text(
                                    text = words[currentIndex].meaning,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF006064)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = words[currentIndex].exampleSentence,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center,
                                    fontStyle = FontStyle.Italic
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "Tap to see word",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            if (currentIndex > 0) {
                                currentIndex--
                                showFront = true
                            }
                        },
                        enabled = currentIndex > 0,
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9E9E9E),
                            disabledContainerColor = Color(0xFFE0E0E0)
                        )
                    ) {
                        Text("Previous")
                    }

                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            if (currentIndex < words.size - 1) {
                                currentIndex++
                                showFront = true
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text(if (currentIndex < words.size - 1) "Next" else "Finish")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (currentIndex < words.size - 1) {
                                currentIndex++
                                showFront = true
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFD32F2F)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFD32F2F))
                    ) {
                        Text("Don't Know")
                    }

                    OutlinedButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            knownWords++
                            if (currentIndex < words.size - 1) {
                                currentIndex++
                                showFront = true
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f).padding(start = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF4CAF50)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF4CAF50))
                    ) {
                        Text("Know")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WordsScreenPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF5F5F5)
        ) {
            val repository = WordRepository(RetrofitClient.wordApiService)
            val viewModel = WordViewModel(repository)
            WordsScreen(viewModel = viewModel)
        }
    }
}