package com.example.mimascota.repository

import android.util.Log
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.model.* // Asegúrate de que ErrorResponse esté en este paquete
import com.example.mimascota.util.TokenManager // Importación añadida
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * AuthRepository: Repository para autenticación
 */
class AuthRepository {
    private val apiService = RetrofitClient.apiService

    /**
     * Parsea una respuesta de error de la API para extraer un mensaje legible.
     */
    private fun parseError(response: Response<*>): String {
        return try {
            val errorBody = response.errorBody()?.string() ?: "Error desconocido"
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            errorResponse.mensaje ?: "Error ${response.code()}: ${response.message()}"
        } catch (e: JsonSyntaxException) {
            "Error inesperado al procesar la respuesta del servidor."
        } catch (e: Exception) {
            "Error de red o de servidor. Por favor, inténtalo más tarde."
        }
    }

    /**
     * Login con email y password
     */
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(email, password)
                val response = apiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    TokenManager.saveToken(authResponse.token)

                    val finalRol = if (authResponse.email.trim().equals("admin", ignoreCase = true)) "admin" else authResponse.rol

                    val usuario = Usuario(
                        usuarioId = authResponse.usuarioId,
                        email = authResponse.email,
                        nombre = authResponse.nombre,
                        telefono = authResponse.telefono,
                        direccion = authResponse.direccion,
                        run = authResponse.run,
                        rol = finalRol,
                        fotoUrl = authResponse.fotoUrl
                    )
                    TokenManager.saveUsuario(usuario)
                    Log.d("AuthRepository", "Login exitoso. Usuario guardado: $usuario")

                    Result.success(authResponse.copy(rol = finalRol))
                } else {
                    Result.failure(Exception(parseError(response)))
                }
            } catch (ex: Exception) {
                Result.failure(Exception("No se pudo conectar al servidor. Revisa tu conexión a internet."))
            }
        }
    }

    /**
     * Registro de nuevo usuario
     */
    suspend fun registro(nombre: String, email: String, password: String, telefono: String?): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = RegistroRequest(email, password, nombre, telefono)
                val response = apiService.registro(request)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    TokenManager.saveToken(authResponse.token)

                    val usuario = Usuario(
                        usuarioId = authResponse.usuarioId,
                        email = authResponse.email,
                        nombre = authResponse.nombre,
                        telefono = authResponse.telefono,
                        direccion = authResponse.direccion,
                        run = authResponse.run,
                        rol = authResponse.rol,
                        fotoUrl = authResponse.fotoUrl
                    )
                    TokenManager.saveUsuario(usuario)

                    Result.success(authResponse)
                } else {
                    Result.failure(Exception(parseError(response)))
                }
            } catch (ex: Exception) {
                Result.failure(Exception("No se pudo conectar al servidor. Revisa tu conexión a internet."))
            }
        }
    }

    /**
     * Verificar si el token es válido
     */
    suspend fun verificarToken(): Result<Usuario> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.verificarToken()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    TokenManager.clearUserData()
                    Result.failure(Exception("Tu sesión ha expirado. Por favor, inicia sesión de nuevo."))
                }
            } catch (ex: Exception) {
                TokenManager.clearUserData()
                Result.failure(Exception("Error de conexión. No se pudo verificar la sesión."))
            }
        }
    }

    /**
     * Logout
     */
    suspend fun logout(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                apiService.logout()
            } finally {
                TokenManager.logout()
            }
            Result.success(Unit)
        }
    }

    /**
     * Obtener usuario actual
     */
    suspend fun obtenerUsuario(): Result<Usuario> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerUsuario()
                if (response.isSuccessful && response.body() != null) {
                    val usuario = response.body()!!
                    TokenManager.saveUsuario(usuario)
                    Log.d("AuthRepository", "Usuario guardado desde obtenerUsuario: $usuario")
                    Result.success(usuario)
                } else {
                    Result.failure(Exception(parseError(response)))
                }
            } catch (ex: Exception) {
                Result.failure(Exception("Error de conexión. No se pudo obtener la información del usuario."))
            }
        }
    }

    /**
     * Actualizar usuario (campo dirección, telefono, region, ciudad)
     */
    suspend fun updateUsuario(usuario: Usuario): Result<Usuario> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateUser(usuario.usuarioId.toLong(), usuario)
                if (response.isSuccessful && response.body() != null) {
                    val updated = response.body()!!
                    TokenManager.saveUsuario(updated)
                    Result.success(updated)
                } else {
                    Result.failure(Exception(parseError(response)))
                }
            } catch (ex: Exception) {
                Result.failure(Exception("Error de conexión. No se pudo actualizar el perfil."))
            }
        }
    }
}

/**
 * Modelo simple para decodificar respuestas de error de la API.
 */
data class ErrorResponse(val mensaje: String?)
