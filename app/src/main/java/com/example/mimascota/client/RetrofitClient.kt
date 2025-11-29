package com.example.mimascota.client

import android.content.Context
import android.util.Log
import com.example.mimascota.service.ApiService
import com.example.mimascota.util.TokenManager
import com.example.mimascota.util.AppConfig
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val TAG = "RetrofitClient"
    private val BASE_URL = AppConfig.BASE_URL
    private var onUnauthorized: (() -> Unit)? = null

    fun init(context: Context, onUnauthorizedCallback: (() -> Unit)? = null) {
        TokenManager.init(context.applicationContext)
        onUnauthorized = onUnauthorizedCallback
        Log.d(TAG, "RetrofitClient inicializado en $BASE_URL")
    }

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        if (AppConfig.isLoggingEnabled) Log.d("OkHttp", message)
    }.apply {
        level = if (AppConfig.isLoggingEnabled) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = if (TokenManager.isLoggedIn()) TokenManager.getToken() else null
        val newRequest = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder().header("Authorization", "Bearer $token").build()
        } else {
            originalRequest
        }
        val response = chain.proceed(newRequest)
        if (response.code == 401) {
            Log.e(TAG, "401 Unauthorized - Token inválido.")
            TokenManager.logout()
            onUnauthorized?.invoke()
        }
        response
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    // Instancia única y consolidada del ApiService
    val apiService: ApiService by lazy { retrofit.create(ApiService::class.java) }

    fun getTokenManager(): TokenManager = TokenManager
}
