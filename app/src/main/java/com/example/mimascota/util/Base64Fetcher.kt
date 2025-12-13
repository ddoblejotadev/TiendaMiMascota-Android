package com.example.mimascota.util

import android.util.Base64
import coil.ImageLoader
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import okio.buffer
import okio.source
import java.io.ByteArrayInputStream

class Base64Fetcher(
    private val data: String,
    private val options: Options
) : Fetcher {
    
    override suspend fun fetch(): FetchResult {
        val base64Data = data.substringAfter("base64,", "")
        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
        
        return SourceResult(
            source = ImageSource(
                source = ByteArrayInputStream(bytes).source().buffer(),
                context = options.context
            ),
            mimeType = when {
                data.contains("image/png") -> "image/png"
                data.contains("image/gif") -> "image/gif"
                data.contains("image/webp") -> "image/webp"
                else -> "image/jpeg"
            },
            dataSource = DataSource.MEMORY
        )
    }
    
    class Factory : Fetcher.Factory<String> {
        override fun create(data: String, options: Options, imageLoader: ImageLoader): Fetcher? {
            return if (data.startsWith("data:image", ignoreCase = true)) {
                Base64Fetcher(data, options)
            } else {
                null
            }
        }
    }
}