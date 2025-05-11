package com.example.deepsea.config

//import com.cloudinary.android.MediaManager
//import android.content.Context
//import com.cloudinary.android.BuildConfig as CloudinaryBuildConfig
//import com.example.deepsea.BuildConfig as AppBuildConfig
//
//object CloudinaryConfig {
//    private var isInitialized = false
//
//    fun init(context: Context) {
//        if (!isInitialized) {
//            try {
//                val config = mapOf(
//                    "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
//                    "api_key" to BuildConfig.CLOUDINARY_API_KEY,
//                    "api_secret" to BuildConfig.CLOUDINARY_API_SECRET,
//                    "secure" to true
//                )
//                MediaManager.init(context, config)
//                isInitialized = true
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//}