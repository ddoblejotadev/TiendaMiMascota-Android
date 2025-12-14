
package com.example.mimascota.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log

object ImageUtils {
    fun decodeBase64ToBitmap(base64Str: String?): Bitmap? {
        if (base64Str.isNullOrBlank()) {
            return null
        }

        return try {
            // Handle both with and without "data:image/jpeg;base64," prefix
            val base64Data = if (base64Str.contains(',')) {
                base64Str.substringAfter("base64,", "")
            } else {
                base64Str
            }

            if (base64Data.isNotEmpty()) {
                val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } else {
                null
            }
        } catch (e: IllegalArgumentException) {
            // This is expected if the string is a URL, not a Base64 string.
            // Log.d("ImageUtils", "Not a valid Base64 string.")
            null
        } catch (e: Exception) {
            Log.e("ImageUtils", "An unexpected error occurred during Base64 decoding: ${e.message}")
            null
        }
    }
}
