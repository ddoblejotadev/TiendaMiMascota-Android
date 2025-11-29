package com.example.mimascota.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.AuthResponse
import com.example.mimascota.model.LoginRequest
import com.example.mimascota.model.RegistroRequest
import com.example.mimascota.repository.AuthRepository
import com.example.mimascota.util.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JwtAuthViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val authRepository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    sealed class AuthState {
        object Loading : AuthState()
        data class Authenticated(val token: String, val userId: Int) : AuthState()
        object Unauthenticated : AuthState()
        data class Error(val message: String) : AuthState()
    }

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            if (tokenManager.isLoggedIn()) {
                val token = tokenManager.getToken()!!
                val userId = tokenManager.getUserId()
                _authState.value = AuthState.Authenticated(token, userId)
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.login(email, password)
                .onSuccess { response ->
                    _authState.value = AuthState.Authenticated(response.token, response.usuario.usuarioId)
                }
                .onFailure { throwable ->
                    _authState.value = AuthState.Error(throwable.message ?: "Error desconocido")
                }
        }
    }

    fun registro(nombre: String, email: String, password: String, telefono: String?) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.registro(nombre, email, password, telefono)
                .onSuccess { response ->
                    _authState.value = AuthState.Authenticated(response.token, response.usuario.usuarioId)
                }
                .onFailure { throwable ->
                    _authState.value = AuthState.Error(throwable.message ?: "Error desconocido")
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState.Unauthenticated
        }
    }
}
