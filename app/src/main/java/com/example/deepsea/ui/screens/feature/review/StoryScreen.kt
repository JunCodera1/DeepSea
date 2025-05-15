package com.example.deepsea.ui.screens.feature.review

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// DataStore setup
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bookmarks")
val BOOKMARK_KEY = stringSetPreferencesKey("bookmarked_stories")

@Composable
fun StoryScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedStory by remember { mutableStateOf<Story?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var filteredStories by remember { mutableStateOf(stories) }
    val bookmarkedStories = remember { mutableStateOf(setOf<String>()) }

    // Load bookmarks from DataStore
    LaunchedEffect(Unit) {
        bookmarkedStories.value = context.dataStore.data
            .map { it[BOOKMARK_KEY] ?: emptySet() }
            .first()
    }

    // Filter stories based on search query
    LaunchedEffect(searchQuery) {
        filteredStories = if (searchQuery.isEmpty()) {
            stories
        } else {
            stories.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.level.contains(searchQuery, ignoreCase = true) ||
                        it.content.contains(searchQuery, ignoreCase = true)
            }
        }
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
                items(stories) { story ->
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
    val annotatedText = remember(story) { buildFuriganaAnnotatedString(story) }

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
                onClick = onBackClick,
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
                Text(
                    text = annotatedText,
                    fontSize = 16.sp,
                    lineHeight = 28.sp,
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
                onClick = onBackClick,
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

// Data classes
data class Story(
    val title: String,
    val level: String,
    val content: String
)

data class Furigana(val kanji: String, val reading: String)

// Furigana rendering
fun buildFuriganaAnnotatedString(story: Story): AnnotatedString {
    val builder = AnnotatedString.Builder()
    val text = story.content
    val furiganaList = furiganaMap[story.title] ?: emptyList()

    var currentIndex = 0
    while (currentIndex < text.length) {
        val matchingFurigana = furiganaList
            .filter { text.startsWith(it.kanji, currentIndex) }
            .maxByOrNull { it.kanji.length }

        if (matchingFurigana != null) {
            builder.append(matchingFurigana.kanji)
            builder.addStyle(
                SpanStyle(fontSize = 10.sp, baselineShift = BaselineShift.Superscript),
                currentIndex,
                currentIndex + matchingFurigana.kanji.length
            )
            builder.append("(${matchingFurigana.reading})")
            currentIndex += matchingFurigana.kanji.length
        } else {
            builder.append(text[currentIndex].toString())
            currentIndex++
        }
    }
    return builder.toAnnotatedString()
}

// Sample stories
val stories = listOf(
    Story(
        title = "猫と犬",
        level = "Beginner",
        content = """
            ある日、猫と犬が公園で会いました。
            猫は言いました。「こんにちは、犬さん！元気？」
            犬は答えました。「うん、元気だよ！猫さんは？」
            猫は笑いました。「私も元気！一緒に遊ぼう！」
            それから、猫と犬は楽しく遊びました。
            友達になるのは、とても簡単でした。
        """.trimIndent()
    ),
    Story(
        title = "山の冒険",
        level = "Intermediate",
        content = """
            太郎は山に登るのが大好きです。ある晴れた日、彼は新しい山に挑戦しました。
            道は険しかったですが、太郎は頑張りました。頂上に着くと、美しい景色が見えました。
            「すごい！」太郎は叫びました。彼は写真を撮って、友達に送りました。
            その日、太郎は自然の美しさを感じました。
        """.trimIndent()
    ),
    Story(
        title = "未来の町",
        level = "Advanced",
        content = """
            2050年、町はとても変わりました。空飛ぶ車が道を走り、ロボットが人を助けます。
            しかし、ある日、町の電力が止まりました。人々はパニックになりました。
            若いエンジニアの花子は、原因を見つけるために働きました。彼女は古いシステムのバグを見つけ、すぐに直しました。
            町はまた明るくなり、花子はヒーローになりました。
        """.trimIndent()
    ),
    Story(
        title = "四季の美しさ",
        level = "Intermediate",
        content = """
            日本には四つの季節があります。春、夏、秋、冬です。
            春には、桜が咲きます。人々は花見を楽しみます。
            夏には、海や山に行きます。花火大会もあります。
            秋には、紅葉がとてもきれいです。食べ物もおいしいです。
            冬 frying panには、雪が降る地域もあります。お正月は特別な時期です。
            日本の四季は、それぞれ違う美しさがあります。
        """.trimIndent()
    ),
    Story(
        title = "友情の意味",
        level = "Advanced",
        content = """
            健太と直樹は幼い頃からの友達でした。彼らは同じ学校に通い、常に一緒にいました。
            しかし、高校生になると、二人は違う道を歩き始めました。健太は勉強に集中し、直樹はスポーツに夢中になりました。
            ある日、直樹が大きな試合で怪我をしました。誰も助けることができませんでしたが、健太は毎日病院に通い、直樹を励ましました。
            「なぜここまでしてくれるの？」と直樹が尋ねると、健太は答えました。「友達だからさ。距離があっても、友情は変わらないよ。」
            その瞬間、二人は真の友情の意味を理解しました。時間や距離に関係なく、本当の友達はいつも心の中にいるのです。
        """.trimIndent()
    )
)

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