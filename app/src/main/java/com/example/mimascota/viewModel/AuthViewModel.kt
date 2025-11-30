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
            // Primero intentar registrar en el backend
            try {
                val result = withContext(Dispatchers.IO) {
                    authRepository.registro(username, email, password, null)
                }
                if (result.isSuccess) {
                    // backend guardó token y usuario en TokenManager desde AuthRepository
                    registroState.value = "Registro completado ✅"
                    // actualizar estado local de sesión
                    val savedUser = TokenManager.getUsuario()
                    usuarioActual.value = savedUser?.nombre ?: username
                    usuarioActualId.value = savedUser?.usuarioId ?: -1
                    return@launch
                } else {
                    // Si fallo en backend, seguir con fallback local
                    registroState.value = "Registro en backend falló: ${result.exceptionOrNull()?.message}. Intentando local..."
                }
            } catch (e: Exception) {
                registroState.value = "Error de red al registrar (backend): ${e.message}. Intentando local..."
            }

            // Fallback: guardar localmente en Room
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
                "Registro completado (local) ✅"
            } else {
                "El usuario ya existe ❌"
            }
        }
    }

    fun loginUsuario(email: String, password: String) {
        viewModelScope.launch {
            // Intentar login con backend
            try {
                val result = withContext(Dispatchers.IO) {
                    authRepository.login(email, password)
                }
                if (result.isSuccess) {
                    // AuthRepository guarda token y usuario en TokenManager
                    val resp = result.getOrNull()!!
                    // usuario puede ser nulo; usar saved values o fallbacks
                    val nombre = resp.usuario?.nombre ?: TokenManager.getUserName() ?: "Usuario"
                    val id = resp.usuario?.usuarioId ?: TokenManager.getUserId().toInt()
                    usuarioActual.value = nombre
                    usuarioActualId.value = if (id == 0) -1 else id
                    loginState.value = "Login exitoso 🎉"
                    return@launch
                } else {
                    // Backend devolvió error -> fallback a local
                    loginState.value = "Login backend falló: ${result.exceptionOrNull()?.message}. Intentando local..."
                }
            } catch (e: Exception) {
                loginState.value = "Error de red al loguear (backend): ${e.message}. Intentando local..."
            }

            // Fallback local (Room)
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
        TokenManager.clearUserData()
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
