package com.example.deepsea.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

class CloudinaryUploadService {
    // Cloudinary configuration
    private val cloudName = "da8vrvx03" // Replace with your cloud name
    private val uploadPreset = "deepsea" // Replace with your unsigned upload preset

    // For unsigned uploads, we don't need to send the API key
    // private val apiKey = "fVmAYi0UgRkHR38QEhVRxId8Wm0"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun uploadImage(context: Context, imageUri: Uri): String {
        return withContext(Dispatchers.IO) {
            try {
                // Convert URI to File
                val file = createTempFileFromUri(context, imageUri)
                    ?: throw IOException("Failed to create temp file")

                // Create the upload URL
                val uploadUrl = "https://api.cloudinary.com/v1_1/$cloudName/image/upload"

                // Create request body
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        file.name,
                        file.asRequestBody(context.contentResolver.getType(imageUri)?.toMediaTypeOrNull() ?: "image/*".toMediaTypeOrNull())
                    )
                    .addFormDataPart("upload_preset", uploadPreset)
                    // For unsigned uploads, we only need the upload_preset, not the API key
                    // .addFormDataPart("api_key", apiKey)
                    .build()

                // Create request
                val request = Request.Builder()
                    .url(uploadUrl)
                    .post(requestBody)
                    .build()

                // Execute request and handle response
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string()
                        Log.e("CloudinaryUpload", "Error response: $errorBody")
                        throw IOException("Unexpected response code: ${response.code}, Response: $errorBody")
                    }

                    val responseBody = response.body?.string()
                    val jsonObject = JSONObject(responseBody)

                    // Get the secure URL of the uploaded image
                    val secureUrl = jsonObject.getString("secure_url")
                    Log.d("CloudinaryUpload", "Image uploaded successfully: $secureUrl")

                    // Clean up temporary file
                    file.delete()

                    secureUrl
                }
            } catch (e: Exception) {
                Log.e("CloudinaryUpload", "Upload failed", e)
                throw e
            }
        }
    }

    private fun createTempFileFromUri(context: Context, uri: Uri): File? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileExtension = getFileExtension(context, uri)
            val tempFile = File.createTempFile("upload_", ".$fileExtension", context.cacheDir)

            FileOutputStream(tempFile).use { outputStream ->
                inputStream.use { input ->
                    input.copyTo(outputStream)
                }
            }

            return tempFile
        } catch (e: Exception) {
            Log.e("CloudinaryUpload", "Error creating temp file", e)
            return null
        }
    }

    private fun getFileExtension(context: Context, uri: Uri): String {
        val contentResolver = context.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ?: "jpg"
    }
}