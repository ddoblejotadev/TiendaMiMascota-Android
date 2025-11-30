package com.example.mimascota.viewModel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.repository.AuthRepository
import com.example.mimascota.repository.UserRoomRepository
import com.example.mimascota.util.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val userRole: String?) : LoginState()
    data class Error(val message: String) : LoginState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()
    private val roomRepository = UserRoomRepository(application)

    var registroState = mutableStateOf("")
    var loginState = mutableStateOf<LoginState>(LoginState.Idle)
        private set
    var usuarioActual = mutableStateOf<String?>(null)
    var usuarioActualId = mutableStateOf<Int?>(null)

    private val _fotoPerfil = MutableStateFlow<String?>(null)
    val fotoPerfil: StateFlow<String?> = _fotoPerfil

    fun registrarUsuario(run: String, username: String, email: String, password: String, direccion: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                authRepository.registro(username, email, password, null)
            }
            if (result.isSuccess) {
                registroState.value = "Registro completado ✅"
                val savedUser = TokenManager.getUsuario()
                usuarioActual.value = savedUser?.nombre
                usuarioActualId.value = savedUser?.usuarioId
            } else {
                registroState.value = "Registro falló: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun loginUsuario(email: String, password: String) {
        viewModelScope.launch {
            loginState.value = LoginState.Loading
            val result = withContext(Dispatchers.IO) {
                authRepository.login(email, password)
            }
            if (result.isSuccess) {
                val savedUser = TokenManager.getUsuario()
                usuarioActual.value = savedUser?.nombre
                usuarioActualId.value = savedUser?.usuarioId
                loginState.value = LoginState.Success(savedUser?.rol)
            } else {
                loginState.value = LoginState.Error(result.exceptionOrNull()?.message ?: "Credenciales inválidas")
            }
        }
    }
    
    fun resetLoginState() {
        loginState.value = LoginState.Idle
    }

    fun esAdmin(): Boolean = TokenManager.getUserRole() == "admin"

    fun cerrarSesion() {
        viewModelScope.launch {
            authRepository.logout()
            usuarioActual.value = null
            usuarioActualId.value = null
            loginState.value = LoginState.Idle
            registroState.value = ""
        }
    }

    fun actualizarFotoPerfil(fotoPath: String) {
        viewModelScope.launch {
            usuarioActualId.value?.let { userId ->
                if (userId != 0) {
                    withContext(Dispatchers.IO) {
                        roomRepository.updateUserPhoto(userId, fotoPath)
                    }
                    _fotoPerfil.value = fotoPath
                }
            }
        }
    }

    suspend fun obtenerFotoPerfilActual(): String? {
        val userId = usuarioActualId.value
        if (userId != null && userId != 0) {
            val foto = withContext(Dispatchers.IO) {
                roomRepository.getUserPhotoById(userId)
            }
            _fotoPerfil.value = foto
            return foto
        } else {
            return null
        }
    }
}
