package com.example.deepsea.utils

import com.example.deepsea.ui.screens.feature.game.DifficultyLevel
import com.example.deepsea.ui.screens.feature.game.Question

val allQuestions = listOf(
    // QUIZ - JAPANESE
    Question(
        id = 1,
        text = "What is the Japanese word for 'house'?",
        options = listOf("犬", "家", "車", "木"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'家' (ie) means 'house'. '犬' (inu) is 'dog', '車' (kuruma) is 'car', '木' (ki) is 'tree'.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 2,
        text = "Which sentence is correct in Japanese?",
        options = listOf("私は行きます学校。", "私は学校に行きます。", "私は行きます学校に。", "私は学校に行く。"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'私は学校に行きます。' (Watashi wa gakkou ni ikimasu.) is correct, meaning 'I go to school.' The particle 'に' and verb order are proper.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 3,
        text = "What is the past tense of 'to eat' (食べる) in Japanese?",
        options = listOf("食べました", "食べます", "食べられ", "食べている"),
        correctAnswer = 0,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'食べました' (tabemashita) is the polite past tense of 'to eat'. '食べます' is present/future, '食べられ' is passive, '食べている' is present continuous.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 4,
        text = "What is the Japanese word for 'thank you'?",
        options = listOf("お願い", "ありがとう", "すみません", "おはよう"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'ありがとう' (arigatou) means 'thank you'. 'お願い' (onegai) is 'please', 'すみません' (sumimasen) is 'excuse me', 'おはよう' (ohayou) is 'good morning'.",
        difficulty = DifficultyLevel.EASY
    ),

    // SCRAMBLE
    Question(
        id = 5,
        text = "Unscramble the letters to form a Japanese word for 'book': B-U-K-K-O",
        options = listOf("Bukko", "Kobbu", "Honbu", "Hon"),
        correctAnswer = 3,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'本' (hon) is the Japanese word for 'book', formed by unscrambling B-U-K-K-O (simplified as 'hon' in kana).",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 6,
        text = "Unscramble T-O-M-O-D-A-C-H-I to form a Japanese word for 'friend':",
        options = listOf("Tomodachi", "Chatomi", "Dachito", "Moticha"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'友達' (tomodachi) is the Japanese word for 'friend'.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 7,
        text = "Unscramble S-A-K-U-R-A to form a Japanese word:",
        options = listOf("Sakura", "Rusaka", "Kusara", "Arasuk"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'桜' (sakura) means 'cherry blossom' in Japanese.",
        difficulty = DifficultyLevel.MEDIUM
    ),

    // MATCH
    Question(
        id = 8,
        text = "Match the English word to its Japanese meaning: 'Dog'",
        options = listOf("猫", "犬", "鳥", "魚"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'犬' (inu) means 'dog' in Japanese. '猫' (neko) is 'cat', '鳥' (tori) is 'bird', '魚' (sakana) is 'fish'.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 9,
        text = "Match the English word to its Japanese meaning: 'Cat'",
        options = listOf("犬", "猫", "鳥", "魚"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'猫' (neko) means 'cat' in Japanese. '犬' (inu) is 'dog', '鳥' (tori) is 'bird', '魚' (sakana) is 'fish'.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 10,
        text = "Match the English word to its Japanese meaning: 'Water'",
        options = listOf("水", "雲", "火", "風"),
        correctAnswer = 0,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'水' (mizu) means 'water' in Japanese. '雲' (kumo) is 'cloud', '火' (hi) is 'fire', '風' (kaze) is 'wind'.",
        difficulty = DifficultyLevel.MEDIUM
    ),

    // DAILY CHALLENGE - Mix of modes
    Question(
        id = 11,
        text = "Today's challenge: What is the Japanese word for 'goodbye'?",
        options = listOf("こんにちは", "さようなら", "ありがとう", "おやすみ"),
        correctAnswer = 1,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'さようなら' (sayounara) means 'goodbye' in Japanese.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 12,
        text = "Today's challenge: Unscramble N-I-H-O-N to form a Japanese word for 'Japan':",
        options = listOf("Nihon", "Honni", "Inhon", "Nonhi"),
        correctAnswer = 0,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'日本' (nihon) is the Japanese word for 'Japan'.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 13,
        text = "What is the Japanese word for 'mountain'?",
        options = listOf("川", "山", "海", "森"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'山' (yama) means 'mountain'. '川' (kawa) is 'river', '海' (umi) is 'sea', '森' (mori) is 'forest'.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 14,
        text = "Which particle is used to indicate the location in Japanese?",
        options = listOf("は", "に", "を", "で"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'に' (ni) indicates location. 'は' (wa) is topic marker, 'を' (wo) is object marker, 'で' (de) indicates means or place of action.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 15,
        text = "What is the polite form of 'to know' (知る) in present tense?",
        options = listOf("知ります", "知った", "知られる", "知っています"),
        correctAnswer = 0,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'知ります' (shimasu) is the polite present tense of 'to know'. '知った' is past, '知られる' is passive, '知っています' is continuous.",
        difficulty = DifficultyLevel.HARD
    ),

    // SCRAMBLE - Thêm
    Question(
        id = 16,
        text = "Unscramble K-A-W-A to form a Japanese word for 'river':",
        options = listOf("Kawa", "Waka", "Akwa", "Kawa"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'川' (kawa) means 'river' in Japanese.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 17,
        text = "Unscramble T-S-U-K-I to form a Japanese word for 'moon':",
        options = listOf("Tsuki", "Kuits", "Sukit", "Tikus"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'月' (tsuki) means 'moon' in Japanese.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 18,
        text = "Unscramble H-A-R-U-N-I to form a Japanese word for 'spring':",
        options = listOf("Haruni", "Haru", "Nihar", "Runih"),
        correctAnswer = 1,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'春' (haru) means 'spring' in Japanese (simplified from HARU-NI).",
        difficulty = DifficultyLevel.HARD
    ),

    // MATCH - Thêm
    Question(
        id = 19,
        text = "Match the English word to its Japanese meaning: 'Tree'",
        options = listOf("木", "石", "土", "金"),
        correctAnswer = 0,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'木' (ki) means 'tree'. '石' (ishi) is 'stone', '土' (tsuchi) is 'earth', '金' (kin) is 'gold'.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 20,
        text = "Match the English word to its Japanese meaning: 'Rain'",
        options = listOf("雪", "雨", "風", "雷"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'雨' (ame) means 'rain'. '雪' (yuki) is 'snow', '風' (kaze) is 'wind', '雷' (kaminari) is 'thunder'.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 21,
        text = "Match the English word to its Japanese meaning: 'Love'",
        options = listOf("憎しみ", "愛", "恨み", "悲しみ"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'愛' (ai) means 'love'. '憎しみ' (nikushimi) is 'hatred', '恨み' (urami) is 'resentment', '悲しみ' (kanashimi) is 'sadness'.",
        difficulty = DifficultyLevel.HARD
    ),

    // DAILY_CHALLENGE - Thêm
    Question(
        id = 22,
        text = "Today's challenge: What is the Japanese word for 'hello'?",
        options = listOf("さようなら", "こんにちは", "ありがとう", "おやすみ"),
        correctAnswer = 1,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'こんにちは' (konnichiwa) means 'hello' in Japanese.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 23,
        text = "Today's challenge: Unscramble K-I-Z-U-N-A to form a Japanese word for 'bond':",
        options = listOf("Kizuna", "Zunaki", "Nakuzi", "Kizanu"),
        correctAnswer = 0,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'絆' (kizuna) means 'bond' or 'connection' in Japanese.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 24,
        text = "Today's challenge: What is the Japanese honorific suffix for teachers?",
        options = listOf("さん", "君", "先生", "ちゃん"),
        correctAnswer = 2,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'先生' (sensei) is the honorific suffix for teachers. 'さん' (san) is a general honorific, '君' (kun) is for younger males, 'ちゃん' (chan) is affectionate.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 25,
        text = "Today's challenge: Match the English word to its Japanese meaning: 'Flower'",
        options = listOf("木", "花", "葉", "根"),
        correctAnswer = 1,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'花' (hana) means 'flower'. '木' (ki) is 'tree', '葉' (ha) is 'leaf', '根' (ne) is 'root'.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 26,
        text = "Today's challenge: Unscramble S-A-M-U-R-A-I to form a Japanese word:",
        options = listOf("Samurai", "Raisamu", "Musara", "Sairuma"),
        correctAnswer = 0,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'侍' (samurai) refers to a Japanese warrior.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 27,
        text = "Today's challenge: What is the negative polite form of 'to go' (行く) in present tense?",
        options = listOf("行きません", "行きました", "行かれません", "行っています"),
        correctAnswer = 0,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'行きません' (ikimasen) is the polite negative present tense of 'to go'. '行きました' is past, '行かれません' is passive negative, '行っています' is continuous.",
        difficulty = DifficultyLevel.HARD
    ),

    // HARD Questions - Thêm
    Question(
        id = 28,
        text = "What is the correct keigo (honorific) form of 'to do' (する) in Japanese?",
        options = listOf("します", "いたします", "しました", "されません"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'いたします' (itashimasu) is the honorific form of 'to do'. 'します' is polite but not honorific, 'しました' is past, 'されません' is passive negative.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 29,
        text = "Unscramble T-E-N-N-O-U to form a Japanese word for 'emperor':",
        options = listOf("Tennou", "Outenn", "Nouten", "Tenuno"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'天皇' (tennou) means 'emperor' in Japanese.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 30,
        text = "Match the English word to its Japanese meaning: 'Philosophy'",
        options = listOf("科学", "哲学", "歴史", "芸術"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'哲学' (tetsugaku) means 'philosophy'. '科学' (kagaku) is 'science', '歴史' (rekishi) is 'history', '芸術' (geijutsu) is 'art'.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 31,
        text = "Today's challenge: What is the causative form of 'to read' (読む) in polite present tense?",
        options = listOf("読みます", "読ませます", "読まれます", "読みました"),
        correctAnswer = 1,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'読ませます' (yomasemasu) is the polite causative form of 'to read', meaning 'to make someone read'. '読みます' is plain polite, '読まれます' is passive, '読みました' is past.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 32,
        text = "What is the Japanese word for 'etiquette' in a formal context?",
        options = listOf("礼儀", "習慣", "文化", "伝統"),
        correctAnswer = 0,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'礼儀' (reigi) means 'etiquette' or 'manners'. '習慣' (shuukan) is 'habit', '文化' (bunka) is 'culture', '伝統' (dentou) is 'tradition'.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 33,
        text = "What is the Japanese word for 'sky'?",
        options = listOf("雲", "空", "星", "月"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'空' (sora) means 'sky'. '雲' (kumo) is 'cloud', '星' (hoshi) is 'star', '月' (tsuki) is 'moon'.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 34,
        text = "Which verb form is used for a humble request in Japanese?",
        options = listOf("ください", "します", "いただきます", "あります"),
        correctAnswer = 0,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'ください' (kudasai) is used for humble requests. 'します' is 'do', 'いただきます' is a humble expression for 'receive', 'あります' is 'there is'.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 35,
        text = "What is the te-form of 'to write' (書く) in Japanese?",
        options = listOf("書きます", "書き", "書いて", "書かれ"),
        correctAnswer = 2,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'書いて' (kaite) is the te-form of 'to write'. '書きます' is polite present, '書き' is dictionary stem, '書かれ' is passive.",
        difficulty = DifficultyLevel.HARD
    ),

    // SCRAMBLE - Thêm
    Question(
        id = 36,
        text = "Unscramble Y-U-U-K-I to form a Japanese word for 'snow':",
        options = listOf("Yuki", "Kiyu", "Uyki", "Kyui"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'雪' (yuki) means 'snow' in Japanese.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 37,
        text = "Unscramble T-A-N-I-K-A to form a Japanese word for 'valley':",
        options = listOf("Tanika", "Tani", "Katani", "Nitaka"),
        correctAnswer = 1,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'谷' (tani) means 'valley' in Japanese (simplified from TAN-I-K-A).",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 38,
        text = "Unscramble S-H-I-N-J-U to form a Japanese word for 'pearl':",
        options = listOf("Shinju", "Junshi", "Shujin", "Jinshu"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'真珠' (shinju) means 'pearl' in Japanese.",
        difficulty = DifficultyLevel.HARD
    ),

    // MATCH - Thêm
    Question(
        id = 39,
        text = "Match the English word to its Japanese meaning: 'Sun'",
        options = listOf("月", "太陽", "星", "雲"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'太陽' (taiyou) means 'sun'. '月' (tsuki) is 'moon', '星' (hoshi) is 'star', '雲' (kumo) is 'cloud'.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 40,
        text = "Match the English word to its Japanese meaning: 'Bridge'",
        options = listOf("道", "橋", "川", "山"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'橋' (hashi) means 'bridge'. '道' (michi) is 'road', '川' (kawa) is 'river', '山' (yama) is 'mountain'.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 41,
        text = "Match the English word to its Japanese meaning: 'Courage'",
        options = listOf("恐怖", "勇気", "弱さ", "悲しみ"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'勇気' (yuuki) means 'courage'. '恐怖' (kyoufu) is 'fear', '弱さ' (yowasa) is 'weakness', '悲しみ' (kanashimi) is 'sadness'.",
        difficulty = DifficultyLevel.HARD
    ),

    // DAILY_CHALLENGE - Thêm
    Question(
        id = 42,
        text = "Today's challenge: What is the Japanese word for 'please'?",
        options = listOf("ありがとう", "お願いします", "さようなら", "こんにちは"),
        correctAnswer = 1,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'お願いします' (onegai shimasu) means 'please' in Japanese.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 43,
        text = "Today's challenge: Unscramble K-A-Z-E to form a Japanese word for 'wind':",
        options = listOf("Kaze", "Zeka", "Azek", "Kzea"),
        correctAnswer = 0,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'風' (kaze) means 'wind' in Japanese.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 44,
        text = "Today's challenge: What is the potential form of 'to see' (見る) in polite present tense?",
        options = listOf("見ます", "見られます", "見ました", "見られ"),
        correctAnswer = 1,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'見られます' (miraremasu) is the polite potential form of 'to see', meaning 'can see'. '見ます' is plain polite, '見ました' is past, '見られ' is incomplete.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 45,
        text = "Today's challenge: Match the English word to its Japanese meaning: 'Forest'",
        options = listOf("川", "森", "山", "海"),
        correctAnswer = 1,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'森' (mori) means 'forest'. '川' (kawa) is 'river', '山' (yama) is 'mountain', '海' (umi) is 'sea'.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 46,
        text = "Today's challenge: Unscramble S-H-I-Z-U-K-A to form a Japanese word for 'quiet':",
        options = listOf("Shizuka", "Kuzashi", "Zukashi", "Shikazu"),
        correctAnswer = 0,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'静か' (shizuka) means 'quiet' in Japanese.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 47,
        text = "Today's challenge: What is the volitional form of 'to learn' (学ぶ) in polite present tense?",
        options = listOf("学びます", "学びましょう", "学びました", "学ばれます"),
        correctAnswer = 1,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'学びましょう' (manabimashou) is the polite volitional form of 'to learn', meaning 'let’s learn'. '学びます' is plain polite, '学びました' is past, '学ばれます' is passive.",
        difficulty = DifficultyLevel.HARD
    ),

    // HARD Questions - Thêm
    Question(
        id = 48,
        text = "What is the passive form of 'to buy' (買う) in polite past tense?",
        options = listOf("買いました", "買われました", "買いました", "買われます"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'買われました' (kawararemashta) is the polite past passive form of 'to buy', meaning 'was bought'. '買いました' is plain past, '買われます' is passive present.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 49,
        text = "Unscramble K-I-N-G-A-K-U to form a Japanese word for 'science':",
        options = listOf("Kingaku", "Kagaku", "Gakukin", "Kunikag"),
        correctAnswer = 1,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'科学' (kagaku) means 'science' in Japanese.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 50,
        text = "Match the English word to its Japanese meaning: 'Justice'",
        options = listOf("不正", "正義", "罪", "罰"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'正義' (seigi) means 'justice'. '不正' (fusei) is 'injustice', '罪' (tsumi) is 'crime', '罰' (batsu) is 'punishment'.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 51,
        text = "Today's challenge: What is the conditional form of 'to come' (来る) in polite present?",
        options = listOf("来ます", "来れば", "来られます", "来ましたら"),
        correctAnswer = 3,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'来ましたら' (kimashita ra) is the polite conditional form of 'to come', meaning 'if (I) come'. '来ます' is plain polite, '来れば' is plain conditional, '来られます' is potential.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 52,
        text = "What is the Japanese word for 'wisdom' in a philosophical context?",
        options = listOf("知恵", "知識", "感情", "経験"),
        correctAnswer = 0,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'知恵' (chie) means 'wisdom'. '知識' (chishiki) is 'knowledge', '感情' (kanjou) is 'emotion', '経験' (keiken) is 'experience'.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 53,
        text = "What is the Japanese word for 'flower'?",
        options = listOf("木", "花", "葉", "根"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'花' (hana) means 'flower'. '木' (ki) is 'tree', '葉' (ha) is 'leaf', '根' (ne) is 'root'.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 54,
        text = "Which particle is used to indicate the indirect object in Japanese?",
        options = listOf("を", "に", "で", "が"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'に' (ni) indicates the indirect object. 'を' (wo) marks the direct object, 'で' (de) indicates means, 'が' (ga) is the subject marker.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 55,
        text = "What is the polite negative form of 'to drink' (飲む) in past tense?",
        options = listOf("飲みませんでした", "飲みます", "飲めませんでした", "飲んでいます"),
        correctAnswer = 0,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'飲みませんでした' (nomanakatta desu) is the polite negative past tense of 'to drink'. '飲みます' is present, '飲めませんでした' is potential negative, '飲んでいます' is continuous.",
        difficulty = DifficultyLevel.HARD
    ),

    // SCRAMBLE - Thêm
    Question(
        id = 56,
        text = "Unscramble H-I-T-O to form a Japanese word for 'person':",
        options = listOf("Hito", "Tohi", "Itoh", "Thio"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'人' (hito) means 'person' in Japanese.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 57,
        text = "Unscramble N-A-M-I to form a Japanese word for 'wave':",
        options = listOf("Nami", "Main", "Inma", "Amni"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'波' (nami) means 'wave' in Japanese.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 58,
        text = "Unscramble K-A-I-S-H-A to form a Japanese word for 'company':",
        options = listOf("Kaisha", "Sakai", "Hakasi", "Ishaka"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'会社' (kaisha) means 'company' in Japanese.",
        difficulty = DifficultyLevel.HARD
    ),

    // MATCH - Thêm
    Question(
        id = 59,
        text = "Match the English word to its Japanese meaning: 'River'",
        options = listOf("山", "川", "海", "森"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'川' (kawa) means 'river'. '山' (yama) is 'mountain', '海' (umi) is 'sea', '森' (mori) is 'forest'.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 60,
        text = "Match the English word to its Japanese meaning: 'Cloud'",
        options = listOf("雨", "雲", "雪", "風"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'雲' (kumo) means 'cloud'. '雨' (ame) is 'rain', '雪' (yuki) is 'snow', '風' (kaze) is 'wind'.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 61,
        text = "Match the English word to its Japanese meaning: 'Peace'",
        options = listOf("戦争", "平和", "争い", "混乱"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'平和' (heiwa) means 'peace'. '戦争' (sensou) is 'war', '争い' (araso) is 'conflict', '混乱' (konran) is 'confusion'.",
        difficulty = DifficultyLevel.HARD
    ),

    // DAILY_CHALLENGE - Thêm
    Question(
        id = 62,
        text = "Today's challenge: What is the Japanese word for 'yes'?",
        options = listOf("いいえ", "はい", "さようなら", "ありがとう"),
        correctAnswer = 1,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'はい' (hai) means 'yes' in Japanese.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 63,
        text = "Today's challenge: Unscramble H-I-R-O to form a Japanese word for 'wide' or 'broad':",
        options = listOf("Hiro", "Rohi", "Ihor", "Hori"),
        correctAnswer = 0,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'広' (hiro) means 'wide' or 'broad' in Japanese (simplified form).",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 64,
        text = "Today's challenge: What is the imperative form of 'to listen' (聞く) in Japanese?",
        options = listOf("聞きます", "聞いて", "聞きなさい", "聞かれ"),
        correctAnswer = 2,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'聞きなさい' (kikinasai) is the polite imperative form of 'to listen', meaning 'listen!'. '聞きます' is polite present, '聞いて' is te-form, '聞かれ' is passive.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 65,
        text = "Today's challenge: Match the English word to its Japanese meaning: 'Fire'",
        options = listOf("水", "火", "風", "土"),
        correctAnswer = 1,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'火' (hi) means 'fire'. '水' (mizu) is 'water', '風' (kaze) is 'wind', '土' (tsuchi) is 'earth'.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 66,
        text = "Today's challenge: Unscramble T-O-K-Y-O to form a Japanese word for the capital city:",
        options = listOf("Tokyo", "Kotyo", "Yotok", "Okyto"),
        correctAnswer = 0,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'東京' (toukyou) is the Japanese word for 'Tokyo'.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 67,
        text = "Today's challenge: What is the desiderative form of 'to sleep' (寝る) in polite present tense?",
        options = listOf("寝ます", "寝たいです", "寝られます", "寝ました"),
        correctAnswer = 1,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'寝たいです' (netai desu) is the polite desiderative form of 'to sleep', meaning 'I want to sleep'. '寝ます' is plain polite, '寝られます' is potential, '寝ました' is past.",
        difficulty = DifficultyLevel.HARD
    ),

    // HARD Questions - Thêm
    Question(
        id = 68,
        text = "What is the causative-passive form of 'to eat' (食べる) in polite present tense?",
        options = listOf("食べます", "食べさせられます", "食べられます", "食べました"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'食べさせられます' (tabesasemasu) is the polite causative-passive form, meaning 'is made to eat'. '食べます' is plain polite, '食べられます' is potential, '食べました' is past.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 69,
        text = "Unscramble B-U-N-K-A to form a Japanese word for 'culture':",
        options = listOf("Bunko", "Bunka", "Kabun", "Kuban"),
        correctAnswer = 1,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'文化' (bunka) means 'culture' in Japanese.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 70,
        text = "Match the English word to its Japanese meaning: 'Hope'",
        options = listOf("絶望", "希望", "失望", "不安"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'希望' (kibou) means 'hope'. '絶望' (zetsubou) is 'despair', '失望' (shitsubou) is 'disappointment', '不安' (fuan) is 'anxiety'.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 71,
        text = "Today's challenge: What is the provisional form of 'to speak' (話す) in polite present?",
        options = listOf("話します", "話せば", "話せばです", "話したら"),
        correctAnswer = 3,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'話したら' (hanasitara) is the provisional form, meaning 'if (I) speak'. '話します' is plain polite, '話せば' is plain conditional, '話せばです' is incorrect.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 72,
        text = "What is the Japanese word for 'patience' in a formal context?",
        options = listOf("我慢", "勇気", "知恵", "努力"),
        correctAnswer = 0,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'我慢' (gaman) means 'patience' or 'endurance'. '勇気' (yuuki) is 'courage', '知恵' (chie) is 'wisdom', '努力' (doryoku) is 'effort'.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 73,
        text = "What is the te-form of 'to give' (くれる) in polite present?",
        options = listOf("くれます", "くれて", "くれてください", "くれられ"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'くれて' (kurete) is the te-form of 'to give'. 'くれます' is polite present, 'くれてください' is a request, 'くれられ' is incomplete.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 74,
        text = "Unscramble S-E-N-S-E-I to form a Japanese word for 'teacher':",
        options = listOf("Sensei", "Seisen", "Ineses", "Sensie"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'先生' (sensei) means 'teacher' in Japanese.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 75,
        text = "Match the English word to its Japanese meaning: 'Dream'",
        options = listOf("現実", "夢", "覚醒", "幻"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'夢' (yume) means 'dream'. '現実' (genjitsu) is 'reality', '覚醒' (kakusei) is 'awakening', '幻' (maboroshi) is 'illusion'.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 76,
        text = "Today's challenge: What is the progressive form of 'to study' (勉強する) in polite present?",
        options = listOf("勉強します", "勉強しています", "勉強しました", "勉強され"),
        correctAnswer = 1,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'勉強しています' (benkyou shite imasu) is the polite progressive form, meaning 'I am studying'. '勉強します' is plain polite, '勉強しました' is past, '勉強され' is incomplete.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 77,
        text = "What is the Japanese word for 'freedom' in a philosophical sense?",
        options = listOf("束縛", "自由", "義務", "権利"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'自由' (jiyuu) means 'freedom'. '束縛' (sokubaku) is 'restraint', '義務' (gimu) is 'duty', '権利' (kenri) is 'right'.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 78,
        text = "Unscramble K-O-K-O-R-O to form a Japanese word for 'heart' or 'mind':",
        options = listOf("Kokoro", "Rokoko", "Koroko", "Kokor"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'心' (kokoro) means 'heart' or 'mind' in Japanese.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 79,
        text = "Match the English word to its Japanese meaning: 'Truth'",
        options = listOf("嘘", "真実", "幻想", "誤解"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'真実' (shinjitsu) means 'truth'. '嘘' (uso) is 'lie', '幻想' (gensou) is 'illusion', '誤解' (gokai) is 'misunderstanding'.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 80,
        text = "Today's challenge: What is the concessive form of 'to live' (住む) in polite present?",
        options = listOf("住みます", "住めば", "住んでも", "住んでいます"),
        correctAnswer = 2,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'住んでも' (sundemo) is the concessive form, meaning 'even if (I) live'. '住みます' is plain polite, '住めば' is conditional, '住んでいます' is progressive.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 81,
        text = "What is the Japanese word for 'harmony' in a cultural context?",
        options = listOf("対立", "調和", "混乱", "競争"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'調和' (chouwa) means 'harmony'. '対立' (tairitsu) is 'conflict', '混乱' (konran) is 'confusion', '競争' (kyousou) is 'competition'.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 82,
        text = "Unscramble Y-U-G-A-T-A to form a Japanese word for 'evening':",
        options = listOf("Yugata", "Tagayu", "Gayuta", "Yuagat"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'夕方' (yugata) means 'evening' in Japanese.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 83,
        text = "What is the Japanese word for 'sea'?",
        options = listOf("川", "山", "海", "森"),
        correctAnswer = 2,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'海' (umi) means 'sea'. '川' (kawa) is 'river', '山' (yama) is 'mountain', '森' (mori) is 'forest'.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 84,
        text = "Which particle indicates possession in Japanese?",
        options = listOf("が", "の", "に", "で"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'の' (no) indicates possession. 'が' (ga) marks the subject, 'に' (ni) indicates direction or indirect object, 'で' (de) indicates means or location of action.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 85,
        text = "What is the polite past form of 'to wait' (待つ) in Japanese?",
        options = listOf("待ちました", "待ちます", "待たれます", "待っています"),
        correctAnswer = 0,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'待ちました' (machimashita) is the polite past form of 'to wait'. '待ちます' is present, '待たれます' is passive, '待っています' is continuous.",
        difficulty = DifficultyLevel.HARD
    ),

    // SCRAMBLE - Thêm
    Question(
        id = 86,
        text = "Unscramble A-M-E to form a Japanese word for 'rain':",
        options = listOf("Ame", "Ema", "Mae", "Eam"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'雨' (ame) means 'rain' in Japanese.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 87,
        text = "Unscramble K-U-M-O to form a Japanese word for 'cloud':",
        options = listOf("Kumo", "Moku", "Okum", "Ukom"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'雲' (kumo) means 'cloud' in Japanese.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 88,
        text = "Unscramble R-E-K-I-S-H-I to form a Japanese word for 'history':",
        options = listOf("Rekishi", "Shireki", "Kireshi", "Hikser"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'歴史' (rekishi) means 'history' in Japanese.",
        difficulty = DifficultyLevel.HARD
    ),

    // MATCH - Thêm
    Question(
        id = 89,
        text = "Match the English word to its Japanese meaning: 'Star'",
        options = listOf("太陽", "星", "月", "雲"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'星' (hoshi) means 'star'. '太陽' (taiyou) is 'sun', '月' (tsuki) is 'moon', '雲' (kumo) is 'cloud'.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 90,
        text = "Match the English word to its Japanese meaning: 'Road'",
        options = listOf("橋", "道", "川", "森"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'道' (michi) means 'road'. '橋' (hashi) is 'bridge', '川' (kawa) is 'river', '森' (mori) is 'forest'.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 91,
        text = "Match the English word to its Japanese meaning: 'Spirit'",
        options = listOf("体", "魂", "心", "命"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'魂' (tamashii) means 'spirit'. '体' (karada) is 'body', '心' (kokoro) is 'heart/mind', '命' (inochi) is 'life'.",
        difficulty = DifficultyLevel.HARD
    ),

    // DAILY_CHALLENGE - Thêm
    Question(
        id = 92,
        text = "Today's challenge: What is the Japanese word for 'no'?",
        options = listOf("はい", "いいえ", "ありがとう", "さようなら"),
        correctAnswer = 1,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'いいえ' (iie) means 'no' in Japanese.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 93,
        text = "Today's challenge: Unscramble Y-A-M-A to form a Japanese word for 'mountain':",
        options = listOf("Yama", "Maya", "Ayam", "Ayma"),
        correctAnswer = 0,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'山' (yama) means 'mountain' in Japanese.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 94,
        text = "Today's challenge: What is the polite negative form of 'to understand' (分かる) in present tense?",
        options = listOf("分かります", "分かりません", "分かっています", "分かれます"),
        correctAnswer = 1,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'分かりません' (wakarimasen) is the polite negative form of 'to understand', meaning 'I don’t understand'. '分かります' is plain polite, '分かっています' is continuous, '分かれます' is passive.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 95,
        text = "Today's challenge: Match the English word to its Japanese meaning: 'Moon'",
        options = listOf("太陽", "星", "月", "雲"),
        correctAnswer = 2,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'月' (tsuki) means 'moon'. '太陽' (taiyou) is 'sun', '星' (hoshi) is 'star', '雲' (kumo) is 'cloud'.",
        difficulty = DifficultyLevel.EASY
    ),
    Question(
        id = 96,
        text = "Today's challenge: Unscramble M-O-R-I to form a Japanese word for 'forest':",
        options = listOf("Mori", "Rimo", "Imor", "Oirm"),
        correctAnswer = 0,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'森' (mori) means 'forest' in Japanese.",
        difficulty = DifficultyLevel.MEDIUM
    ),
    Question(
        id = 97,
        text = "Today's challenge: What is the causative form of 'to walk' (歩く) in polite present tense?",
        options = listOf("歩きます", "歩かせます", "歩かれます", "歩きました"),
        correctAnswer = 1,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'歩かせます' (arukaremasu) is the polite causative form of 'to walk', meaning 'to make someone walk'. '歩きます' is plain polite, '歩かれます' is passive, '歩きました' is past.",
        difficulty = DifficultyLevel.HARD
    ),

    // HARD Questions - Thêm
    Question(
        id = 98,
        text = "What is the passive form of 'to teach' (教える) in polite present tense?",
        options = listOf("教えます", "教えられます", "教えました", "教えさせます"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'教えられます' (oshieraremasu) is the polite passive form, meaning 'is taught'. '教えます' is plain polite, '教えました' is past, '教えさせます' is causative.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 99,
        text = "Unscramble G-E-I-J-U-T-S-U to form a Japanese word for 'art':",
        options = listOf("Geijutsu", "Jutsugei", "Tsugeiju", "Eigujts"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'芸術' (geijutsu) means 'art' in Japanese.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 100,
        text = "Match the English word to its Japanese meaning: 'Destiny'",
        options = listOf("運命", "偶然", "選択", "結果"),
        correctAnswer = 0,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'運命' (unmei) means 'destiny'. '偶然' (guuzen) is 'chance', '選択' (sentaku) is 'choice', '結果' (kekka) is 'result'.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 101,
        text = "Today's challenge: What is the provisional form of 'to swim' (泳ぐ) in polite present?",
        options = listOf("泳ぎます", "泳げば", "泳げばです", "泳いだら"),
        correctAnswer = 3,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'泳いだら' (oyoidara) is the provisional form, meaning 'if (I) swim'. '泳ぎます' is plain polite, '泳げば' is plain conditional, '泳げばです' is incorrect.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 102,
        text = "What is the Japanese word for 'tradition' in a cultural context?",
        options = listOf("習慣", "伝統", "改革", "変化"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'伝統' (dentou) means 'tradition'. '習慣' (shuukan) is 'habit', '改革' (kaikaku) is 'reform', '変化' (henka) is 'change'.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 103,
        text = "What is the te-form of 'to receive' (もらう) in polite present?",
        options = listOf("もらいます", "もらって", "もらってください", "もらられ"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'もらって' (moratte) is the te-form of 'to receive'. 'もらいます' is polite present, 'もらってください' is a request, 'もらられ' is incorrect.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 104,
        text = "Unscramble S-H-O-K-K-A-K-U to form a Japanese word for 'enlightenment':",
        options = listOf("Shokkaku", "Kakusho", "Hakkoku", "Sokukah"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'触覚' (shokkaku) means 'enlightenment' in a philosophical sense (simplified context).",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 105,
        text = "Match the English word to its Japanese meaning: 'Honor'",
        options = listOf("恥", "名誉", "罪", "罰"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'名誉' (meiyo) means 'honor'. '恥' (haji) is 'shame', '罪' (tsumi) is 'crime', '罰' (batsu) is 'punishment'.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 106,
        text = "Today's challenge: What is the progressive form of 'to run' (走る) in polite present?",
        options = listOf("走ります", "走っています", "走りました", "走られ"),
        correctAnswer = 1,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'走っています' (hashitte imasu) is the polite progressive form, meaning 'I am running'. '走ります' is plain polite, '走りました' is past, '走られ' is incomplete.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 107,
        text = "What is the Japanese word for 'beauty' in an aesthetic context?",
        options = listOf("美しさ", "醜さ", "普通", "平凡"),
        correctAnswer = 0,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'美しさ' (utsukushisa) means 'beauty'. '醜さ' (minikusa) is 'ugliness', '普通' (futsuu) is 'normal', '平凡' (heibon) is 'ordinary'.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 108,
        text = "Unscramble T-A-I-K-A-I to form a Japanese word for 'tournament':",
        options = listOf("Taikai", "Kaitai", "Itaka", "Aitak"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'大会' (taikai) means 'tournament' in Japanese.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 109,
        text = "Match the English word to its Japanese meaning: 'Faith'",
        options = listOf("不信", "信仰", "疑惑", "迷信"),
        correctAnswer = 1,
        gameMode = "MATCH",
        language = "JAPANESE",
        explanation = "'信仰' (shinkou) means 'faith'. '不信' (fushin) is 'distrust', '疑惑' (giwaku) is 'doubt', '迷信' (meishin) is 'superstition'.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 110,
        text = "Today's challenge: What is the concessive form of 'to laugh' (笑う) in polite present?",
        options = listOf("笑います", "笑えば", "笑っても", "笑っています"),
        correctAnswer = 2,
        gameMode = "DAILY_CHALLENGE",
        language = "JAPANESE",
        explanation = "'笑っても' (warattemo) is the concessive form, meaning 'even if (I) laugh'. '笑います' is plain polite, '笑えば' is conditional, '笑っています' is progressive.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 111,
        text = "What is the Japanese word for 'strength' in a physical context?",
        options = listOf("弱さ", "力", "知恵", "勇気"),
        correctAnswer = 1,
        gameMode = "QUIZ",
        language = "JAPANESE",
        explanation = "'力' (chikara) means 'strength'. '弱さ' (yowasa) is 'weakness', '知恵' (chie) is 'wisdom', '勇気' (yuuki) is 'courage'.",
        difficulty = DifficultyLevel.HARD
    ),
    Question(
        id = 112,
        text = "Unscramble H-A-N-A-B-I to form a Japanese word for 'fireworks':",
        options = listOf("Hanabi", "Bihan", "Nabiah", "Habina"),
        correctAnswer = 0,
        gameMode = "SCRAMBLE",
        language = "JAPANESE",
        explanation = "'花火' (hanabi) means 'fireworks' in Japanese.",
        difficulty = DifficultyLevel.HARD
    ),
)