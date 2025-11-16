package com.example.mimascota.util

import com.example.mimascota.BuildConfig

/**
 * AppConfig: Configuración centralizada de la aplicación
 *
 * Permite cambiar fácilmente entre entornos sin modificar código
 *
 * MODO DEBUG (Android Studio - Run):
 * - URL: http://10.0.2.2:8080/api/
 * - Logs detallados activados
 * - Backend local en desarrollo
 *
 * MODO RELEASE (APK firmado):
 * - URL: https://tiendamimascotabackends.onrender.com/
 * - Logs básicos
 * - Backend en producción (Render)
 *
 * CAMBIAR MANUALMENTE EL ENTORNO:
 * - Modifica USE_PRODUCTION = true/false abajo
 * - O usa Build Variants en Android Studio (debug/release)
 */
object AppConfig {

    /**
     * FORZAR MODO PRODUCCIÓN (solo para testing)
     * Cuando esté en true, usará servidor de producción incluso en debug
     * Cuando esté en false, usará configuración del BuildType
     */
    private const val USE_PRODUCTION = false

    /**
     * URL base del backend según el entorno actual
     */
    val BASE_URL: String
        get() = when {
            USE_PRODUCTION -> BuildConfig.BASE_URL // Producción forzada
            BuildConfig.DEBUG -> BuildConfig.BASE_URL_DEV // Debug - servidor local
            else -> BuildConfig.BASE_URL // Release - servidor producción
        }

    /**
     * ¿Estamos en producción?
     */
    val isProduction: Boolean
        get() = USE_PRODUCTION || !BuildConfig.DEBUG

    /**
     * ¿Logs detallados activados?
     */
    val isLoggingEnabled: Boolean
        get() = BuildConfig.DEBUG && !USE_PRODUCTION

    /**
     * URL del backend según el tipo
     */
    const val BACKEND_LOCAL = "http://10.0.2.2:8080/api/"
    const val BACKEND_PRODUCTION = "https://tiendamimascotabackends.onrender.com/"

    /**
     * Info de configuración actual
     */
    fun getConfigInfo(): String {
        return """
            ══════════════════════════════════
            CONFIGURACIÓN ACTUAL
            ══════════════════════════════════
            BuildType: ${if (BuildConfig.DEBUG) "DEBUG" else "RELEASE"}
            Entorno: ${if (isProduction) "PRODUCCIÓN" else "DESARROLLO"}
            URL Backend: $BASE_URL
            Logs detallados: ${if (isLoggingEnabled) "Activados" else "Desactivados"}
            ══════════════════════════════════
        """.trimIndent()
    }

    /**
     * GUÍA DE USO:
     *
     * 1. DESARROLLO LOCAL (Spring Boot en tu PC):
     *    - Ejecutar: Run 'app' (debug)
     *    - USE_PRODUCTION = false
     *    - Verás logs detallados en Logcat
     *
     * 2. PRUEBAS CON PRODUCCIÓN (Render):
     *    - Cambiar: USE_PRODUCTION = true
     *    - Ejecutar: Run 'app' (debug)
     *    - Espera 20-30s si el servidor está dormido
     *
     * 3. RELEASE (APK para distribuir):
     *    - Build > Generate Signed Bundle/APK
     *    - Automáticamente usa servidor de producción
     *    - No necesitas cambiar nada
     */
}

