package com.example.mimascota.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mimascota.R

/**
 * ImageLoader: Utilidad centralizada para cargar imágenes desde el backend
 *
 * Usa Glide para cargar imágenes de manera eficiente con:
 * - Conversión automática de rutas relativas a URLs absolutas
 * - Placeholder mientras carga
 * - Error fallback si falla la carga
 * - Cache en memoria y disco
 * - Transiciones suaves
 */
object ImageLoader {

    /**
     * Carga una imagen en un ImageView desde una URL o ruta relativa
     *
     * @param imageView El ImageView donde se mostrará la imagen
     * @param pathOrUrl La ruta relativa ("/images/producto.jpg") o URL absoluta
     * @param placeholderRes El recurso drawable a mostrar mientras carga (opcional)
     * @param errorRes El recurso drawable a mostrar si falla la carga (opcional)
     */
    fun loadImage(
        imageView: ImageView,
        pathOrUrl: String?,
        placeholderRes: Int = R.drawable.placeholder_product,
        errorRes: Int = R.drawable.ic_error_image
    ) {
        // Convertir a URL absoluta
        val absoluteUrl = com.example.mimascota.config.ApiConfig.toAbsoluteImageUrl(pathOrUrl)

        if (absoluteUrl.isNullOrBlank()) {
            // Si no hay URL, mostrar placeholder
            imageView.setImageResource(errorRes)
            return
        }

        // Cargar con Glide
        Glide.with(imageView.context)
            .load(absoluteUrl)
            .placeholder(placeholderRes)
            .error(errorRes)
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache en disco
            .centerCrop() // Ajustar imagen manteniendo proporción
            .into(imageView)
    }

    /**
     * Carga una imagen circular (útil para avatares)
     */
    fun loadCircularImage(
        imageView: ImageView,
        pathOrUrl: String?,
        placeholderRes: Int = R.drawable.placeholder_product,
        errorRes: Int = R.drawable.ic_error_image
    ) {
        val absoluteUrl = com.example.mimascota.config.ApiConfig.toAbsoluteImageUrl(pathOrUrl)

        if (absoluteUrl.isNullOrBlank()) {
            imageView.setImageResource(errorRes)
            return
        }

        Glide.with(imageView.context)
            .load(absoluteUrl)
            .placeholder(placeholderRes)
            .error(errorRes)
            .circleCrop() // Recorte circular
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }

    /**
     * Pre-carga una imagen en segundo plano (útil para optimizar carga posterior)
     */
    fun preloadImage(imageView: ImageView, pathOrUrl: String?) {
        val absoluteUrl = com.example.mimascota.config.ApiConfig.toAbsoluteImageUrl(pathOrUrl)
        if (absoluteUrl.isNullOrBlank()) return

        Glide.with(imageView.context)
            .load(absoluteUrl)
            .preload()
    }
}
