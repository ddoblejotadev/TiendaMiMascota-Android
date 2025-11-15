package com.example.mimascota.client

import com.example.mimascota.service.ProductoService
import com.example.mimascota.service.CartSyncService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * RetrofitClient: Singleton para gestionar la conexión con el backend Spring Boot
 *
 * Configuración:
 * - URL base: http://10.0.2.2:8080/api/ (para emulador Android)
 * - Logging interceptor para debugging
 * - Timeouts configurados
 * - Conversor Gson para serialización JSON
 */
object RetrofitClient {

    // URL base del backend Spring Boot (10.0.2.2 es localhost para el emulador Android)
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    // Logging interceptor para ver las peticiones y respuestas en Logcat
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente OkHttp con configuración de timeouts y logging
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Configuración de Gson para manejar valores null y fechas
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    // Instancia de Retrofit (lazy initialization)
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // Servicio de productos (lazy initialization)
    val productoService: ProductoService by lazy {
        retrofit.create(ProductoService::class.java)
    }

    // Servicio de sincronización de carrito (lazy initialization)
    val cartSyncService: CartSyncService by lazy {
        retrofit.create(CartSyncService::class.java)
    }
}
