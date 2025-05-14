package com.example.deepsea.utils

import com.example.deepsea.R
import com.example.deepsea.data.model.question.QuizQuestion
import com.example.deepsea.ui.viewmodel.learn.VocabularyItem
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import timber.log.Timber

class QuizResponseParser {

    private val gson = Gson()

    /**
     * Parse raw JSON string into QuizQuestion objects
     */
    fun parseQuizQuestions(jsonString: String): List<QuizQuestion> {
        return try {
            val listType = object : TypeToken<List<QuizQuestion>>() {}.type
            gson.fromJson(jsonString, listType)
        } catch (e: JsonSyntaxException) {
            Timber.e(e, "Error parsing JSON: $jsonString")
            emptyList()
        }
    }

    /**
     * Convert QuizQuestion objects to VocabularyItem objects
     */
    fun convertToVocabularyItems(quizQuestions: List<QuizQuestion>): List<VocabularyItem> {
        return quizQuestions.mapNotNull { question ->
            try {
                // Get the correct answer from the question
                val correctOption = question.correctAnswer
                val englishText = correctOption.languageContent["en"]?.text ?: return@mapNotNull null
                val japaneseText = correctOption.languageContent["ja"]?.text ?: return@mapNotNull null
                val pronunciation = correctOption.languageContent["ja"]?.pronunciation ?: ""

                // Map image ID to actual resource ID
                val imageResId = mapImageIdToResourceId(correctOption.image)

                VocabularyItem(
                    id = question.id,
                    native = japaneseText,
                    romaji = pronunciation,
                    english = englishText,
                    imageResId = imageResId
                )
            } catch (e: Exception) {
                Timber.e(e, "Error converting quiz question to vocabulary item")
                null
            }
        }
    }

    /**
     * Map image IDs from API to local resource IDs
     */
    private fun mapImageIdToResourceId(imageId: Int): Int {
        return when (imageId) {
            // 🍚 Food & Drinks (1000s)
            1001 -> R.drawable.ic_rice
            1002 -> R.drawable.ic_sushi
            1003 -> R.drawable.ic_water
            1004 -> R.drawable.ic_drinks
            1005 -> R.drawable.ic_bread
            1006 -> R.drawable.ic_meat
            1007 -> R.drawable.ic_milk
            1008 -> R.drawable.ic_fruit
            1009 -> R.drawable.ic_vegetables
            1010 -> R.drawable.ic_snack
            1011 -> R.drawable.ic_juice
            1012 -> R.drawable.ic_tea
            1013 -> R.drawable.ic_coffee
            1014 -> R.drawable.ic_cake
            1015 -> R.drawable.ic_icecream

            // 🐾 Animals (2000s)
            2001 -> R.drawable.ic_dog
            2002 -> R.drawable.ic_cat
            2003 -> R.drawable.ic_bird
            2004 -> R.drawable.ic_fish
            2005 -> R.drawable.ic_cow
            2006 -> R.drawable.ic_horse
            2007 -> R.drawable.ic_sheep
            2008 -> R.drawable.ic_pig
            2009 -> R.drawable.ic_rabbit

            // 📚 Objects (3000s)
            3001 -> R.drawable.ic_book
            3002 -> R.drawable.ic_pen
            3003 -> R.drawable.ic_chair
            3004 -> R.drawable.ic_table
            3005 -> R.drawable.ic_bag
            3006 -> R.drawable.ic_clock
            3007 -> R.drawable.ic_phone
            3008 -> R.drawable.ic_computer
            3009 -> R.drawable.ic_tv

            // 🏙️ Places (4000s)
            4001 -> R.drawable.ic_school
            4002 -> R.drawable.ic_home
            4003 -> R.drawable.ic_hospital
            4004 -> R.drawable.ic_store
            4005 -> R.drawable.ic_park

            // 🚗 Transport (5000s)
            5001 -> R.drawable.ic_car
            5002 -> R.drawable.ic_bus
            5003 -> R.drawable.ic_train
            5004 -> R.drawable.ic_airplane
            5005 -> R.drawable.ic_bicycle
            5006 -> R.drawable.ic_boat

            // 🎨 Colors (6000s)
            6001 -> R.drawable.ic_red
            6002 -> R.drawable.ic_blue
            6003 -> R.drawable.ic_yellow
            6004 -> R.drawable.ic_green
            6005 -> R.drawable.ic_black
            6006 -> R.drawable.ic_white
            6007 -> R.drawable.ic_pink
            6008 -> R.drawable.ic_orange
            6009 -> R.drawable.ic_purple

            // 👕 Clothes (7000s)
            7001 -> R.drawable.ic_shirt
            7002 -> R.drawable.ic_pants
            7003 -> R.drawable.ic_hat
            7004 -> R.drawable.ic_shoes
            7005 -> R.drawable.ic_dress
            7006 -> R.drawable.ic_socks
            7007 -> R.drawable.ic_jacket

            // 😊 Emotions (8000s)
            8001 -> R.drawable.ic_happy
            8002 -> R.drawable.ic_sad
            8003 -> R.drawable.ic_angry
            8004 -> R.drawable.ic_surprised
            8005 -> R.drawable.ic_sleepy
            8006 -> R.drawable.ic_love

            // 🌤️ Weather (9000s)
            9001 -> R.drawable.ic_sunny
            9002 -> R.drawable.ic_rainy
            9003 -> R.drawable.ic_cloudy
            9004 -> R.drawable.ic_snow
            9005 -> R.drawable.ic_wind
            9006 -> R.drawable.ic_thunder

            // 🏃 Verbs (10000s)
            10001 -> R.drawable.ic_run
            10002 -> R.drawable.ic_eat
            10003 -> R.drawable.ic_sleep
            10004 -> R.drawable.ic_read
            10005 -> R.drawable.ic_write
            10006 -> R.drawable.ic_jump
            10007 -> R.drawable.ic_walk
            10008 -> R.drawable.ic_drinks

            else -> R.drawable.avatar_placeholder // Fallback
        }
    }


