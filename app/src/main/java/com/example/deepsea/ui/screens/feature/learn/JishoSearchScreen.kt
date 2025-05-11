package com.example.deepsea.ui.screens.feature.learn

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.random.Random

@Composable
fun JapaneseCharacterLearningScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Hiragana", "Katakana", "Basic Kanji", "Dictionary")
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedCharacter by remember { mutableStateOf<JapaneseCharacter?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Japanese Learning Center",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTabIndex) {
            0 -> HiraganaGrid { character ->
                selectedCharacter = character
                showDetailDialog = true
            }
            1 -> KatakanaGrid { character ->
                selectedCharacter = character
                showDetailDialog = true
            }
            2 -> BasicKanjiGrid { character ->
                selectedCharacter = character
                showDetailDialog = true
            }
            3 -> JishoDictionaryLookup()
        }
    }

    if (showDetailDialog && selectedCharacter != null) {
        CharacterDetailDialog(
            character = selectedCharacter!!,
            onDismiss = { showDetailDialog = false }
        )
    }
}

@Composable
fun HiraganaGrid(onCharacterClick: (JapaneseCharacter) -> Unit) {
    val hiraganaChars = listOf(
        listOf("あ" to "a", "い" to "i", "う" to "u", "え" to "e", "お" to "o"),
        listOf("か" to "ka", "き" to "ki", "く" to "ku", "け" to "ke", "こ" to "ko"),
        listOf("さ" to "sa", "し" to "shi", "す" to "su", "せ" to "se", "そ" to "so"),
        listOf("た" to "ta", "ち" to "chi", "つ" to "tsu", "て" to "te", "と" to "to"),
        listOf("な" to "na", "に" to "ni", "ぬ" to "nu", "ね" to "ne", "の" to "no"),
        listOf("は" to "ha", "ひ" to "hi", "ふ" to "fu", "へ" to "he", "ほ" to "ho"),
        listOf("ま" to "ma", "み" to "mi", "む" to "mu", "め" to "me", "も" to "mo"),
        listOf("や" to "ya", "ゆ" to "yu", "よ" to "yo"),
        listOf("ら" to "ra", "り" to "ri", "る" to "ru", "れ" to "re", "ろ" to "ro"),
        listOf("わ" to "wa", "を" to "wo", "ん" to "n")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        hiraganaChars.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { (char, romanization) ->
                    CharacterCard(
                        character = JapaneseCharacter(
                            char = char,
                            romanization = romanization,
                            type = "Hiragana",
                            examples = listOf(
                                "あさ (asa) - morning",
                                "あき (aki) - autumn"
                            ).filter { it.contains(char) }.takeIf { it.isNotEmpty() } ?: listOf("No examples available")
                        ),
                        onClick = onCharacterClick
                    )
                }
                repeat(5 - row.size) {
                    Spacer(modifier = Modifier.width(60.dp))
                }
            }
        }
    }
}

@Composable
fun KatakanaGrid(onCharacterClick: (JapaneseCharacter) -> Unit) {
    val katakanaChars = listOf(
        listOf("ア" to "a", "イ" to "i", "ウ" to "u", "エ" to "e", "オ" to "o"),
        listOf("カ" to "ka", "キ" to "ki", "ク" to "ku", "ケ" to "ke", "コ" to "ko"),
        listOf("サ" to "sa", "シ" to "shi", "ス" to "su", "セ" to "se", "ソ" to "so"),
        listOf("タ" to "ta", "チ" to "chi", "ツ" to "tsu", "テ" to "te", "ト" to "to"),
        listOf("ナ" to "na", "ニ" to "ni", "ヌ" to "nu", "ネ" to "ne", "ノ" to "no"),
        listOf("ハ" to "ha", "ヒ" to "hi", "フ" to "fu", "ヘ" to "he", "ホ" to "ho"),
        listOf("マ" to "ma", "ミ" to "mi", "ム" to "mu", "メ" to "me", "モ" to "mo"),
        listOf("ヤ" to "ya", "ユ" to "yu", "ヨ" to "yo"),
        listOf("ラ" to "ra", "リ" to "ri", "ル" to "ru", "レ" to "re", "ロ" to "ro"),
        listOf("ワ" to "wa", "ヲ" to "wo", "ン" to "n")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        katakanaChars.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { (char, romanization) ->
                    CharacterCard(
                        character = JapaneseCharacter(
                            char = char,
                            romanization = romanization,
                            type = "Katakana",
                            examples = listOf(
                                "カメラ (kamera) - camera",
                                "コーヒー (kōhī) - coffee"
                            ).filter { it.contains(char) }.takeIf { it.isNotEmpty() } ?: listOf("No examples available")
                        ),
                        onClick = onCharacterClick
                    )
                }
                repeat(5 - row.size) {
                    Spacer(modifier = Modifier.width(60.dp))
                }
            }
        }
    }
}

