package com.example.mimascota.ViewModel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.Model.User
import com.example.mimascota.repository.UserRepository
import com.example.mimascota.repository.UserRoomRepository
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

    var registroState = mutableStateOf("")
    var loginState = mutableStateOf("")
    var usuarioActual = mutableStateOf<String?>(null)
    var usuarioActualId = mutableStateOf<Int?>(null)

    private val _fotoPerfil = MutableStateFlow<String?>(null)
    val fotoPerfil: StateFlow<String?> = _fotoPerfil

    fun registrarUsuario(run: String, username: String, email: String, password: String, direccion: String) {
        // Validación cliente (defensiva) antes de insertar en la base local
        if (username.isBlank()) {
            registroState.value = "Ingrese nombre"
            return
        }
        if (run.isBlank()) {
            registroState.value = "Ingrese RUT"
            return
        }
        if (email.isBlank()) {
            registroState.value = "Ingrese email"
            return
        }
        if (password.length < 6) {
            registroState.value = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        viewModelScope.launch {
            val contexto = getApplication<Application>()
            val nuevoUsuario = User(
                id = repo.generarNuevoId(contexto),
                run = run,
                username = username,
                email = email,
                password = password,
                direccion = direccion
            )

            val resultado = withContext(Dispatchers.IO) {
                roomRepository.saveUserToDatabase(nuevoUsuario)
            }

            registroState.value = if (resultado) {
                "Registro completado ✅"
            } else {
                "El usuario ya existe ❌"
            }
        }
    }

    fun loginUsuario(email: String, password: String) {
        viewModelScope.launch {
            if (email.equals("admin", ignoreCase = true) && password == "admin") {
                usuarioActual.value = "admin"
                usuarioActualId.value = 0
                loginState.value = "Login exitoso 🎉 (Administrador)"
                return@launch
            }

            val usuarioEncontrado = withContext(Dispatchers.IO) {
                roomRepository.getUserByEmailAndPassword(email, password)
            }

            loginState.value = if (usuarioEncontrado != null) {
                usuarioActual.value = usuarioEncontrado.username
                usuarioActualId.value = usuarioEncontrado.id
                "Login exitoso 🎉"
            } else {
                "Credenciales inválidas ❌"
            }
        }
    }

    fun esAdmin(): Boolean = usuarioActual.value?.equals("admin", ignoreCase = true) == true

    fun cerrarSesion() {
        usuarioActual.value = null
        usuarioActualId.value = null
        loginState.value = ""
        registroState.value = ""
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
