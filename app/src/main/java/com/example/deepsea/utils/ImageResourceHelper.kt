package com.example.deepsea.utils

import android.content.Context
import android.util.Log

// Add this new utility class for image handling
class ImageResourceResolver(private val context: Context) {
    // Cache for resolved image resources to avoid repeated lookups
    private val imageResourceCache = mutableMapOf<String, Int>()

    /**
     * Resolves an image name from the API to a drawable resource ID
     *
     * @param imageName The name of the image from the API (e.g., "ic_handshake")
     * @param defaultResId Fallback resource ID if resolution fails
     * @return The resolved drawable resource ID
     */
    fun resolveImageResource(imageName: String?, defaultResId: Int): Int {
        // Handle null or empty image names
        if (imageName.isNullOrBlank()) {
            Log.d("ImageResolver", "Empty image name, using default")
            return defaultResId
        }

        // Check if we've already resolved this image name
        imageResourceCache[imageName]?.let {
            return it
        }

        // Try to resolve the image name to a resource ID
        val resolvedId = context.resources.getIdentifier(imageName, "drawable", context.packageName)

        return if (resolvedId != 0) {
            // Cache the result for future use
            imageResourceCache[imageName] = resolvedId
            Log.d("ImageResolver", "Successfully resolved image: $imageName to resource ID: $resolvedId")
            resolvedId
        } else {
            Log.w("ImageResolver", "Failed to resolve image: $imageName, using default")
            defaultResId
        }
    }

    /**
     * Preload common images to avoid delays later
     */
    fun preloadCommonImages(imageNames: List<String>, defaultResId: Int) {
        for (name in imageNames) {
            resolveImageResource(name, defaultResId)
        }
    }
}