@Composable
fun BasicKanjiGrid(onCharacterClick: (JapaneseCharacter) -> Unit) {
    var kanjiList by remember { mutableStateOf<List<JapaneseCharacter>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Load kanji on first composition
    fun loadKanji() {
        isLoading = true
        fetchRandomKanji { kanji ->
            kanjiList = kanji
            isLoading = false
        }
    }

    // Initial load
    remember { loadKanji() }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { loadKanji() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Reload Random Kanji")
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading Kanji...")
            }
        } else if (kanjiList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No Kanji Found")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(kanjiList) { kanji ->
                    KanjiCard(kanji, onCharacterClick)
                }
            }
        }
    }
}

fun fetchRandomKanji(onResult: (List<JapaneseCharacter>) -> Unit) {
    val client = OkHttpClient()
    // Use a random JLPT level (N5 to N1) or common kanji tag
    val tags = listOf("jlpt-n5", "jlpt-n4", "jlpt-n3", "jlpt-n2", "jlpt-n1")
    val randomTag = tags[Random.nextInt(tags.size)]
    val url = "https://jisho.org/api/v1/search/words?keyword=%23$randomTag"
    val request = Request.Builder().url(url).build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Handler(Looper.getMainLooper()).post {
                onResult(emptyList())
            }
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { body ->
                try {
                    val json = JSONObject(body)
                    val data = json.getJSONArray("data")
                    val kanjiList = mutableListOf<JapaneseCharacter>()

                    // Shuffle indices and limit to 24 kanji
                    val indices = (0 until minOf(data.length(), 100)).shuffled()
                    val maxKanji = minOf(indices.size, 24)

                    for (i in 0 until maxKanji) {
                        val entry = data.getJSONObject(indices[i])
                        val japanese = entry.getJSONArray("japanese")
                        if (japanese.length() > 0) {
                            val japaneseObj = japanese.getJSONObject(0)
                            val word = if (japaneseObj.has("word")) japaneseObj.getString("word") else ""
                            val reading = if (japaneseObj.has("reading")) japaneseObj.getString("reading") else ""

                            // Filter for single kanji characters
                            if (word.length == 1 && word.matches(Regex("\\p{InCJKUnifiedIdeographs}"))) {
                                val senses = entry.getJSONArray("senses")
                                val meanings = mutableListOf<String>()
                                val examples = mutableListOf<String>()

                                if (senses.length() > 0) {
                                    val sense = senses.getJSONObject(0)
                                    if (sense.has("english_definitions")) {
                                        val defs = sense.getJSONArray("english_definitions")
                                        for (j in 0 until defs.length()) {
                                            meanings.add(defs.getString(j))
                                        }
                                    }
                                }

                                // Generate example if possible (simplified)
                                if (reading.isNotEmpty()) {
                                    examples.add("$word ($reading) - ${meanings.firstOrNull() ?: "unknown"}")
                                }

                                kanjiList.add(
                                    JapaneseCharacter(
                                        char = word,
                                        romanization = reading,
                                        type = "Kanji",
                                        meanings = meanings,
                                        examples = examples.takeIf { it.isNotEmpty() } ?: listOf("No examples available")
                                    )
                                )
                            }
                        }
                    }

                    Handler(Looper.getMainLooper()).post {
                        onResult(kanjiList)
                    }
                } catch (e: Exception) {
                    Handler(Looper.getMainLooper()).post {
                        onResult(emptyList())
                    }
                }
            } ?: run {
                Handler(Looper.getMainLooper()).post {
                    onResult(emptyList())
                }
            }
        }
    })
}

