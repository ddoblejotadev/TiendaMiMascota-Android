package com.example.mimascota.util

import com.example.mimascota.BuildConfig

/**
 * AppConfig: Configuración centralizada de la aplicación
 *
 * Permite cambiar fácilmente entre entornos sin modificar código
 *
 * MODO DEBUG (Android Studio - Run):
 * - URL: http://10.0.2.2:8080/api/ (emulador accede a localhost de tu PC)
 * - Logs detallados activados
 * - Backend local en desarrollo
 *
 * MODO RELEASE (APK firmado):
 * - URL: https://tiendamimascotabackends.onrender.com/api/
 * - Logs básicos
 * - Backend en producción (Render)
 *
 * CAMBIAR MANUALMENTE EL ENTORNO:
 * - Modifica USE_PRODUCTION = true para forzar producción en debug
 * - O usa Build Variants en Android Studio (debug/release)
 *
 * NOTA IMPORTANTE:
 * - 10.0.2.2 es la IP especial del emulador para acceder a localhost
 * - Para dispositivo físico, cambia a tu IP local (ej: 192.168.1.100)
 */
object AppConfig {

    /**
     * FORZAR MODO PRODUCCIÓN (solo para testing)
     * Cuando esté en true, usará servidor de producción incluso en debug
     * Cuando esté en false, usará configuración del BuildType
     */
    private const val USE_PRODUCTION = true

    /**
     * URLs del backend
     */
    private const val DEV_URL = "http://10.0.2.2:8080/api/"
    private const val PROD_URL = "https://tiendamimascotabackends.onrender.com/api/"

    /**
     * URL base del backend según el entorno actual
     */
    val BASE_URL: String
        get() = when {
            USE_PRODUCTION -> PROD_URL // Producción forzada
            BuildConfig.DEBUG -> DEV_URL // Debug - servidor local (10.0.2.2:8080)
            else -> PROD_URL // Release - servidor producción (Render)
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
     * Info de configuración actual
     */
    fun getConfigInfo(): String {
        return """
            ══════════════════════════════════
            📱 CONFIGURACIÓN ACTUAL
            ══════════════════════════════════
            BuildType: ${if (BuildConfig.DEBUG) "DEBUG" else "RELEASE"}
            Entorno: ${if (isProduction) "PRODUCCIÓN" else "DESARROLLO"}
            URL Backend: $BASE_URL
            Logs detallados: ${if (isLoggingEnabled) "✅ Activados" else "❌ Desactivados"}
            ══════════════════════════════════
            
            ℹ️  RECORDATORIO:
            - Emulador usa 10.0.2.2 para localhost
            - Dispositivo físico necesita tu IP local
            - Render puede tardar 20-30s en despertar
        """.trimIndent()
    }

    /**
     * GUÍA DE USO:
     *
     * 1. DESARROLLO LOCAL (Spring Boot en tu PC):
     *    - Ejecutar: Run 'app' (debug)
     *    - USE_PRODUCTION = false
     *    - Verás logs detallados en Logcat
     *    - URL automática: http://10.0.2.2:8080/api/
     *
     * 2. PRUEBAS CON PRODUCCIÓN (Render):
     *    - Cambiar: USE_PRODUCTION = true
     *    - Ejecutar: Run 'app' (debug)
     *    - Espera 20-30s si el servidor está dormido
     *    - URL automática: https://tiendamimascotabackends.onrender.com/api/
     *
     * 3. RELEASE (APK para distribuir):
     *    - Build > Generate Signed Bundle/APK
     *    - Automáticamente usa servidor de producción
     *    - No necesitas cambiar nada
     *
     * 4. DISPOSITIVO FÍSICO (WiFi):
     *    - Cambia DEV_URL a "http://TU_IP_LOCAL:8080/api/"
     *    - Obtén tu IP con: ipconfig (Windows)
     *    - Ejemplo: "http://192.168.1.100:8080/api/"
     */
}
