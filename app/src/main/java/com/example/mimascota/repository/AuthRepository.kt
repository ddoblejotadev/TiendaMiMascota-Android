package com.example.mimascota.repository

import android.util.Log
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.model.AuthResponse
import com.example.mimascota.model.LoginRequest
import com.example.mimascota.model.RegistroRequest
import com.example.mimascota.model.Usuario
import com.example.mimascota.util.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * AuthRepository: Repository para autenticación
 */
class AuthRepository {
    private val apiService = RetrofitClient.apiService

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

                    // Patch para el admin: si el email es 'admin', forzar el rol a 'admin'.
                    val finalRol = if (authResponse.email.equals("admin", ignoreCase = true)) {
                        "admin"
                    } else {
                        authResponse.rol
                    }

                    val usuario = Usuario(
                        usuarioId = authResponse.usuarioId,
                        email = authResponse.email,
                        nombre = authResponse.nombre,
                        telefono = authResponse.telefono,
                        direccion = authResponse.direccion,
                        run = authResponse.run,
                        rol = finalRol // Usar el rol (posiblemente parcheado)
                    )
                    TokenManager.saveUsuario(usuario)
                    Log.d("AuthRepository", "Login exitoso. Usuario guardado: $usuario")

                    Result.success(authResponse)
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    Result.failure(Exception("Error ${response.code()}: ${response.message()} $errorBody"))
                }
            } catch (ex: Exception) {
                Result.failure(Exception("Error de conexión: ${ex.message}"))
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
                        rol = authResponse.rol
                    )
                    TokenManager.saveUsuario(usuario)

                    Result.success(authResponse)
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (ex: Exception) {
                Result.failure(Exception("Error de conexión: ${ex.message}"))
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
                    Result.failure(Exception("Token inválido"))
                }
            } catch (ex: Exception) {
                TokenManager.clearUserData()
                Result.failure(Exception("Error de conexión: ${ex.message}"))
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
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (ex: Exception) {
                Result.failure(Exception("Error de conexión: ${ex.message}"))
            }
        }
    }

    /**
     * Actualizar usuario (campo dirección, telefono, region, ciudad)
     */
    suspend fun updateUsuario(usuario: Usuario): Result<Usuario> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateCurrentUser(usuario)
                if (response.isSuccessful && response.body() != null) {
                    val updated = response.body()!!
                    TokenManager.saveUsuario(updated)
                    Result.success(updated)
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (ex: Exception) {
                Result.failure(Exception("Error de conexión: ${ex.message}"))
            }
        }
    }
}
