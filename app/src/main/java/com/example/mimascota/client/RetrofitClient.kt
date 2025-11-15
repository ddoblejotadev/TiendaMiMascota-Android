package com.example.mimascota.client

import android.content.Context
import com.example.mimascota.service.AuthService
import com.example.mimascota.service.ProductoService
import com.example.mimascota.service.CartSyncService
import com.example.mimascota.service.CheckoutService
import com.example.mimascota.util.TokenManager
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
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
 * - JWT Interceptor: Agrega token a todas las peticiones
 * - Manejo de 401 Unauthorized (logout automático)
 * - Logging interceptor para debugging
 * - Timeouts configurados
 * - Conversor Gson para serialización JSON
 */
object RetrofitClient {

    // URL base del backend Spring Boot (10.0.2.2 es localhost para el emulador Android)
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    private lateinit var tokenManager: TokenManager
    private var onUnauthorized: (() -> Unit)? = null

    /**
     * Inicializa RetrofitClient con el contexto de la aplicación
     * Debe llamarse desde Application.onCreate() o antes de usar los servicios
     */
    fun init(context: Context, onUnauthorizedCallback: (() -> Unit)? = null) {
        tokenManager = TokenManager(context.applicationContext)
        onUnauthorized = onUnauthorizedCallback
    }

    // Logging interceptor para ver las peticiones y respuestas en Logcat
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * JWT Interceptor: Agrega el token de autenticación a todas las peticiones
     */
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        // Obtener token del TokenManager
        val token = if (::tokenManager.isInitialized) {
            tokenManager.getToken()
        } else {
            null
        }

        // Si hay token, agregarlo al header Authorization
        val newRequest = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        // Ejecutar petición
        val response = chain.proceed(newRequest)

        // Si respuesta es 401 Unauthorized, hacer logout y notificar
        if (response.code == 401) {
            if (::tokenManager.isInitialized) {
                tokenManager.logout()
            }
            onUnauthorized?.invoke()
        }

        response
    }

    // Cliente OkHttp con configuración de timeouts, auth interceptor y logging
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor) // PRIMERO el auth interceptor
        .addInterceptor(loggingInterceptor) // DESPUÉS el logging
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

    // Servicio de autenticación (lazy initialization)
    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    // Servicio de productos (lazy initialization)
    val productoService: ProductoService by lazy {
        retrofit.create(ProductoService::class.java)
    }

    // Servicio de sincronización de carrito (lazy initialization)
    val cartSyncService: CartSyncService by lazy {
        retrofit.create(CartSyncService::class.java)
    }

    // Servicio de checkout y órdenes (lazy initialization)
    val checkoutService: CheckoutService by lazy {
        retrofit.create(CheckoutService::class.java)
    }

    /**
     * Obtiene la instancia de TokenManager
     */
    fun getTokenManager(): TokenManager {
        if (!::tokenManager.isInitialized) {
            throw IllegalStateException("RetrofitClient no ha sido inicializado. Llama a init() primero.")
        }
        return tokenManager
    }
}
