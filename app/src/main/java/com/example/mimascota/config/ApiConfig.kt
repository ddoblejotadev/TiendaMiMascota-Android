// kotlin
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

object ApiConfig {

    // Exponer BASE_URL y BASE_ORIGIN para compatibilidad con código existente
    val BASE_URL: String = AppConfig.BASE_URL
    val BASE_ORIGIN: String = BASE_URL.substringBefore("/api/")

    private fun createAuthInterceptor(): Interceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = TokenManager.getToken()
        val requestBuilder = originalRequest.newBuilder()

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
            Log.d("ApiConfig", "🔐 Token JWT agregado a la petición")
        } else {
            Log.d("ApiConfig", "⚠️ Sin token JWT - petición anónima")
        }

        requestBuilder.addHeader("Content-Type", "application/json")
        requestBuilder.addHeader("Accept", "application/json")

        val newRequest = requestBuilder.build()
        Log.d("ApiConfig", "🌐 Request: ${newRequest.method} ${newRequest.url}")
        chain.proceed(newRequest)
    }

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

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(createAuthInterceptor())
        .addInterceptor(createLoggingInterceptor())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    init {
        Log.d("ApiConfig", AppConfig.getConfigInfo())
    }

    fun toAbsoluteImageUrl(pathOrUrl: String?): String? {
        if (pathOrUrl.isNullOrBlank()) return null
        if (pathOrUrl.startsWith("http://", ignoreCase = true) || pathOrUrl.startsWith("https://", ignoreCase = true)) {
            return pathOrUrl
        }
        val cleanPath = pathOrUrl.trimStart('/')
        return "$BASE_ORIGIN/$cleanPath"
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