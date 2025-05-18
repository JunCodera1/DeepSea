package com.example.deepsea.ui.screens.feature.learn

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder
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
                    text = { Text(text = title, fontSize = 12.sp) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Crossfade(targetState = selectedTabIndex) { index ->
            when (index) {
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
    }

    if (showDetailDialog && selectedCharacter != null) {
        CharacterDetailDialog(
            character = selectedCharacter!!,
            onDismiss = { showDetailDialog = false }
        )
    }
}

class AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun play(url: String, onCompletion: () -> Unit) {
        // Release any existing player first
        release()

        // Create and configure a new player
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )

            try {
                setDataSource(url)
                setOnPreparedListener { mp -> mp.start() }
                setOnCompletionListener { mp ->
                    mp.reset()
                    onCompletion()
                }
                setOnErrorListener { mp, _, _ ->
                    mp.reset()
                    onCompletion()
                    true
                }
                prepareAsync()
            } catch (e: Exception) {
                e.printStackTrace()
                onCompletion()
            }
        }
    }

    fun release() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            reset()
            release()
        }
        mediaPlayer = null
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

    // Define examples for each Hiragana character
    val hiraganaExamples = mapOf(
        "あ" to listOf("あさ (asa) - morning", "あめ (ame) - rain", "あい (ai) - love"),
        "い" to listOf("いぬ (inu) - dog", "いち (ichi) - one", "いえ (ie) - house"),
        "う" to listOf("うみ (umi) - sea", "うた (uta) - song", "うえ (ue) - above"),
        "え" to listOf("えき (eki) - station", "えん (en) - yen", "えがく (egaku) - to draw"),
        "お" to listOf("おちゃ (ocha) - tea", "おに (oni) - demon", "おく (oku) - to put"),
        "か" to listOf("かさ (kasa) - umbrella", "かわ (kawa) - river", "かく (kaku) - to write"),
        "き" to listOf("きく (kiku) - to listen", "きた (kita) - north", "きもの (kimono) - kimono"),
        "く" to listOf("くち (kuchi) - mouth", "くも (kumo) - cloud", "くる (kuru) - to come"),
        "け" to listOf("けさ (kesa) - this morning", "けん (ken) - sword", "ける (keru) - to kick"),
        "こ" to listOf("こえ (koe) - voice", "こめ (kome) - rice", "こい (koi) - carp"),
        "さ" to listOf("さかな (sakana) - fish", "さく (saku) - to bloom", "さる (saru) - monkey"),
        "し" to listOf("しお (shio) - salt", "しろ (shiro) - white", "した (shita) - below"),
        "す" to listOf("すし (sushi) - sushi", "すむ (sumu) - to live", "すき (suki) - to like"),
        "せ" to listOf("せかい (sekai) - world", "せん (sen) - line", "せなか (senaka) - back"),
        "そ" to listOf("そら (sora) - sky", "そと (soto) - outside", "そば (soba) - noodles"),
        "た" to listOf("たべる (taberu) - to eat", "たかい (takai) - high", "たま (tama) - ball"),
        "ち" to listOf("ちかい (chikai) - near", "ちず (chizu) - map", "ちち (chichi) - father"),
        "つ" to listOf("つき (tsuki) - moon", "つめ (tsume) - nail", "つくる (tsukuru) - to make"),
        "て" to listOf("て (te) - hand", "てん (ten) - point", "てる (teru) - to shine"),
        "と" to listOf("とり (tori) - bird", "とぶ (tobu) - to fly", "とも (tomo) - friend"),
        "な" to listOf("なつ (natsu) - summer", "なまえ (namae) - name", "なか (naka) - inside"),
        "に" to listOf("にく (niku) - meat", "にほん (nihon) - Japan", "にわ (niwa) - garden"),
        "ぬ" to listOf("ぬま (numa) - swamp", "ぬの (nuno) - cloth", "ぬく (nuku) - to warm"),
        "ね" to listOf("ねこ (neko) - cat", "ねつ (nets傾向(netsu) - fever", "ねる (neru) - to sleep"),
        "の" to listOf("のみもの (nomimono) - drink", "のり (nori) - seaweed", "のぼる (noboru) - to climb"),
        "は" to listOf("はな (hana) - flower", "はし (hashi) - bridge", "はる (haru) - spring"),
        "ひ" to listOf("ひかり (hikari) - light", "ひる (hiru) - noon", "ひく (hiku) - to pull"),
        "ふ" to listOf("ふね (fune) - boat", "ふゆ (fuyu) - winter", "ふく (fuku) - clothes"),
        "へ" to listOf("へや (heya) - room", "へい (hei) - wall", "へそ (heso) - navel"),
        "ほ" to listOf("ほし (hoshi) - star", "ほん (hon) - book", "ほね (hone) - bone"),
        "ま" to listOf("まち (machi) - town", "まど (mado) - window", "まう (mau) - to dance"),
        "み" to listOf("みず (mizu) - water", "みる (miru) - to see", "みみ (mimi) - ear"),
        "む" to listOf("むし (mushi) - insect", "むら (mura) - village", "むね (mune) - chest"),
        "め" to listOf("め (me) - eye", "めし (meshi) - meal", "める (meru) - to email"),
        "も" to listOf("もり (mori) - forest", "もの (mono) - thing", "もつ (motsu) - to hold"),
        "や" to listOf("やま (yama) - mountain", "やさい (yasai) - vegetable", "やく (yaku) - to bake"),
        "ゆ" to listOf("ゆき (yuki) - snow", "ゆめ (yume) - dream", "ゆう (yuu) - evening"),
        "よ" to listOf("よる (yoru) - night", "よむ (yomu) - to read", "よこ (yoko) - side"),
        "ら" to listOf("らく (raku) - comfort", "らし (rashi) - seems", "らん (ran) - orchid"),
        "り" to listOf("りんご (ringo) - apple", "りく (riku) - land", "りょう (ryou) - quantity"),
        "る" to listOf("るす (rusu) - absence", "るい (rui) - type", "るな (runa) - luna"),
        "れ" to listOf("れいぞうこ (reizoko) - refrigerator", "れん (ren) - love", "れつ (retsu) - row"),
        "ろ" to listOf("ろく (roku) - six", "ろん (ron) - argument", "ろぼ (robo) - robot"),
        "わ" to listOf("わかい (wakai) - young", "わすれる (wasureru) - to forget", "わる (waru) - bad"),
        "を" to listOf("を (wo) - object marker", "をんな (onna) - woman", "をう (ou) - to chase"),
        "ん" to listOf("んまい (nmai) - delicious", "ん (n) - sound", "んだ (nda) - explanatory particle")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        hiraganaChars.forEachIndexed { index, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (index % 2 == 0) MaterialTheme.colorScheme.surfaceVariant
                        else MaterialTheme.colorScheme.surface
                    )
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { (char, romanization) ->
                    CharacterCard(
                        character = JapaneseCharacter(
                            char = char,
                            romanization = romanization,
                            type = "Hiragana",
                            examples = hiraganaExamples[char] ?: listOf("No examples available")
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

    // Define examples for each Katakana character
    val katakanaExamples = mapOf(
        "ア" to listOf("アメリカ (Amerika) - America", "アルバム (arubamu) - album", "アパート (apāto) - apartment"),
        "イ" to listOf("インターネット (intānetto) - internet", "イメージ (imēji) - image", "イギリス (Igirisu) - England"),
        "ウ" to listOf("ウェブ (webu) - web", "ウィスキー (uisukī) - whiskey", "ウイルス (uirusu) - virus"),
        "エ" to listOf("エネルギー (enerugī) - energy", "エアコン (eakon) - air conditioner", "エレベーター (erebētā) - elevator"),
        "オ" to listOf("オフィス (ofisu) - office", "オレンジ (orenji) - orange", "オーストラリア (Ōsutoraria) - Australia"),
        "カ" to listOf("カメラ (kamera) - camera", "カレンダー (karendā) - calendar", "カレー (karē) - curry"),
        "キ" to listOf("キッチン (kitchin) - kitchen", "キー (kī) - key", "キロ (kiro) - kilo"),
        "ク" to listOf("クラス (kurasu) - class", "クラブ (kurabu) - club", "クリスマス (Kurisumasu) - Christmas"),
        "ケ" to listOf("ケーキ (kēki) - cake", "ゲーム (gēmu) - game", "ケータイ (kētai) - mobile phone"),
        "コ" to listOf("コーヒー (kōhī) - coffee", "コンピューター (konpyūtā) - computer", "コンサート (konsāto) - concert"),
        "サ" to listOf("サラダ (sarada) - salad", "サービス (sābisu) - service", "サッカー (sakkā) - soccer"),
        "シ" to listOf("シャワー (shawā) - shower", "シーズン (shīzun) - season", "シティ (shiti) - city"),
        "ス" to listOf("スーパー (sūpā) - supermarket", "スポーツ (supōtsu) - sports", "スーツ (sūtsu) - suit"),
        "セ" to listOf("セール (sēru) - sale", "センター (sentā) - center", "センチ (senchi) - centimeter"),
        "ソ" to listOf("ソフトウェア (sofutouea) - software", "ソファー (sofā) - sofa", "ソース (sōsu) - sauce"),
        "タ" to listOf("タクシー (takushī) - taxi", "タバコ (tabako) - cigarette", "タレント (tarento) - talent"),
        "チ" to listOf("チーム (chīmu) - team", "チケット (chiketto) - ticket", "チーズ (chīzu) - cheese"),
        "ツ" to listOf("ツアー (tsuā) - tour", "ツイッター (tsuittā) - Twitter", "ツール (tsūru) - tool"),
        "テ" to listOf("テレビ (terebi) - television", "テスト (tesuto) - test", "テーブル (tēburu) - table"),
        "ト" to listOf("トイレ (toire) - toilet", "トマト (tomato) - tomato", "トラック (torakku) - truck"),
        "ナ" to listOf("ナイフ (naifu) - knife", "ナンバー (nanbā) - number", "ナース (nāsu) - nurse"),
        "ニ" to listOf("ニュース (nyūsu) - news", "ニック (nikku) - nickname", "ニット (nitto) - knit"),
        "ヌ" to listOf("ヌードル (nūdoru) - noodle", "ヌーベル (nūberu) - novel", "ヌーディスト (nūdisuto) - nudist"),
        "ネ" to listOf("ネット (netto) - net", "ネットワーク (nettowāku) - network", "ネックレス (nekkuresu) - necklace"),
        "ノ" to listOf("ノート (nōto) - note", "ノック (nokku) - knock", "ノーベル (nōberu) - Nobel"),
        "ハ" to listOf("ハンバーガー (hanbāgā) - hamburger", "ハンドバッグ (handobaggu) - handbag", "ハワイ (Hawai) - Hawaii"),
        "ヒ" to listOf("ヒーロー (hīrō) - hero", "ヒット (hitto) - hit", "ヒップ (hippu) - hip"),
        "フ" to listOf("フットボール (futtobōru) - football", "フルーツ (furūtsu) - fruit", "ファン (fan) - fan"),
        "ヘ" to listOf("ヘア (hea) - hair", "ヘリコプター (herikoputā) - helicopter", "ヘッド (heddo) - head"),
        "ホ" to listOf("ホテル (hoteru) - hotel", "ホーム (hōmu) - home", "ホラー (horā) - horror"),
        "マ" to listOf("マシン (mashin) - machine", "マスク (masuku) - mask", "マラソン (marason) - marathon"),
        "ミ" to listOf("ミルク (miruku) - milk", "ミサイル (misairu) - missile", "ミニ (mini) - mini"),
        "ム" to listOf("ムービー (mūbī) - movie", "ミュージック (myūjikku) - music", "ムード (mūdo) - mood"),
        "メ" to listOf("メール (mēru) - mail", "メニュー (menyū) - menu", "メートル (mētoru) - meter"),
        "モ" to listOf("モデル (moderu) - model", "モニター (monitā) - monitor", "モード (mōdo) - mode"),
        "ヤ" to listOf("ヤード (yādo) - yard", "ヤング (yangu) - young", "ヤンキー (yankī) - yankee"),
        "ユ" to listOf("ユーザー (yūzā) - user", "ユーモア (yūmoa) - humor", "ユーロ (yūro) - euro"),
        "ヨ" to listOf("ヨガ (yoga) - yoga", "ヨット (yotto) - yacht", "ヨーロッパ (Yōroppa) - Europe"),
        "ラ" to listOf("ラジオ (rajio) - radio", "ランチ (ranchi) - lunch", "ランプ (ranpu) - lamp"),
        "リ" to listOf("リーダー (rīdā) - leader", "リスト (risuto) - list", "リモコン (rimokon) - remote control"),
        "ル" to listOf("ルール (rūru) - rule", "ルート (rūto) - route", "ルーム (rūmu) - room"),
        "レ" to listOf("レコード (rekōdo) - record", "レストラン (resutoran) - restaurant", "レンタル (rentaru) - rental"),
        "ロ" to listOf("ロボット (robotto) - robot", "ロック (rokku) - rock", "ロマン (roman) - romance"),
        "ワ" to listOf("ワイン (wain) - wine", "ワイシャツ (waishatsu) - white shirt", "ワーク (wāku) - work"),
        "ヲ" to listOf("ヲタク (otaku) - otaku", "ヲルフ (worufu) - wolf", "ヲーカー (wōkā) - walker"),
        "ン" to listOf("オンライン (onrain) - online", "エンジン (enjin) - engine", "テンション (tenshon) - tension")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        katakanaChars.forEachIndexed { index, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (index % 2 == 0) MaterialTheme.colorScheme.surfaceVariant
                        else MaterialTheme.colorScheme.surface
                    )
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { (char, romanization) ->
                    CharacterCard(
                        character = JapaneseCharacter(
                            char = char,
                            romanization = romanization,
                            type = "Katakana",
                            examples = katakanaExamples[char] ?: listOf("No examples available")
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

    // Initial load using LaunchedEffect
    LaunchedEffect(Unit) {
        loadKanji()
    }

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
                CircularProgressIndicator()
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
    Card(
        modifier = Modifier
            .size(60.dp)
            .clickable { onClick(character) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var inputWord by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<JishoResult>>(emptyList()) }

    // Audio player state management
    val audioPlayer = remember { AudioPlayer() }
    var currentlyPlayingIndex by remember { mutableStateOf<Int?>(null) }

    // Clean up resources when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.release()
        }
    }

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
            OutlinedTextField(
                value = inputWord,
                onValueChange = { inputWord = it },
                placeholder = { Text("Enter Japanese or English word") },
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    if (inputWord.isNotBlank()) {
                        IconButton(
                            onClick = {
                                isLoading = true
                                searchJishoWithAudio(inputWord) { results ->
                                    searchResults = results
                                    isLoading = false
                                }
                            }
                        ) {
                            Icon(Icons.Default.Search, "Search")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (inputWord.isNotBlank()) {
                        isLoading = true
                        searchJishoWithAudio(inputWord) { results ->
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
                CircularProgressIndicator()
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
                    val result = searchResults[index]
                    JishoResultCardWithAudio(
                        result = result,
                        isPlaying = currentlyPlayingIndex == index,
                        onPlayAudio = {
                            if (result.audioUrl != null) {
                                currentlyPlayingIndex = index
                                coroutineScope.launch {
                                    audioPlayer.play(result.audioUrl) {
                                        currentlyPlayingIndex = null
                                    }
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun JishoResultCardWithAudio(
    result: JishoResult,
    isPlaying: Boolean,
    onPlayAudio: () -> Unit
) {
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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
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
                }

                // Audio button
                if (result.audioUrl != null) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onPlayAudio,
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    if (isPlaying) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.VolumeUp,
                                contentDescription = "Play pronunciation",
                                tint = if (isPlaying) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.primary
                            )


                        }

                        if (isPlaying) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(4.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp
                            )
                        }
                    }
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

fun searchJishoWithAudio(word: String, onResult: (List<JishoResult>) -> Unit) {
    val client = OkHttpClient()
    val encodedWord = URLEncoder.encode(word, "UTF-8")
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

                        // Find audio URL if available
                        var audioUrl: String? = null
                        if (entry.has("attribution")) {
                            try {
                                if (entry.has("media") && !entry.isNull("media")) {
                                    val mediaArray = entry.getJSONArray("media")
                                    for (j in 0 until mediaArray.length()) {
                                        val mediaObj = mediaArray.getJSONObject(j)
                                        if (mediaObj.has("audio") && !mediaObj.isNull("audio")) {
                                            val audioObj = mediaObj.getJSONObject("audio")
                                            if (audioObj.has("mp3") && !audioObj.isNull("mp3")) {
                                                audioUrl = audioObj.getString("mp3")
                                                break
                                            } else if (audioObj.has("opus") && !audioObj.isNull("opus")) {
                                                audioUrl = audioObj.getString("opus")
                                                break
                                            } else if (audioObj.has("aac") && !audioObj.isNull("aac")) {
                                                audioUrl = audioObj.getString("aac")
                                                break
                                            }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                // Handle exception in audio URL extraction
                                e.printStackTrace()
                            }
                        }

                        // If no audio URL in Jisho API response, try to generate one using TTS API
                        if (audioUrl == null && japaneseWord.first.isNotEmpty()) {
                            // Using TTS API for Japanese pronunciation
                            audioUrl = getTextToSpeechUrl(japaneseWord.first)
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
                                partsOfSpeech = partsOfSpeech,
                                audioUrl = audioUrl
                            )
                        )
                    }

                    Handler(Looper.getMainLooper()).post {
                        onResult(results)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
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

fun getTextToSpeechUrl(text: String): String {
    val encodedText = URLEncoder.encode(text, "UTF-8")

    // Use Google Translate TTS API (note: this is not officially supported and may require API key for production)
    // In a real app, you should use an official TTS API service or bundle your own audio files
    return "https://translate.google.com/translate_tts?ie=UTF-8&q=$encodedText&tl=ja&client=tw-ob"

    // Alternative free TTS APIs:
    // return "https://api.voicerss.org/?key=YOUR_API_KEY&hl=ja-jp&src=$encodedText"
    // return "https://ttsmp3.com/makemp3_new.php?msg=$encodedText&lang=Takumi&source=ttsmp3"
}

// Ext

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
    val partsOfSpeech: List<String>,
    val audioUrl: String? = null
)

data class JapaneseCharacter(
    val char: String,
    val romanization: String,
    val type: String,
    val meanings: List<String> = emptyList(),
    val examples: List<String> = emptyList()
)