package com.example.mimascota.client

import android.content.Context
import android.util.Log
import com.example.mimascota.BuildConfig
import com.example.mimascota.service.AuthService
import com.example.mimascota.service.ProductoService
import com.example.mimascota.service.CartSyncService
import com.example.mimascota.service.CheckoutService
import com.example.mimascota.util.TokenManager
import com.example.mimascota.util.AppConfig
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
 * - URL automática según build type (debug/release)
 * - Debug: http://10.0.2.2:8080/api/ (servidor local)
 * - Release: https://tiendamimascotabackends.onrender.com/api/ (producción)
 * - JWT Interceptor: Agrega token a todas las peticiones
 * - Manejo de 401 Unauthorized (logout automático)
 * - Logging interceptor para debugging
 * - Timeouts configurados (30s para Render)
 * - Conversor Gson para serialización JSON
 */
object RetrofitClient {

    private const val TAG = "RetrofitClient"

    // URL automática según entorno (usa AppConfig para mayor flexibilidad)
    private val BASE_URL = AppConfig.BASE_URL

    private var onUnauthorized: (() -> Unit)? = null

    /**
     * Inicializa RetrofitClient con el contexto de la aplicación
     * Debe llamarse desde Application.onCreate() o antes de usar los servicios
     */
    fun init(context: Context, onUnauthorizedCallback: (() -> Unit)? = null) {
        TokenManager.init(context.applicationContext)
        onUnauthorized = onUnauthorizedCallback

        Log.d(TAG, "==============================================")
        Log.d(TAG, "RetrofitClient inicializado")
        Log.d(TAG, AppConfig.getConfigInfo())
        Log.d(TAG, "==============================================")
    }

    // Logging interceptor MEJORADO para debugging
    private val loggingInterceptor = HttpLoggingInterceptor { message ->
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

    /**
     * JWT Interceptor: Agrega el token de autenticación a todas las peticiones
     */
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        // Obtener token del TokenManager
        val token = if (TokenManager.isLoggedIn()) {
            TokenManager.getToken()
        } else {
            null
        }

        // Log de petición saliente
        Log.d(TAG, "─────────────────────────────────────────")
        Log.d(TAG, "→ REQUEST: ${originalRequest.method} ${originalRequest.url}")
        Log.d(TAG, "→ Token presente: ${!token.isNullOrEmpty()}")

        // Si hay token, agregarlo al header Authorization
        val newRequest = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        // Ejecutar petición
        val startTime = System.currentTimeMillis()
        val response = chain.proceed(newRequest)
        val duration = System.currentTimeMillis() - startTime

        // Log de respuesta
        Log.d(TAG, "← RESPONSE: ${response.code} (${duration}ms)")

        // Si respuesta es 401 Unauthorized, hacer logout y notificar
        if (response.code == 401) {
            Log.e(TAG, "⚠️ 401 Unauthorized - Token expirado o inválido")
            TokenManager.logout()
            onUnauthorized?.invoke()
        }

        Log.d(TAG, "─────────────────────────────────────────")

        response
    }

    // Cliente OkHttp con configuración de timeouts, auth interceptor y logging
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor) // PRIMERO el auth interceptor
        .addInterceptor(loggingInterceptor) // DESPUÉS el logging
        .connectTimeout(30, TimeUnit.SECONDS) // Timeout para Render (wake up time)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true) // Reintentar si falla la conexión
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
    fun getTokenManager(): TokenManager = TokenManager

    /**
     * Función de test de conectividad con el backend
     * Útil para verificar si el servidor está despierto
     *
     * Uso:
     * ```
     * lifecycleScope.launch {
     *     RetrofitClient.testConectividad { success, message ->
     *         Log.d("TEST", "Backend: $message")
     *     }
     * }
     * ```
     */
    suspend fun testConectividad(callback: (Boolean, String) -> Unit) {
        try {
            Log.d(TAG, "🔄 Probando conectividad con backend...")
            Log.d(TAG, "URL: $BASE_URL")

            val startTime = System.currentTimeMillis()
            val response = productoService.getAllProductos()
            val duration = System.currentTimeMillis() - startTime

            if (response.isSuccessful) {
                val productos = response.body()
                Log.d(TAG, "✅ Backend conectado (${duration}ms)")
                Log.d(TAG, "Productos encontrados: ${productos?.size ?: 0}")
                callback(true, "Conectado exitosamente en ${duration}ms")
            } else {
                Log.e(TAG, "❌ Error ${response.code()}: ${response.message()}")
                callback(false, "Error ${response.code()}: ${response.message()}")
            }
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "⏱️ Timeout - El servidor tardó más de 30s")
            Log.e(TAG, "💡 ¿Está el backend en Render dormido? Espera unos segundos más")
            callback(false, "Timeout - Backend posiblemente dormido en Render")
        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "🌐 No se puede resolver el host: ${e.message}")
            Log.e(TAG, "💡 Verifica que la URL sea correcta: $BASE_URL")
            callback(false, "Host no encontrado - Verifica la URL")
        } catch (e: javax.net.ssl.SSLHandshakeException) {
            Log.e(TAG, "🔒 Error SSL: ${e.message}")
            callback(false, "Error de certificado SSL")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error de conectividad: ${e.javaClass.simpleName}")
            Log.e(TAG, "Mensaje: ${e.message}")
            callback(false, "Error: ${e.message}")
        }
    }
}
