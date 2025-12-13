package com.example.mimascota.view

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.mimascota.R

@Composable
fun ProductImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    key(imageUrl) {
        when {
            imageUrl.isNullOrBlank() -> {
                Image(
                    painter = painterResource(R.drawable.logo1),
                    contentDescription = contentDescription,
                    modifier = modifier,
                    contentScale = contentScale
                )
            }
            imageUrl.startsWith("data:image", ignoreCase = true) -> {
                val bitmap = remember(imageUrl) {
                    try {
                        val base64Data = imageUrl.substringAfter("base64,", "")
                        if (base64Data.isNotEmpty()) {
                            val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
                            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        } else null
                    } catch (e: Exception) {
                        Log.e("ProductImage", "Error decodificando Base64: ${e.message}")
                        null
                    }
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = contentDescription,
                        modifier = modifier,
                        contentScale = contentScale
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.logo1),
                        contentDescription = contentDescription,
                        modifier = modifier,
                        contentScale = contentScale
                    )
                }
            }
            else -> {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = contentDescription,
                    modifier = modifier,
                    contentScale = contentScale,
                    placeholder = painterResource(R.drawable.logo1),
                    error = painterResource(R.drawable.logo1)
                )
            }
        }
    }
}