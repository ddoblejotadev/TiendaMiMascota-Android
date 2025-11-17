package com.example.tiendamimascota.network

object ApiConfig {
    // Para desarrollo: usa 10.0.2.2 (emulador) o tu IP local
    private const val DEV_URL = "http://10.0.2.2:8080/api/"

    // Para dispositivo físico en la misma red WiFi, cambia a tu IP local
    // Ejemplo: private const val DEV_URL = "http://192.168.1.100:8080/api/"

    // Para producción: usa Render
    private const val PROD_URL = "https://tiendamimascotabackends.onrender.com/api/"

    // Cambia esto a true cuando quieras usar producción
    private const val USE_PRODUCTION = true

    val BASE_URL: String = if (USE_PRODUCTION) PROD_URL else DEV_URL

    // URL base para recursos estáticos (imágenes, archivos, etc.)
    // Sin el /api/ al final
    val BASE_ORIGIN: String = BASE_URL.substringBefore("/api/")

    /**
     * Convierte una ruta relativa o URL a una URL absoluta válida
     *
     * Ejemplos:
     * - "/images/producto.jpg" -> "https://tiendamimascotabackends.onrender.com/images/producto.jpg"
     * - "images/producto.jpg" -> "https://tiendamimascotabackends.onrender.com/images/producto.jpg"
     * - "https://..." -> "https://..." (ya absoluta)
     * - null/blank -> null
     */
    fun toAbsoluteImageUrl(pathOrUrl: String?): String? {
        if (pathOrUrl.isNullOrBlank()) return null

        // Si ya es una URL absoluta, retornarla
        if (pathOrUrl.startsWith("http://", ignoreCase = true) ||
            pathOrUrl.startsWith("https://", ignoreCase = true)) {
            return pathOrUrl
        }

        // Si es una ruta relativa, construir URL absoluta
        val cleanPath = pathOrUrl.trimStart('/')
        return "$BASE_ORIGIN/$cleanPath"
    }

    // Información del entorno actual (útil para debugging)
    val currentEnvironment: String
        get() = if (USE_PRODUCTION) "Producción" else "Desarrollo"
}
