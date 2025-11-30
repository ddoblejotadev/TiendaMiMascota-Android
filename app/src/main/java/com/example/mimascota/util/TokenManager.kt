package com.example.mimascota.util

import android.content.Context
import com.example.mimascota.model.Usuario
import com.google.gson.Gson

/**
 * TokenManager: Gestiona almacenamiento de token y datos de usuario en SharedPreferences
 */
object TokenManager {
    private const val PREFS_NAME = "TiendaMiMascotaPrefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_USUARIO = "usuario_data"
    private const val KEY_USER_ID = "user_id"

    private lateinit var prefs: android.content.SharedPreferences
    private val gson = Gson()

    /**
     * Inicializar TokenManager (debe llamarse en Application o MainActivity)
     */
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Guardar token JWT
     */
    fun saveToken(token: String) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            apply()
        }
    }

    /**
     * Obtener token JWT
     */
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    /**
     * Guardar datos del usuario
     */
    fun saveUsuario(usuario: Usuario) {
        val json = gson.toJson(usuario)
        prefs.edit().apply {
            putString(KEY_USUARIO, json)
            putLong(KEY_USER_ID, usuario.usuarioId.toLong())
            apply()
        }
    }

    /**
     * Obtener datos del usuario
     */
    fun getUsuario(): Usuario? {
        val json = prefs.getString(KEY_USUARIO, null)
        return if (json != null) {
            try {
                gson.fromJson(json, Usuario::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    /**
     * Obtener ID del usuario
     */
    fun getUserId(): Long {
        return prefs.getLong(KEY_USER_ID, -1L)
    }

    /**
     * Obtener nombre del usuario
     */
    fun getUserName(): String? {
        return getUsuario()?.nombre
    }

    /**
     * Obtener email del usuario
     */
    fun getUserEmail(): String? {
        return getUsuario()?.email
    }

    /**
     * Obtener teléfono del usuario
     */
    fun getUserTelefono(): String? {
        return getUsuario()?.telefono
    }

    /**
     * Obtener dirección del usuario (ahora usando el modelo Usuario si existe)
     */
    fun getUserDireccion(): String? {
        return getUsuario()?.direccion
    }

    /**
     * Obtener RUN del usuario
     */
    fun getUserRun(): String? {
        return getUsuario()?.run
    }

    /**
     * Verificar si está logueado
     */
    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }

    /**
     * Cerrar sesión
     */
    fun logout() {
        prefs.edit().apply {
            clear()
            apply()
        }
    }

    /**
     * Limpiar datos (pero mantener algunas preferencias)
     */
    fun clearUserData() {
        prefs.edit().apply {
            remove(KEY_TOKEN)
            remove(KEY_USUARIO)
            remove(KEY_USER_ID)
            apply()
        }
    }
}