    /**
     * Process hardcoded JSON from logs when API fails
     */
    fun processHardcodedLogResponse(): List<VocabularyItem> {
        val hardcodedJson = """
            [{"id":2,"type":"IMAGE_SELECTION","prompt":"Choose the correct translation for 'sushi'","lessonId":1,"languageContent":{"en":{"text":"Choose the correct image for sushi","pronunciation":""},"ja":{"text":"sushiの正しい画像を選んでください","pronunciation":""}},"options":[{"id":2,"image":1002,"languageContent":{"en":{"text":"sushi","pronunciation":""},"ja":{"text":"すし","pronunciation":"sushi"}}}],"correctAnswer":{"id":2,"image":1002,"languageContent":{"en":{"text":"sushi","pronunciation":""},"ja":{"text":"すし","pronunciation":"sushi"}}}},{"id":3,"type":"IMAGE_SELECTION","prompt":"Choose the correct translation for 'water'","lessonId":1,"languageContent":{"en":{"text":"Choose the correct image for water","pronunciation":""},"ja":{"text":"waterの正しい画像を選んでください","pronunciation":""}},"options":[{"id":3,"image":1003,"languageContent":{"en":{"text":"water","pronunciation":""},"ja":{"text":"みず","pronunciation":"mizu"}}}],"correctAnswer":{"id":3,"image":1003,"languageContent":{"en":{"text":"water","pronunciation":""},"ja":{"text":"みず","pronunciation":"mizu"}}}},{"id":4,"type":"IMAGE_SELECTION","prompt":"Choose the correct translation for 'green tea'","lessonId":1,"languageContent":{"en":{"text":"Choose the correct image for green tea","pronunciation":""},"ja":{"text":"green teaの正しい画像を選んでください","pronunciation":""}},"options":[{"id":4,"image":1004,"languageContent":{"en":{"text":"green tea","pronunciation":""},"ja":{"text":"おちゃ","pronunciation":"ocha"}}}],"correctAnswer":{"id":4,"image":1004,"languageContent":{"en":{"text":"green tea","pronunciation":""},"ja":{"text":"おちゃ","pronunciation":"ocha"}}}}]
        """.trimIndent()

        val quizQuestions = parseQuizQuestions(hardcodedJson)
        return convertToVocabularyItems(quizQuestions)
    }
}