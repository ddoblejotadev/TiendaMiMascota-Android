package com.example.mimascota.util

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.mimascota.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.util.Log

@Suppress("unused")
object ImageLoader {

    private const val TAG = "ImageLoader"

    /**
     * Carga una imagen en un ImageView usando Coil
     */
    fun loadImage(
        context: Context,
        imageUrl: String?,
        imageView: ImageView,
        placeholder: Int = R.drawable.placeholder_product,
        errorDrawable: Int = R.drawable.placeholder_product
    ) {
        // Corregido: Usar AppConfig para la URL base
        val fullUrl = AppConfig.toAbsoluteImageUrl(imageUrl) ?: ""

        val request = ImageRequest.Builder(context)
            .data(fullUrl)
            .target(imageView)
            .placeholder(placeholder)
            .error(errorDrawable)
            .build()
        context.imageLoader.enqueue(request)
    }

    /**
     * Descarga una imagen y la guarda en el almacenamiento interno
     */
    @Suppress("unused")
    suspend fun downloadAndSaveImage(
        context: Context,
        imageUrl: String
    ): File? = withContext(Dispatchers.IO) {
        try {
            val fullUrl = AppConfig.toAbsoluteImageUrl(imageUrl) ?: imageUrl
            val request = ImageRequest.Builder(context).data(fullUrl).build()
            val result = (context.imageLoader.execute(request) as? SuccessResult)?.drawable

            if (result is BitmapDrawable) {
                val bitmap = result.bitmap
                val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
                FileOutputStream(file).use {
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, it)
                }
                file
            } else {
                null
            }
        } catch (e: IOException) {
            Log.w(TAG, "Error descargando imagen: ${e.message}")
            null
        }
    }

    /**
     * Carga una imagen desde un File en un ImageView
     */
    @Suppress("unused")
    fun loadFileImage(
        context: Context,
        file: File,
        imageView: ImageView,
        placeholder: Int = R.drawable.placeholder_product,
        errorDrawable: Int = R.drawable.placeholder_product
    ) {
        val request = ImageRequest.Builder(context)
            .data(file)
            .target(imageView)
            .placeholder(placeholder)
            .error(errorDrawable)
            .build()
        context.imageLoader.enqueue(request)
    }
}
