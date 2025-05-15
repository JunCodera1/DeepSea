package com.example.deepsea.ui.screens.feature.review

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.review.Story
import com.example.deepsea.data.repository.StoryRepository
import com.example.deepsea.ui.viewmodel.learn.StoriesState
import com.example.deepsea.ui.viewmodel.learn.StoryViewModel
import com.example.deepsea.ui.viewmodel.learn.StoryViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale

// DataStore setup
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bookmarks")
val BOOKMARK_KEY = stringSetPreferencesKey("bookmarked_stories")

@Composable
fun StoryScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    // Create StoryViewModel
    val storyRepository = StoryRepository(RetrofitClient.storyApiService)
    val viewModel: StoryViewModel = viewModel(
        factory = StoryViewModelFactory(storyRepository)
    )

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedStory by remember { mutableStateOf<Story?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val bookmarkedStories = remember { mutableStateOf(setOf<String>()) }
    val storiesState by remember { derivedStateOf { viewModel.storiesState } }

    // Load stories
    LaunchedEffect(Unit) {
        viewModel.loadStories()
    }

    // Load bookmarks from DataStore
    LaunchedEffect(Unit) {
        bookmarkedStories.value = context.dataStore.data
            .map { it[BOOKMARK_KEY] ?: emptySet() }
            .first()
    }

    Scaffold(
        topBar = {
            if (selectedStory == null) {
                StoryTopAppBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onBackClick = onBackClick
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val state = storiesState) {
                is StoriesState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is StoriesState.Error -> {
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
                                Icons.Filled.Clear,
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
                                onClick = { viewModel.loadStories() }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is StoriesState.Success -> {
                    val filteredStories = if (searchQuery.isEmpty()) {
                        state.stories
                    } else {
                        state.stories.filter {
                            it.title.contains(searchQuery, ignoreCase = true) ||
                                    it.level.contains(searchQuery, ignoreCase = true) ||
                                    it.content.contains(searchQuery, ignoreCase = true)
                        }
                    }

                    AnimatedVisibility(
                        visible = selectedStory == null,
                        enter = fadeIn() + slideInHorizontally(),
                        exit = fadeOut() + slideOutHorizontally()
                    ) {
                        StoryListScreen(
                            stories = filteredStories,
                            bookmarkedStories = bookmarkedStories.value,
                            onStoryClick = { selectedStory = it },
                            onBookmarkClick = { storyTitle ->
                                scope.launch {
                                    context.dataStore.edit { preferences ->
                                        val currentBookmarks = preferences[BOOKMARK_KEY] ?: emptySet()
                                        preferences[BOOKMARK_KEY] = if (storyTitle in currentBookmarks) {
                                            currentBookmarks - storyTitle
                                        } else {
                                            currentBookmarks + storyTitle
                                        }
                                        bookmarkedStories.value = preferences[BOOKMARK_KEY] ?: emptySet()
                                    }
                                }
                            },
                            modifier = modifier.fillMaxSize()
                        )
                    }

                    AnimatedVisibility(
                        visible = selectedStory != null,
                        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                        exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it })
                    ) {
                        selectedStory?.let { story ->
                            StoryDetailScreen(
                                story = story,
                                onBackClick = { selectedStory = null },
                                isBookmarked = story.title in bookmarkedStories.value,
                                onBookmarkClick = {
                                    scope.launch {
                                        context.dataStore.edit { preferences ->
                                            val currentBookmarks = preferences[BOOKMARK_KEY] ?: emptySet()
                                            preferences[BOOKMARK_KEY] = if (story.title in currentBookmarks) {
                                                currentBookmarks - story.title
                                            } else {
                                                currentBookmarks + story.title
                                            }
                                            bookmarkedStories.value = preferences[BOOKMARK_KEY] ?: emptySet()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryTopAppBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var isSearchActive by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (isSearchActive) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search stories") },
                    singleLine = true,
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "Japanese Stories",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = { isSearchActive = !isSearchActive }) {
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = if (isSearchActive) "Close search" else "Open search"
                )
            }
        }
    )
}

@Composable
fun StoryListScreen(
    stories: List<Story>,
    bookmarkedStories: Set<String>,
    onStoryClick: (Story) -> Unit,
    onBookmarkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        if (stories.isEmpty()) {
            EmptyStoryList()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(stories, key = { it.id }) { story ->
                    StoryItem(
                        story = story,
                        isBookmarked = story.title in bookmarkedStories,
                        onClick = { onStoryClick(story) },
                        onBookmarkClick = { onBookmarkClick(story.title) }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStoryList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No stories found",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Try adjusting your search or check back later for new stories",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StoryItem(
    story: Story,
    isBookmarked: Boolean,
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getLevelColor(story.level)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = story.level.first().toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = story.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = story.level,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        modifier = Modifier.semantics { contentDescription = "${story.level} difficulty" }
                    ) {
                        repeat(getLevelDifficulty(story.level)) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFB900),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = story.content.lines().first(),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.DarkGray
                )
            }

            IconButton(onClick = onBookmarkClick) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark",
                    tint = if (isBookmarked) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
    }
}

@Composable
fun StoryDetailScreen(
    story: Story,
    onBackClick: () -> Unit,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit
) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var ttsInitialized by remember { mutableStateOf(false) }

    // Initialize TTS
    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.JAPANESE)
                ttsInitialized = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }
        onDispose {
            tts?.stop()
            tts?.shutdown()
            tts = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    tts?.stop()
                    isPlaying = false
                    onBackClick()
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onBookmarkClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark",
                    tint = if (isBookmarked) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
            IconButton(
                onClick = {
                    if (ttsInitialized) {
                        if (isPlaying) {
                            tts?.stop()
                            isPlaying = false
                        } else {
                            tts?.speak(story.content, TextToSpeech.QUEUE_FLUSH, null, null)
                            isPlaying = true
                        }
                    }
                },
                enabled = ttsInitialized,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause story" else "Play story",
                    tint = if (ttsInitialized) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = story.title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = getLevelColor(story.level).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = story.level,
                            fontSize = 14.sp,
                            color = getLevelColor(story.level),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        modifier = Modifier.semantics { contentDescription = "${story.level} difficulty" }
                    ) {
                        repeat(getLevelDifficulty(story.level)) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFB900),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Use EnhancedFuriganaText with dynamic furigana
                EnhancedFuriganaText(
                    text = story.content,
                    furiganaData = processFuriganaForStory(story),
                    fontSize = 16,
                    modifier = Modifier.semantics { contentDescription = story.content }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { /* TODO: Navigate to PracticeScreen */ },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Practice",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
            OutlinedButton(
                onClick = {
                    tts?.stop()
                    isPlaying = false
                    onBackClick()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Back to Stories",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}

// Enhanced version with support for character-level furigana
@Composable
fun EnhancedFuriganaText(
    text: String,
    furiganaData: List<FuriganaMapping>,
    fontSize: Int = 16,
    modifier: Modifier = Modifier
) {
    val (annotatedString, inlineContent) = remember(text, furiganaData) {
        buildEnhancedFuriganaAnnotatedString(text, furiganaData)
    }

    Text(
        text = annotatedString,
        inlineContent = inlineContent,
        fontSize = fontSize.sp,
        lineHeight = (fontSize * 1.8).sp,
        modifier = modifier
    )
}

// FuriganaMapping with character positions
data class FuriganaMapping(
    val word: String,
    val reading: String,
    val startIndex: Int,
    val id: String = "furigana-$startIndex-$word"
)

// Build annotated string with position-aware furigana
private fun buildEnhancedFuriganaAnnotatedString(
    text: String,
    furiganaData: List<FuriganaMapping>
): Pair<AnnotatedString, Map<String, InlineTextContent>> {
    val inlineContentMap = mutableMapOf<String, InlineTextContent>()
    val sortedFurigana = furiganaData.sortedBy { it.startIndex }

    val builder = buildAnnotatedString {
        var currentPosition = 0

        for (furigana in sortedFurigana) {
            // Append regular text before furigana
            if (furigana.startIndex > currentPosition) {
                append(text.substring(currentPosition, furigana.startIndex))
            }

            // Add furigana content
            appendInlineContent(furigana.id, furigana.word)

            // Create inline content
            inlineContentMap[furigana.id] = InlineTextContent(
                placeholder = Placeholder(
                    width = furigana.word.length.toFloat().sp,
                    height = 24.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Bottom
                )
            ) {
                FuriganaLayout(kanji = furigana.word, reading = furigana.reading)
            }

            currentPosition = furigana.startIndex + furigana.word.length
        }

        // Append remaining text
        if (currentPosition < text.length) {
            append(text.substring(currentPosition))
        }
    }

    return builder to inlineContentMap
}

// Helper function to process the story and generate FuriganaMapping with position info
fun processFuriganaForStory(story: Story): List<FuriganaMapping> {
    val furiganaList = furiganaMap[story.title] ?: emptyList()
    val result = mutableListOf<FuriganaMapping>()
    val text = story.content

    for (furigana in furiganaList) {
        var startSearchIndex = 0
        while (true) {
            val foundIndex = text.indexOf(furigana.kanji, startSearchIndex)
            if (foundIndex == -1) break

            result.add(
                FuriganaMapping(
                    word = furigana.kanji,
                    reading = furigana.reading,
                    startIndex = foundIndex,
                    id = "furigana-$foundIndex-${furigana.kanji}"
                )
            )

            startSearchIndex = foundIndex + furigana.kanji.length
        }
    }

    return result.sortedBy { it.startIndex }
}

// Helper functions
fun getLevelColor(level: String): Color {
    return when (level.lowercase()) {
        "beginner" -> Color(0xFF4CAF50)
        "intermediate" -> Color(0xFF2196F3)
        "advanced" -> Color(0xFFF44336)
        else -> Color(0xFF9C27B0)
    }
}

fun getLevelDifficulty(level: String): Int {
    return when (level.lowercase()) {
        "beginner" -> 1
        "intermediate" -> 2
        "advanced" -> 3
        else -> 1
    }
}

// Furigana data
val furiganaMap = mapOf(
    "猫と犬" to listOf(
        Furigana("猫", "ねこ"),
        Furigana("犬", "いぬ"),
        Furigana("公園", "こうえん"),
        Furigana("会いました", "あいました"),
        Furigana("言いました", "いいました"),
        Furigana("元気", "げんき"),
        Furigana("答えました", "こたえました"),
        Furigana("笑いました", "わらいました"),
        Furigana("一緒に", "いっしょに"),
        Furigana("遊ぼう", "あそぼう"),
        Furigana("楽しく", "たのしく"),
        Furigana("遊びました", "あそびました"),
        Furigana("友達", "ともだち"),
        Furigana("簡単", "かんたん")
    ),
    "山の冒険" to listOf(
        Furigana("太郎", "たろう"),
        Furigana("山", "やま"),
        Furigana("登る", "のぼる"),
        Furigana("大好き", "だいすき"),
        Furigana("晴れた", "はれた"),
        Furigana("日", "ひ"),
        Furigana("挑戦", "ちょうせん"),
        Furigana("道", "みち"),
        Furigana("険しかった", "けわしかった"),
        Furigana("頑張りました", "がんばりました"),
        Furigana("頂上", "ちょうじょう"),
        Furigana("着くと", "つくと"),
        Furigana("美しい", "うつくしい"),
        Furigana("景色", "けしき"),
        Furigana("見えました", "みえました"),
        Furigana("叫びました", "さけびました"),
        Furigana("写真", "しゃしん"),
        Furigana("撮って", "とって"),
        Furigana("友達", "ともだち"),
        Furigana("送りました", "おくりました"),
        Furigana("自然", "しぜん"),
        Furigana("美しさ", "うつくしさ"),
        Furigana("感じました", "かんじました")
    ),
    "未来の町" to listOf(
        Furigana("町", "まち"),
        Furigana("変わりました", "かわりました"),
        Furigana("空飛ぶ", "そらとぶ"),
        Furigana("車", "くるま"),
        Furigana("道", "みち"),
        Furigana("走り", "はしり"),
        Furigana("ロボット", "ろぼっと"),
        Furigana("人", "ひと"),
        Furigana("助けます", "たすけます"),
        Furigana("日", "ひ"),
        Furigana("電力", "でんりょく"),
        Furigana("止まりました", "とまりました"),
        Furigana("人々", "ひとびと"),
        Furigana("パニック", "ぱにっく"),
        Furigana("若い", "わかい"),
        Furigana("エンジニア", "えんじにあ"),
        Furigana("花子", "はなこ"),
        Furigana("原因", "げんいん"),
        Furigana("見つける", "みつける"),
        Furigana("働きました", "はたらきました"),
        Furigana("古い", "ふるい"),
        Furigana("システム", "しすてむ"),
        Furigana("バグ", "ばぐ"),
        Furigana("見つけ", "みつけ"),
        Furigana("直しました", "なおしました"),
        Furigana("明るくなり", "あかるくなり"),
        Furigana("ヒーロー", "ひーろー")
    ),
    "四季の美しさ" to listOf(
        Furigana("日本", "にほん"),
        Furigana("四つ", "よっつ"),
        Furigana("季節", "きせつ"),
        Furigana("春", "はる"),
        Furigana("夏", "なつ"),
        Furigana("秋", "あき"),
        Furigana("冬", "ふゆ"),
        Furigana("桜", "さくら"),
        Furigana("咲きます", "さきます"),
        Furigana("人々", "ひとびと"),
        Furigana("花見", "はなみ"),
        Furigana("楽しみます", "たのしみます"),
        Furigana("海", "うみ"),
        Furigana("山", "やま"),
        Furigana("行きます", "いきます"),
        Furigana("花火", "はなび"),
        Furigana("大会", "たいかい"),
        Furigana("紅葉", "こうよう"),
        Furigana("きれい", "きれい"),
        Furigana("食べ物", "たべもの"),
        Furigana("おいしい", "おいしい"),
        Furigana("雪", "ゆき"),
        Furigana("降る", "ふる"),
        Furigana("地域", "ちいき"),
        Furigana("お正月", "おしょうがつ"),
        Furigana("特別", "とくべつ"),
        Furigana("時期", "じき"),
        Furigana("四季", "しき"),
        Furigana("それぞれ", "それぞれ"),
        Furigana("美しさ", "うつくしさ")
    ),
    "友情の意味" to listOf(
        Furigana("健太", "けんた"),
        Furigana("直樹", "なおき"),
        Furigana("幼い", "おさない"),
        Furigana("頃", "ころ"),
        Furigana("友達", "ともだち"),
        Furigana("学校", "がっこう"),
        Furigana("通い", "かよい"),
        Furigana("常に", "つねに"),
        Furigana("一緒に", "いっしょに"),
        Furigana("高校生", "こうこうせい"),
        Furigana("違う", "ちがう"),
        Furigana("道", "みち"),
        Furigana("歩き", "あるき"),
        Furigana("始めました", "はじめました"),
        Furigana("勉強", "べんきょう"),
        Furigana("集中", "しゅうちゅう"),
        Furigana("スポーツ", "すぽーつ"),
        Furigana("夢中", "むちゅう"),
        Furigana("日", "ひ"),
        Furigana("大きな", "おおきな"),
        Furigana("試合", "しあい"),
        Furigana("怪我", "けが"),
        Furigana("誰も", "だれも"),
        Furigana("助ける", "たすける"),
        Furigana("毎日", "まいにち"),
        Furigana("病院", "びょういん"),
        Furigana("励ましました", "はげましました"),
        Furigana("尋ねると", "たずねると"),
        Furigana("答えました", "こたえました"),
        Furigana("距離", "きょり"),
        Furigana("友情", "ゆうじょう"),
        Furigana("変わらない", "かわらない"),
        Furigana("瞬間", "しゅんかん"),
        Furigana("真", "しん"),
        Furigana("理解", "りかい"),
        Furigana("時間", "じかん"),
        Furigana("関係", "かんけい"),
        Furigana("本当", "ほんとう"),
        Furigana("心", "こころ")
    )
)

data class Furigana(val kanji: String, val reading: String)

@Composable
private fun FuriganaLayout(kanji: String, reading: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = reading,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.offset(y = (-2).dp)
        )
        Text(
            text = kanji,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StoryScreenPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF5F5F5)
        ) {
            StoryScreen()
        }
    }
}