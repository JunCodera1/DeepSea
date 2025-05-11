package com.example.deepsea.data.api

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.collections.get
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CloudinaryUploadService {
    private val TAG = "CloudinaryUploadService"

    /**
     * Upload an image to Cloudinary from a Uri
     * @param context The application context
     * @param imageUri The Uri of the image to upload
     * @param folder Optional folder to store the image in Cloudinary
     * @return The URL of the uploaded image
     */
    suspend fun uploadImage(context: Context, imageUri: Uri, folder: String = "avatars"): String {
        try {
            // Convert Uri to File
            val file = uriToFile(context, imageUri)

            // Generate a unique public ID
            val publicId = "$folder/${UUID.randomUUID()}"

            return suspendCancellableCoroutine { continuation ->
                val requestId = MediaManager.get().upload(file.absolutePath)
                    .option("public_id", publicId)
                    .option("folder", folder)
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String) {
                            Log.d(TAG, "Upload started: $requestId")
                        }

                        override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                            val progress = (bytes * 100) / totalBytes
                            Log.d(TAG, "Upload progress: $progress%")
                        }

                        override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                            val secureUrl = resultData["secure_url"] as String
                            Log.d(TAG, "Upload success: $secureUrl")
                            continuation.resume(secureUrl)
                        }

                        override fun onError(requestId: String, error: ErrorInfo) {
                            Log.e(TAG, "Upload error: ${error.description}")
                            continuation.resumeWithException(Exception(error.description))
                        }

                        override fun onReschedule(requestId: String, error: ErrorInfo) {
                            Log.d(TAG, "Upload rescheduled: ${error.description}")
                        }
                    })
                    .dispatch()

                continuation.invokeOnCancellation {
                    MediaManager.get().cancelRequest(requestId)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading image: ${e.message}")
            throw e
        }
    }

    /**
     * Convert a Uri to a File
     * @param context The application context
     * @param uri The Uri to convert
     * @return The created File
     */
    private suspend fun uriToFile(context: Context, uri: Uri): File = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Failed to open input stream")

        val fileName = "temp_${System.currentTimeMillis()}.jpg"
        val outputFile = File(context.cacheDir, fileName)

        FileOutputStream(outputFile).use { outputStream ->
            val buffer = ByteArray(4 * 1024) // 4KB buffer
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.flush()
        }

        inputStream.close()
        return@withContext outputFile
    }
}