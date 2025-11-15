package com.example.mimascota.util

import android.content.Context
import android.content.SharedPreferences

/**
 * TokenManager: Gestiona el almacenamiento seguro del token JWT y datos del usuario
 *
 * Funcionalidades:
 * - Guardar/obtener token JWT
 * - Guardar/obtener datos del usuario
 * - Validar sesión activa
 * - Logout (limpiar datos)
 */
class TokenManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "mimascota_auth"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_NOMBRE = "nombre"
        private const val KEY_TELEFONO = "telefono"
        private const val KEY_DIRECCION = "direccion"
        private const val KEY_RUN = "run"
    }

    /**
     * Guarda el token JWT
     */
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    /**
     * Obtiene el token JWT almacenado
     */
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    /**
     * Guarda los datos del usuario logueado
     */
    fun saveUserData(
        usuarioId: Int,
        email: String,
        nombre: String,
        telefono: String? = null,
        direccion: String? = null,
        run: String? = null
    ) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, usuarioId)
            putString(KEY_EMAIL, email)
            putString(KEY_NOMBRE, nombre)
            putString(KEY_TELEFONO, telefono)
            putString(KEY_DIRECCION, direccion)
            putString(KEY_RUN, run)
            apply()
        }
    }

    /**
     * Obtiene el ID del usuario logueado
     */
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    /**
     * Obtiene el email del usuario
     */
    fun getUserEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    /**
     * Obtiene el nombre del usuario
     */
    fun getUserName(): String? {
        return prefs.getString(KEY_NOMBRE, null)
    }

    /**
     * Obtiene el teléfono del usuario
     */
    fun getUserTelefono(): String? {
        return prefs.getString(KEY_TELEFONO, null)
    }

    /**
     * Obtiene la dirección del usuario
     */
    fun getUserDireccion(): String? {
        return prefs.getString(KEY_DIRECCION, null)
    }

    /**
     * Obtiene el RUN del usuario
     */
    fun getUserRun(): String? {
        return prefs.getString(KEY_RUN, null)
    }

    /**
     * Verifica si hay un usuario logueado
     */
    fun isLoggedIn(): Boolean {
        val token = getToken()
        val userId = getUserId()
        return !token.isNullOrEmpty() && userId > 0
    }

    /**
     * Cierra sesión y limpia todos los datos
     */
    fun logout() {
        prefs.edit().clear().apply()
    }

    /**
     * Obtiene todos los datos del usuario como mapa
     */
    fun getUserData(): Map<String, Any?> {
        return mapOf(
            "userId" to getUserId(),
            "email" to getUserEmail(),
            "nombre" to getUserName(),
            "telefono" to getUserTelefono(),
            "direccion" to getUserDireccion(),
            "run" to getUserRun()
        )
    }
}

