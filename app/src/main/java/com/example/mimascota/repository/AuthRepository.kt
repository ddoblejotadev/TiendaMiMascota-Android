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
                // Debug logging del request
                try {
                    if (com.example.mimascota.util.AppConfig.isLoggingEnabled) {
                        android.util.Log.d("AuthRepository", "Login request: ${request}")
                    }
                } catch (_: Exception) {}

                val response = apiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!

                    // Guardar token siempre
                    TokenManager.saveToken(loginResponse.token)
                    // Usuario puede venir nulo en algunas respuestas; manejar defensivamente
                    if (loginResponse.usuario != null) {
                        try {
                            TokenManager.saveUsuario(loginResponse.usuario)
                        } catch (ex: Exception) {
                            val msg = ex.message ?: "error desconocido"
                            android.util.Log.w("AuthRepository", "No se pudo guardar usuario localmente: $msg")
                        }
                    } else if (loginResponse.usuarioId != null && loginResponse.usuarioId > 0) {
                        // Algunos backends devuelven campos sueltos en la respuesta de login
                        try {
                            val usuario = Usuario(
                                usuarioId = loginResponse.usuarioId,
                                email = email,
                                nombre = loginResponse.nombre ?: email.substringBefore('@')
                            )
                            TokenManager.saveUsuario(usuario)
                            android.util.Log.d("AuthRepository", "Usuario creado desde LoginResponse usuarioId=${loginResponse.usuarioId}")
                        } catch (ex: Exception) {
                            val msg = ex.message ?: "error desconocido"
                            android.util.Log.w("AuthRepository", "No se pudo crear usuario desde LoginResponse: $msg")
                        }
                    } else {
                         // Intentar recuperar el perfil actual si el backend ofrece ese endpoint
                         try {
                             val perfilResp = apiService.obtenerUsuario()
                             if (perfilResp.isSuccessful && perfilResp.body() != null) {
                                 TokenManager.saveUsuario(perfilResp.body()!!)
                             } else {
                                 android.util.Log.w("AuthRepository", "Usuario no incluido en LoginResponse y obtenerUsuario devolvió ${perfilResp.code()}")
                                 // Fallback: intentar verificar token para obtener usuario
                                 try {
                                     val vResp = apiService.verificarToken()
                                     if (vResp.isSuccessful && vResp.body() != null) {
                                         TokenManager.saveUsuario(vResp.body()!!)
                                         android.util.Log.d("AuthRepository", "Perfil recuperado via verificarToken()")
                                     } else {
                                         android.util.Log.w("AuthRepository", "verificarToken devolvió ${vResp.code()}")
                                     }
                                 } catch (ex: Exception) {
                                     val msg = ex.message ?: "error desconocido"
                                     android.util.Log.w("AuthRepository", "Error fallback verificarToken: $msg")
                                 }
                             }
                         } catch (ex: Exception) {
                             val msg = ex.message ?: "error desconocido"
                             android.util.Log.w("AuthRepository", "Error al obtener perfil tras login: $msg")
                             // Intentar fallback verificarToken
                             try {
                                 val vResp = apiService.verificarToken()
                                 if (vResp.isSuccessful && vResp.body() != null) {
                                     TokenManager.saveUsuario(vResp.body()!!)
                                     android.util.Log.d("AuthRepository", "Perfil recuperado via verificarToken() (catch)")
                                 } else {
                                     android.util.Log.w("AuthRepository", "verificarToken devolvió ${vResp.code()} (catch)")
                                 }
                             } catch (_: Exception) {
                                 // ignorar
                             }
                         }
                     }

                    Result.success(loginResponse)
                } else {
                    val bodyString = try { response.errorBody()?.string() } catch (ex: Exception) { null }
                    android.util.Log.e("AuthRepository", "Login failed: code=${response.code()} message=${response.message()} body=$bodyString")
                    Result.failure(Exception("Error ${response.code()}: ${response.message()} Body=$bodyString"))
                }
            } catch (ex: Exception) {
                val msg = ex.message ?: "error desconocido"
                android.util.Log.e("AuthRepository", "Exception during login: $msg", ex)
                Result.failure(Exception("Error de conexión: $msg"))
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
                    // Guardar usuario si viene
                    loginResponse.usuario?.let { TokenManager.saveUsuario(it) }

                    Result.success(loginResponse)
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (ex: Exception) {
                val msg = ex.message ?: "error desconocido"
                Result.failure(Exception("Error de conexión: $msg"))
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
                    // Si no es exitoso, limpiar el token
                    TokenManager.clearUserData()
                    Result.failure<Usuario>(Exception("Token inválido"))
                }
            } catch (ex: Exception) {
                // Si hay error de conexión, limpiar el token
                TokenManager.clearUserData()
                val msg = ex.message ?: "error desconocido"
                Result.failure<Usuario>(Exception("Error de conexión: $msg"))
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
            } catch (ex: Exception) {
                // Limpiar datos locales
                TokenManager.logout()
                val msg = ex.message ?: "error desconocido"
                Result.failure(Exception("Error de conexión: $msg"))
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
                    // Fallback: intentar verificar token para obtener usuario
                    try {
                        val vResp = apiService.verificarToken()
                        if (vResp.isSuccessful && vResp.body() != null) {
                            TokenManager.saveUsuario(vResp.body()!!)
                            android.util.Log.d("AuthRepository", "Perfil recuperado via verificarToken()")
                            Result.success(vResp.body()!!)
                        } else {
                            Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                        }
                    } catch (ex: Exception) {
                        Result.failure(Exception("Error de conexión: error desconocido"))
                    }
                }
            } catch (ex: Exception) {
                val msg = ex.message ?: "error desconocido"
                Result.failure(Exception("Error de conexión: $msg"))
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
                val msg = ex.message ?: "error desconocido"
                Result.failure(Exception("Error de conexión: $msg"))
            }
        }
    }
}
