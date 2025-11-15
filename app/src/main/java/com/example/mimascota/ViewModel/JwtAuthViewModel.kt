package com.example.mimascota.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.Model.AuthResponse
import com.example.mimascota.Model.LoginRequest
import com.example.mimascota.Model.RegistroRequest
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.util.TokenManager
import com.example.mimascota.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * AuthViewModel: ViewModel para gestionar autenticación JWT
 *
 * Funcionalidades:
 * - Login con email y password
 * - Registro de nuevos usuarios
 * - Logout
 * - Estados reactivos con StateFlow
 * - Validación de credenciales
 */
class JwtAuthViewModel : ViewModel() {

    private val authService = RetrofitClient.authService
    private val tokenManager = TokenManager

    // Estados expuestos (podrían usarse desde UI)
    private val _loginState = MutableStateFlow<AuthResponse?>(null)
    val loginState: StateFlow<AuthResponse?> = _loginState.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    private val _usuario = MutableStateFlow<Map<String, Any?>?>(null)
    val usuario: StateFlow<Map<String, Any?>?> = _usuario.asStateFlow()

    init { checkLoginStatus() }

    private fun checkLoginStatus() {
        val loggedIn = tokenManager.isLoggedIn()
        _isLoggedIn.value = loggedIn
        if (loggedIn) {
            tokenManager.getUsuario()?.let { u ->
                _usuario.value = mapOf(
                    "usuario_id" to u.usuarioId,
                    "nombre" to u.nombre,
                    "email" to u.email,
                    "telefono" to (u.telefono ?: ""),
                    "rol" to u.rol
                )
            }
        }
    }

    fun login(email: String, password: String): Boolean {
        if (!isValidEmail(email)) { _error.value = "Email inválido"; return false }
        if (!isValidPassword(password)) { _error.value = "La contraseña debe tener al menos 6 caracteres"; return false }
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val response = authService.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    response.body()?.let { authResp ->
                        // Construir Usuario desde AuthResponse (no trae objeto usuario)
                        val usuarioObj = Usuario(
                            usuarioId = authResp.usuarioId,
                            nombre = authResp.nombre,
                            email = authResp.email,
                            telefono = authResp.telefono,
                            rol = "USUARIO"
                        )
                        tokenManager.saveToken(authResp.token)
                        tokenManager.saveUsuario(usuarioObj)
                        _loginState.value = authResp
                        _isLoggedIn.value = true
                        _usuario.value = mapOf(
                            "usuario_id" to usuarioObj.usuarioId,
                            "nombre" to usuarioObj.nombre,
                            "email" to usuarioObj.email,
                            "telefono" to (usuarioObj.telefono ?: ""),
                            "rol" to usuarioObj.rol
                        )
                    } ?: run { _error.value = "Respuesta vacía del servidor" }
                } else {
                    _error.value = "Credenciales incorrectas: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.localizedMessage}"
            } finally { _isLoading.value = false }
        }
        return true
    }

    fun registro(
        email: String,
        password: String,
        nombre: String,
        telefono: String? = null,
        @Suppress("UNUSED_PARAMETER") direccion: String? = null,
        @Suppress("UNUSED_PARAMETER") run: String? = null
    ): Boolean {
        if (!isValidEmail(email)) { _error.value = "Email inválido"; return false }
        if (!isValidPassword(password)) { _error.value = "La contraseña debe tener al menos 6 caracteres"; return false }
        if (nombre.isBlank()) { _error.value = "El nombre es obligatorio"; return false }
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val response = authService.registro(
                    RegistroRequest(
                        nombre = nombre,
                        email = email,
                        password = password,
                        telefono = telefono
                    )
                )
                if (response.isSuccessful) {
                    response.body()?.let { authResp ->
                        val usuarioObj = Usuario(
                            usuarioId = authResp.usuarioId,
                            nombre = authResp.nombre,
                            email = authResp.email,
                            telefono = authResp.telefono,
                            rol = "USUARIO"
                        )
                        tokenManager.saveToken(authResp.token)
                        tokenManager.saveUsuario(usuarioObj)
                        _loginState.value = authResp
                        _isLoggedIn.value = true
                        _usuario.value = mapOf(
                            "usuario_id" to usuarioObj.usuarioId,
                            "nombre" to usuarioObj.nombre,
                            "email" to usuarioObj.email,
                            "telefono" to (usuarioObj.telefono ?: ""),
                            "rol" to usuarioObj.rol
                        )
                    } ?: run { _error.value = "Respuesta vacía del servidor" }
                } else {
                    _error.value = "Error al registrar: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.localizedMessage}"
            } finally { _isLoading.value = false }
        }
        return true
    }

    fun logout() {
        tokenManager.logout()
        _isLoggedIn.value = false
        _usuario.value = null
        _loginState.value = null
        _error.value = null
    }

    fun clearError() { _error.value = null }

    private fun isValidEmail(email: String): Boolean = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    private fun isValidPassword(password: String): Boolean = password.length >= 6
}
