package com.example.mimascota.config

import android.util.Log
import com.example.mimascota.service.ApiService
import com.example.mimascota.util.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * ApiConfig: Configuración centralizada para Retrofit con autenticación JWT
 */
object ApiConfig {
    // URL base del backend en Render
    private const val BASE_URL = "https://tiendamimascotabackends.onrender.com/api/"

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
        }

        // Agregar headers adicionales
        requestBuilder.addHeader("Content-Type", "application/json")

        val newRequest = requestBuilder.build()
        chain.proceed(newRequest)
    }

    /**
     * Logging interceptor para debug
     */
    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            Log.d("OkHttp", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    /**
     * Cliente HTTP con interceptores
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
     */
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

/**
 * ApiClient: Singleton para acceder a ApiService
 */
object ApiClient {
    val apiService: ApiService by lazy {
        ApiConfig.retrofit.create(ApiService::class.java)
    }
}

