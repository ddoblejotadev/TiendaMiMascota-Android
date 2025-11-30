package com.example.mimascota.viewModel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.Usuario
import com.example.mimascota.repository.AuthRepository
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

    var registroState = mutableStateOf("")
    var loginState = mutableStateOf<LoginState>(LoginState.Idle)
        private set
    var usuarioActual = mutableStateOf<String?>(null)
        private set
    var usuarioActualId = mutableStateOf<Int?>(null)
        private set

    var fotoPerfil = mutableStateOf<Bitmap?>(null)
        private set

    init {
        refrescarUsuarioDesdeToken()
    }

    fun refrescarUsuarioDesdeToken() {
        viewModelScope.launch {
            val usuario = TokenManager.getUsuario()
            usuarioActual.value = usuario?.nombre
            usuarioActualId.value = usuario?.usuarioId

            usuario?.fotoUrl?.let { base64String ->
                if (base64String.startsWith("data:image")) {
                    val pureBase64 = base64String.substringAfter(",")
                    try {
                        val decodedBytes = Base64.decode(pureBase64, Base64.DEFAULT)
                        fotoPerfil.value = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        Log.d("AuthViewModel", "✅ Foto de perfil decodificada y actualizada.")
                    } catch (e: Exception) {
                        Log.e("AuthViewModel", "❌ Error al decodificar foto de perfil Base64: ${e.message}")
                        fotoPerfil.value = null
                    }
                } else {
                    Log.w("AuthViewModel", "⚠️ fotoUrl no es un data URI válido.")
                    fotoPerfil.value = null
                }
            } ?: run {
                fotoPerfil.value = null // Limpiar la foto si fotoUrl es nulo
            }
            Log.d("AuthViewModel", "🔄 Usuario refrescado desde TokenManager: ${usuario?.nombre}")
        }
    }

    fun registrarUsuario(run: String, username: String, email: String, password: String, direccion: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                authRepository.registro(username, email, password, null)
            }
            if (result.isSuccess) {
                registroState.value = "Registro completado ✅"
                refrescarUsuarioDesdeToken()
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
                refrescarUsuarioDesdeToken()
                loginState.value = LoginState.Success(TokenManager.getUserRole())
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
            fotoPerfil.value = null
            loginState.value = LoginState.Idle
            registroState.value = ""
        }
    }
}
