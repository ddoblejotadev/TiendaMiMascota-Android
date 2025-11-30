package com.example.mimascota.viewModel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.User
import com.example.mimascota.repository.UserRepository
import com.example.mimascota.repository.UserRoomRepository
import com.example.mimascota.repository.AuthRepository
import com.example.mimascota.util.TokenManager
import com.example.mimascota.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * AuthViewModel: Maneja autenticación y perfil de usuarios
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = UserRepository()
    private val roomRepository = UserRoomRepository(application)
    private val authRepository = AuthRepository()

    var registroState = mutableStateOf("")
    var loginState = mutableStateOf("")
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
            val result = withContext(Dispatchers.IO) {
                authRepository.login(email, password)
            }
            if (result.isSuccess) {
                val savedUser = TokenManager.getUsuario()
                usuarioActual.value = savedUser?.nombre
                usuarioActualId.value = savedUser?.usuarioId
                loginState.value = "Login exitoso 🎉"
            } else {
                loginState.value = "Error: ${result.exceptionOrNull()?.message ?: "Credenciales inválidas"}"
            }
        }
    }

    fun esAdmin(): Boolean = TokenManager.getUserRole() == "admin"

    fun cerrarSesion() {
        viewModelScope.launch {
            authRepository.logout()
            usuarioActual.value = null
            usuarioActualId.value = null
            loginState.value = ""
            registroState.value = ""
        }
    }

    fun actualizarFotoPerfil(fotoPerfil: String) {
        viewModelScope.launch {
            usuarioActualId.value?.takeIf { it != 0 }?.let { userId ->
                withContext(Dispatchers.IO) {
                    roomRepository.updateUserPhoto(userId, fotoPerfil)
                }
                _fotoPerfil.value = fotoPerfil
            }
        }
    }

    suspend fun obtenerFotoPerfilActual(): String? {
        val foto = usuarioActualId.value?.takeIf { it != 0 }?.let { userId ->
            withContext(Dispatchers.IO) {
                roomRepository.getUserPhotoById(userId)
            }
        }
        _fotoPerfil.value = foto
        return foto
    }
}
