package com.example.mimascota.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.Model.AuthResponse
import com.example.mimascota.Model.LoginRequest
import com.example.mimascota.Model.RegistroRequest
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.util.TokenManager
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
class JwtAuthViewModel(private val context: Context) : ViewModel() {

    private val authService = RetrofitClient.authService
    private val tokenManager = RetrofitClient.getTokenManager()

    // ========== ESTADOS ==========
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

    init {
        // Verificar si hay sesión activa al iniciar
        checkLoginStatus()
    }

    /**
     * Verifica si hay un usuario logueado
     */
    private fun checkLoginStatus() {
        val loggedIn = tokenManager.isLoggedIn()
        _isLoggedIn.value = loggedIn

        if (loggedIn) {
            _usuario.value = tokenManager.getUserData()
        }
    }

    /**
     * Inicia sesión con email y password
     */
    fun login(email: String, password: String): Boolean {
        // Validaciones
        if (!isValidEmail(email)) {
            _error.value = "Email inválido"
            return false
        }

        if (!isValidPassword(password)) {
            _error.value = "La contraseña debe tener al menos 6 caracteres"
            return false
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val request = LoginRequest(email, password)
                val response = authService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!

                    // Guardar token y datos del usuario
                    tokenManager.saveToken(authResponse.token)
                    tokenManager.saveUserData(
                        usuarioId = authResponse.usuarioId,
                        email = authResponse.email,
                        nombre = authResponse.nombre,
                        telefono = authResponse.telefono,
                        direccion = authResponse.direccion,
                        run = authResponse.run
                    )

                    _loginState.value = authResponse
                    _isLoggedIn.value = true
                    _usuario.value = tokenManager.getUserData()

                } else {
                    _error.value = "Credenciales incorrectas: ${response.message()}"
                }

            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }

        return true
    }

    /**
     * Registra un nuevo usuario
     */
    fun registro(
        email: String,
        password: String,
        nombre: String,
        telefono: String? = null,
        direccion: String? = null,
        run: String? = null
    ): Boolean {
        // Validaciones
        if (!isValidEmail(email)) {
            _error.value = "Email inválido"
            return false
        }

        if (!isValidPassword(password)) {
            _error.value = "La contraseña debe tener al menos 6 caracteres"
            return false
        }

        if (nombre.isBlank()) {
            _error.value = "El nombre es obligatorio"
            return false
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val request = RegistroRequest(
                    email = email,
                    password = password,
                    nombre = nombre,
                    telefono = telefono,
                    direccion = direccion,
                    run = run
                )

                val response = authService.registro(request)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!

                    // Guardar token y datos del usuario
                    tokenManager.saveToken(authResponse.token)
                    tokenManager.saveUserData(
                        usuarioId = authResponse.usuarioId,
                        email = authResponse.email,
                        nombre = authResponse.nombre,
                        telefono = authResponse.telefono,
                        direccion = authResponse.direccion,
                        run = authResponse.run
                    )

                    _loginState.value = authResponse
                    _isLoggedIn.value = true
                    _usuario.value = tokenManager.getUserData()

                } else {
                    _error.value = "Error al registrar: ${response.message()}"
                }

            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }

        return true
    }

    /**
     * Cierra sesión
     */
    fun logout() {
        tokenManager.logout()
        _isLoggedIn.value = false
        _usuario.value = null
        _loginState.value = null
        _error.value = null
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Valida formato de email
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Valida longitud de password
     */
    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}

