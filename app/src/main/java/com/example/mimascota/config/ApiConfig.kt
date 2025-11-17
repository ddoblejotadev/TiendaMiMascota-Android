package com.example.mimascota.config

import android.util.Log
import com.example.mimascota.service.ApiService
import com.example.mimascota.util.TokenManager
import com.example.mimascota.util.AppConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * ApiConfig: Configuración centralizada para Retrofit con autenticación JWT
 *
 * URLs configuradas:
 * - Desarrollo: http://10.0.2.2:8080/api/ (localhost desde emulador)
 * - Producción: https://tiendamimascotabackends.onrender.com/api/
 *
 * Cambia entre entornos en AppConfig.USE_PRODUCTION
 */
object ApiConfig {

    /**
     * Interceptor para agregar token JWT a todas las peticiones
     */
    private fun createAuthInterceptor(): Interceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        // Obtener token del TokenManager
        val token = TokenManager.getToken()

        val requestBuilder = originalRequest.newBuilder()

        // Si hay token, agregarlo al header Authorization
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
            Log.d("ApiConfig", "🔐 Token JWT agregado a la petición")
        } else {
            Log.d("ApiConfig", "⚠️ Sin token JWT - petición anónima")
        }

        // Agregar headers adicionales
        requestBuilder.addHeader("Content-Type", "application/json")
        requestBuilder.addHeader("Accept", "application/json")

        val newRequest = requestBuilder.build()

        Log.d("ApiConfig", "🌐 Request: ${newRequest.method} ${newRequest.url}")

        chain.proceed(newRequest)
    }

    /**
     * Logging interceptor para debug
     */
    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            if (AppConfig.isLoggingEnabled) {
                Log.d("OkHttp", message)
            }
        }.apply {
            level = if (AppConfig.isLoggingEnabled) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.BASIC
            }
        }
    }

    /**
     * Cliente HTTP con interceptores
     * Timeout 30s para Render (puede tardar en despertar)
     */
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(createAuthInterceptor())
        .addInterceptor(createLoggingInterceptor())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Instancia de Retrofit
     * Usa AppConfig.BASE_URL que cambia según el entorno
     */
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(AppConfig.BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    init {
        Log.d("ApiConfig", AppConfig.getConfigInfo())
    }

    /**
     * Convierte una ruta relativa o URL a una URL absoluta válida usando AppConfig.BASE_URL
     */
    fun toAbsoluteImageUrl(pathOrUrl: String?): String? {
        if (pathOrUrl.isNullOrBlank()) return null
        if (pathOrUrl.startsWith("http://", ignoreCase = true) || pathOrUrl.startsWith("https://", ignoreCase = true)) {
            return pathOrUrl
        }
        val baseOrigin = AppConfig.BASE_URL.substringBefore("/api/")
        val cleanPath = pathOrUrl.trimStart('/')
        return "$baseOrigin/$cleanPath"
    }
}

/**
 * ApiClient: Singleton para acceder a ApiService
 */
object ApiClient {
    val apiService: ApiService by lazy {
        ApiConfig.retrofit.create(ApiService::class.java)
    }
}
