package com.example.mimascota.service

import com.example.mimascota.model.CartItem
import com.example.mimascota.model.ApiResponse
import com.example.mimascota.util.AppConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class CartSyncService {

    private val client = OkHttpClient()
    private val gson = Gson()

    // Definir el tipo para la respuesta de la API que contiene una lista de CartItem
    private val cartItemListType = object : TypeToken<ApiResponse<List<CartItem>>>() {}.type

    /**
     * Sincroniza el carrito con el backend.
     */
    suspend fun sincronizarCarrito(userId: Int, items: List<CartItem>): ApiResponse<Unit> {
        // Usar AppConfig para construir URL (unificada)
        val baseOrigin = AppConfig.BASE_ORIGIN
        val url = "$baseOrigin/api/cart/sync/$userId"
        val json = gson.toJson(items)
        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder().url(url).post(requestBody).build()

        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    ApiResponse(success = true, data = Unit)
                } else {
                    ApiResponse(success = false, message = "Error: ${response.code}")
                }
            }
        } catch (e: IOException) {
            ApiResponse(success = false, message = e.message)
        }
    }

    /**
     * Obtiene el carrito del backend.
     */
    suspend fun obtenerCarrito(userId: Int): ApiResponse<List<CartItem>> {
        val baseOrigin = AppConfig.BASE_ORIGIN
        val url = "$baseOrigin/api/cart/$userId"
        val request = Request.Builder().url(url).build()

        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val json = response.body?.string()
                    if (json != null) {
                        gson.fromJson(json, cartItemListType)
                    } else {
                        ApiResponse(success = false, message = "Respuesta vacía del servidor")
                    }
                } else {
                    ApiResponse(success = false, message = "Error: ${response.code}")
                }
            }
        } catch (e: IOException) {
            ApiResponse(success = false, message = e.message)
        }
    }
}
