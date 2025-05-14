package com.example.deepsea.utils

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.repository.QuestionFactoryImpl
import com.example.deepsea.data.repository.VocabularyRepository
import com.example.deepsea.ui.viewmodel.learn.LearningViewModel
import timber.log.Timber

/**
 * JsonLogImporter - Công cụ tiện ích để nhập dữ liệu từ log trong quá trình phát triển
 * Lớp này chỉ nên sử dụng trong bản debug
 */
class JsonLogImporter(private val context: Context) {

    /**
     * Xử lý dữ liệu JSON từ log và áp dụng vào ViewModel hiện tại
     */
    fun importJsonFromLog(jsonLog: String, viewModelStoreOwner: ViewModelStoreOwner) {
        try {
            // Trích xuất phần JSON từ dòng log
            val jsonStart = jsonLog.indexOf("[{")
            val jsonEnd = jsonLog.lastIndexOf("}]") + 2

            if (jsonStart == -1 || jsonEnd == -1 || jsonEnd <= jsonStart) {
                showToast("Invalid JSON format")
                Timber.e("Invalid JSON format in log line")
                return
            }

            val jsonString = jsonLog.substring(jsonStart, jsonEnd)
            Timber.d("Extracted JSON: $jsonString")

            // Tạo repository
            val questionFactory = QuestionFactoryImpl()
            val repository = VocabularyRepository(
                RetrofitClient.vocabularyApiService,
                questionFactory
            )

            // Kiểm tra dữ liệu
            val vocabularyItems = repository.processRawQuizResponse(jsonString)
            if (vocabularyItems.isEmpty()) {
                showToast("No valid vocabulary items found in JSON")
                return
            }

            showToast("Found ${vocabularyItems.size} vocabulary items")

            // Lấy ViewModel hiện tại và áp dụng dữ liệu
            val viewModel = ViewModelProvider(viewModelStoreOwner)
                .get(LearningViewModel::class.java)

            viewModel.processRawJson(jsonString)
            showToast("JSON data loaded successfully")

        } catch (e: Exception) {
            Timber.e(e, "Error importing JSON from log")
            showToast("Error: ${e.message}")
        }
    }

    /**
     * Xử lý dữ liệu JSON được hardcode và áp dụng vào ViewModel hiện tại
     */
    fun importHardcodedJson(viewModelStoreOwner: ViewModelStoreOwner) {
        try {
            val hardcodedJson = """[{"id":2,"type":"IMAGE_SELECTION","prompt":"Choose the correct translation for 'sushi'","lessonId":1,"languageContent":{"en":{"text":"Choose the correct image for sushi","pronunciation":""},"ja":{"text":"sushiの正しい画像を選んでください","pronunciation":""}},"options":[{"id":2,"image":1002,"languageContent":{"en":{"text":"sushi","pronunciation":""},"ja":{"text":"すし","pronunciation":"sushi"}}}],"correctAnswer":{"id":2,"image":1002,"languageContent":{"en":{"text":"sushi","pronunciation":""},"ja":{"text":"すし","pronunciation":"sushi"}}}},{"id":3,"type":"IMAGE_SELECTION","prompt":"Choose the correct translation for 'water'","lessonId":1,"languageContent":{"en":{"text":"Choose the correct image for water","pronunciation":""},"ja":{"text":"waterの正しい画像を選んでください","pronunciation":""}},"options":[{"id":3,"image":1003,"languageContent":{"en":{"text":"water","pronunciation":""},"ja":{"text":"みず","pronunciation":"mizu"}}}],"correctAnswer":{"id":3,"image":1003,"languageContent":{"en":{"text":"water","pronunciation":""},"ja":{"text":"みず","pronunciation":"mizu"}}}},{"id":4,"type":"IMAGE_SELECTION","prompt":"Choose the correct translation for 'green tea'","lessonId":1,"languageContent":{"en":{"text":"Choose the correct image for green tea","pronunciation":""},"ja":{"text":"green teaの正しい画像を選んでください","pronunciation":""}},"options":[{"id":4,"image":1004,"languageContent":{"en":{"text":"green tea","pronunciation":""},"ja":{"text":"おちゃ","pronunciation":"ocha"}}}],"correctAnswer":{"id":4,"image":1004,"languageContent":{"en":{"text":"green tea","pronunciation":""},"ja":{"text":"おちゃ","pronunciation":"ocha"}}}}]"""

            // Lấy ViewModel hiện tại và áp dụng dữ liệu
            val viewModel = ViewModelProvider(viewModelStoreOwner)
                .get(LearningViewModel::class.java)

            viewModel.processRawJson(hardcodedJson)
            showToast("Hardcoded JSON data loaded successfully")

        } catch (e: Exception) {
            Timber.e(e, "Error importing hardcoded JSON")
            showToast("Error: ${e.message}")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}