package com.example.mimascota.util

import android.util.Log
import com.example.mimascota.config.ApiConfig
import com.example.mimascota.service.HealthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * ConnectionTester: Utilidad para verificar la conexión con el backend
 *
 * USO:
 * ```kotlin
 * viewModelScope.launch {
 *     val isConnected = ConnectionTester.testConnection()
 *     if (isConnected) {
 *         Log.d("App", "✅ Backend disponible")
 *     } else {
 *         Log.e("App", "❌ Backend no responde")
 *     }
 * }
 * ```
 */
object ConnectionTester {

    private const val TAG = "ConnectionTester"

    private val healthService: HealthService by lazy {
        ApiConfig.retrofit.create(HealthService::class.java)
    }

    /**
     * Prueba la conexión con el backend
     * @return true si el backend responde correctamente
     */
    suspend fun testConnection(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🔍 Probando conexión con backend...")
            Log.d(TAG, "📡 URL: ${AppConfig.BASE_URL}")

            // Intenta hacer ping al endpoint de productos
            val response = healthService.pingProductos()

            val success = response.isSuccessful

            if (success) {
                Log.d(TAG, "✅ Backend respondió correctamente")
                Log.d(TAG, "📊 Status code: ${response.code()}")
            } else {
                Log.e(TAG, "❌ Backend respondió con error")
                Log.e(TAG, "📊 Status code: ${response.code()}")
                Log.e(TAG, "📄 Message: ${response.message()}")
            }

            success
        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "❌ No se puede resolver el host - Verifica tu conexión a internet")
            Log.e(TAG, "🌐 URL intentada: ${AppConfig.BASE_URL}")
            false
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "⏱️ Timeout - El servidor no respondió a tiempo")
            Log.e(TAG, "💡 Si usas Render, espera 20-30s para que despierte")
            false
        } catch (e: java.net.ConnectException) {
            Log.e(TAG, "❌ No se puede conectar al servidor")
            Log.e(TAG, "💡 Verifica que el backend esté corriendo")
            Log.e(TAG, "🌐 URL intentada: ${AppConfig.BASE_URL}")
            false
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error inesperado al conectar con backend")
            Log.e(TAG, "📝 Error: ${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    /**
     * Obtiene información detallada de la conexión
     */
    suspend fun getConnectionInfo(): ConnectionInfo = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val isConnected = testConnection()
        val responseTime = System.currentTimeMillis() - startTime

        ConnectionInfo(
            url = AppConfig.BASE_URL,
            isConnected = isConnected,
            responseTimeMs = responseTime,
            environment = if (AppConfig.isProduction) "Producción" else "Desarrollo"
        )
    }

    /**
     * Info de conexión
     */
    data class ConnectionInfo(
        val url: String,
        val isConnected: Boolean,
        val responseTimeMs: Long,
        val environment: String
    ) {
        override fun toString(): String {
            return """
                ══════════════════════════════════
                📡 ESTADO DE CONEXIÓN
                ══════════════════════════════════
                URL: $url
                Entorno: $environment
                Estado: ${if (isConnected) "✅ Conectado" else "❌ Sin conexión"}
                Tiempo de respuesta: ${responseTimeMs}ms
                ══════════════════════════════════
            """.trimIndent()
        }
    }
}

