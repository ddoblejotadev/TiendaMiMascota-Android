package com.example.mimascota.client

import com.example.mimascota.service.HuachitosApiService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object HuachitosRetrofitClient {

    private const val BASE_URL = "https://huachitos.cl/api/"

    // Interceptor para ver los logs de las peticiones (muy útil para depurar)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Instancia de Retrofit para la API de Huachitos
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    // Instancia del servicio que usaremos para hacer las llamadas
    val apiService: HuachitosApiService by lazy {
        retrofit.create(HuachitosApiService::class.java)
    }
}
