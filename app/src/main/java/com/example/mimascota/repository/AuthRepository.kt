package com.example.mimascota.repository

import com.example.mimascota.model.LoginRequest
import com.example.mimascota.model.LoginResponse
import com.example.mimascota.model.RegistroRequest
import com.example.mimascota.model.Usuario
import com.example.mimascota.client.RetrofitClient // Corregido: usar RetrofitClient
import com.example.mimascota.util.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * AuthRepository: Repository para autenticación
 */
class AuthRepository {
    // Corregido: Usar la instancia única de apiService desde RetrofitClient
    private val apiService = RetrofitClient.apiService

    /**
     * Login con email y password
     */
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(email, password)
                val response = apiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    TokenManager.saveToken(loginResponse.token)

                    // Después de un login exitoso, obtener y guardar el perfil del usuario
                    val perfilResult = obtenerUsuario()
                    if (perfilResult.isFailure) {
                        // Si no se puede obtener el perfil, el login se considera fallido
                        return@withContext Result.failure(Exception("No se pudo obtener el perfil del usuario después del login."))
                    }

                    Result.success(loginResponse)
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
    suspend fun registro(nombre: String, email: String, password: String, telefono: String?): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = RegistroRequest(email, password, nombre, telefono)
                val response = apiService.registro(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    TokenManager.saveToken(loginResponse.token)
                    val perfilResult = obtenerUsuario()
                    if (perfilResult.isFailure) {
                        return@withContext Result.failure(Exception("No se pudo obtener el perfil del usuario después del registro."))
                    }
                    Result.success(loginResponse)
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
                    Result.success<Usuario>(response.body()!!)
                } else {
                    TokenManager.clearUserData()
                    Result.failure<Usuario>(Exception("Token inválido"))
                }
            } catch (ex: Exception) {
                TokenManager.clearUserData()
                Result.failure<Usuario>(Exception("Error de conexión: ${ex.message}"))
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
