package com.example.deepsea.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/" // Android emulator localhost

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    internal val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val authApi: ApiService = retrofit.create(ApiService::class.java)
    val userProfileService: UserProfileService = retrofit.create(UserProfileService::class.java)
    val lessonApi: LessonApi by lazy {
        retrofit.create(LessonApi::class.java)
    }
    val vocabularyApiService: VocabularyApiService by lazy {
        retrofit.create(VocabularyApiService::class.java)
    }
    val courseApiService: CourseApiService by lazy {
        retrofit.create(CourseApiService::class.java)
    }
    val unitGuideService: UnitGuideService by lazy {
        retrofit.create(UnitGuideService::class.java)
    }
    val sessionApiService: SessionApiService by lazy {
        retrofit.create(SessionApiService::class.java)
    }

    val learningApiService: LearningApiService by lazy {
        retrofit.create(LearningApiService::class.java)
    }

    val gameApiService: GameApiService by lazy {
        retrofit.create(GameApiService::class.java)
    }

    val hearingService: HearingService by lazy {
        retrofit.create(HearingService::class.java)
    }

    val wordBuildingService: WordBuildingService by lazy {
        retrofit.create(WordBuildingService::class.java)
    }

    val friendSuggestionService: FriendSuggestionService by lazy {
        retrofit.create(FriendSuggestionService::class.java)
    }

    val mistakeApiService: MistakeApiService by lazy {
        retrofit.create(MistakeApiService::class.java)
    }

    val wordApiService: WordApiService by lazy {
        retrofit.create(WordApiService::class.java)
    }

    val storyApiService: StoryApiService by lazy {
        retrofit.create(StoryApiService::class.java)
    }

    val paymentService: PaymentService by lazy {
        retrofit.create(PaymentService::class.java)
    }

    val shadowListeningApiService: ShadowListeningApiService by lazy {
        retrofit.create(ShadowListeningApiService::class.java)
    }
}