@Composable
fun CharacterCard(character: JapaneseCharacter, onClick: (JapaneseCharacter) -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .clickable { onClick(character) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = character.char,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = character.romanization,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun KanjiCard(kanji: JapaneseCharacter, onClick: (JapaneseCharacter) -> Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable { onClick(kanji) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = kanji.char,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = kanji.romanization,
                fontSize = 14.sp
            )
            Text(
                text = kanji.meanings.joinToString(", "),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CharacterDetailDialog(character: JapaneseCharacter, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = character.char,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = character.romanization,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Type: ${character.type}",
                    fontSize = 16.sp
                )

                if (character.meanings.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Meanings: ${character.meanings.joinToString(", ")}",
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Examples:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                LazyColumn(
                    modifier = Modifier.height(120.dp)
                ) {
                    character.examples.forEach { example ->
                        item {
                            Text(
                                text = "• $example",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun JishoDictionaryLookup() {
    var inputWord by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<JishoResult>>(emptyList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            text = "Jisho Dictionary Lookup",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.OutlinedTextField(
                value = inputWord,
                onValueChange = { inputWord = it },
                placeholder = { Text("Enter Japanese or English word") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (inputWord.isNotBlank()) {
                        isLoading = true
                        searchJisho(inputWord) { results ->
                            searchResults = results
                            isLoading = false
                        }
                    }
                }
            ) {
                Text("Search")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading results...")
            }
        } else if (searchResults.isEmpty() && inputWord.isNotBlank()) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No results found")
            }
        } else {
            LazyColumn {
                items(searchResults.size) { index ->
                    JishoResultCard(searchResults[index])
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun JishoResultCard(result: JishoResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = result.japanese,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                if (result.reading.isNotEmpty() && result.reading != result.japanese) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "[${result.reading}]",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                result.meanings.forEachIndexed { index, meaning ->
                    Text(
                        text = "${index + 1}. $meaning",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }

            if (result.partsOfSpeech.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Parts of speech: ${result.partsOfSpeech.joinToString(", ")}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun searchJisho(word: String, onResult: (List<JishoResult>) -> Unit) {
    val client = OkHttpClient()
    val encodedWord = java.net.URLEncoder.encode(word, "UTF-8")
    val url = "https://jisho.org/api/v1/search/words?keyword=$encodedWord"
    val request = Request.Builder().url(url).build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Handler(Looper.getMainLooper()).post {
                onResult(emptyList())
            }
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { body ->
                try {
                    val json = JSONObject(body)
                    val data = json.getJSONArray("data")
                    val results = mutableListOf<JishoResult>()

                    for (i in 0 until minOf(data.length(), 10)) {
                        val entry = data.getJSONObject(i)
                        val japanese = entry.getJSONArray("japanese")
                        val japaneseWord = if (japanese.length() > 0) {
                            val japaneseObj = japanese.getJSONObject(0)
                            val word = if (japaneseObj.has("word")) japaneseObj.getString("word") else ""
                            val reading = if (japaneseObj.has("reading")) japaneseObj.getString("reading") else word
                            Pair(word, reading)
                        } else {
                            Pair("", "")
                        }

                        val senses = entry.getJSONArray("senses")
                        val meanings = mutableListOf<String>()
                        val partsOfSpeech = mutableListOf<String>()

                        if (senses.length() > 0) {
                            val sense = senses.getJSONObject(0)
                            if (sense.has("parts_of_speech")) {
                                val pos = sense.getJSONArray("parts_of_speech")
                                for (j in 0 until pos.length()) {
                                    partsOfSpeech.add(pos.getString(j))
                                }
                            }
                            if (sense.has("english_definitions")) {
                                val defs = sense.getJSONArray("english_definitions")
                                for (j in 0 until defs.length()) {
                                    meanings.add(defs.getString(j))
                                }
                            }
                        }

                        results.add(
                            JishoResult(
                                japanese = japaneseWord.first,
                                reading = japaneseWord.second,
                                meanings = meanings,
                                partsOfSpeech = partsOfSpeech
                            )
                        )
                    }

                    Handler(Looper.getMainLooper()).post {
                        onResult(results)
                    }
                } catch (e: Exception) {
                    Handler(Looper.getMainLooper()).post {
                        onResult(emptyList())
                    }
                }
            } ?: run {
                Handler(Looper.getMainLooper()).post {
                    onResult(emptyList())
                }
            }
        }
    })
}

data class JishoResult(
    val japanese: String,
    val reading: String,
    val meanings: List<String>,
    val partsOfSpeech: List<String>
)

data class JapaneseCharacter(
    val char: String,
    val romanization: String,
    val type: String,
    val meanings: List<String> = emptyList(),
    val examples: List<String> = emptyList()
)