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

                    // Guardar token y usuario
                    TokenManager.saveToken(loginResponse.token)
                    TokenManager.saveUsuario(loginResponse.usuario)

                    Result.success(loginResponse)
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
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

                    // Guardar token y usuario
                    TokenManager.saveToken(loginResponse.token)
                    TokenManager.saveUsuario(loginResponse.usuario)

                    Result.success(loginResponse)
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
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
                    // Si no es exitoso, limpiar el token
                    TokenManager.clearUserData()
                    Result.failure(Exception("Token inválido"))
                }
            } catch (e: Exception) {
                // Si hay error de conexión, limpiar el token
                TokenManager.clearUserData()
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }

    /**
     * Logout
     */
    suspend fun logout(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.logout()

                // Limpiar datos locales sin importar la respuesta
                TokenManager.logout()

                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error en logout: ${response.message()}"))
                }
            } catch (e: Exception) {
                // Limpiar datos locales
                TokenManager.logout()
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
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
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
}
