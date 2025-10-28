package com.example.mimascota.ViewModel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.Model.User
import com.example.mimascota.repository.UserRepository
import com.example.mimascota.repository.UserRoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = UserRepository()

    // Repository con Room para persistencia en BD
    private val roomRepository = UserRoomRepository(application)

    var registroState = mutableStateOf<String>("")
    var loginState = mutableStateOf<String>("")
    var usuarioActual = mutableStateOf<String?>(null)
    var usuarioActualId = mutableStateOf<Int?>(null)

    // StateFlow para foto de perfil (observable)
    private val _fotoPerfil = MutableStateFlow<String?>(null)
    val fotoPerfil: StateFlow<String?> = _fotoPerfil

    /**
     * Registrar nuevo usuario
     * Guarda en ambas ubicaciones: archivo (legacy) y BD Room (nueva)
     */
    fun registrarUsuario(run: String, username: String, email: String, password: String, direccion: String) {
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
                // Guardar en archivo (legacy)
                val guardoEnArchivo = repo.guardarUsuarioEnArchivo(contexto, nuevoUsuario)

                // Guardar en BD Room (persistencia local)
                val guardoEnRoom = roomRepository.saveUserToDatabase(nuevoUsuario)

                guardoEnArchivo && guardoEnRoom
            }

            registroState.value = if (resultado) {
                "Registro completado ✅"
            } else {
                "El usuario ya existe ❌"
            }
        }
    }

    /**
     * Login de usuario
     * Busca credenciales en BD Room
     */
    fun loginUsuario(email: String, password: String) {
        viewModelScope.launch {
            val contexto = getApplication<Application>()

            // Verificar si es el admin (credenciales hardcodeadas)
            if (email.equals("admin", ignoreCase = true) && password == "admin") {
                usuarioActual.value = "admin"
                usuarioActualId.value = 0
                loginState.value = "Login exitoso 🎉 (Administrador)"
                return@launch
            }

            // Buscar en BD Room
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

    // Función para verificar si el usuario actual es administrador
    fun esAdmin(): Boolean {
        return usuarioActual.value?.equals("admin", ignoreCase = true) == true
    }

    // Función para cerrar sesión
    fun cerrarSesion() {
        usuarioActual.value = null
        usuarioActualId.value = null
        loginState.value = ""
        registroState.value = ""
    }

    // Función para actualizar foto de perfil
    fun actualizarFotoPerfil(fotoPerfil: String) {
        viewModelScope.launch {
            if (usuarioActualId.value != null && usuarioActualId.value != 0) {
                withContext(Dispatchers.IO) {
                    roomRepository.updateUserPhoto(usuarioActualId.value!!, fotoPerfil)
                }
                // Actualizar StateFlow para notificar a la UI
                _fotoPerfil.value = fotoPerfil
            }
        }
    }

    // Función para obtener foto de perfil del usuario actual
    suspend fun obtenerFotoPerfilActual(): String? {
        val foto = if (usuarioActualId.value != null && usuarioActualId.value != 0) {
            withContext(Dispatchers.IO) {
                roomRepository.getUserPhotoById(usuarioActualId.value!!)
            }
        } else {
            null
        }
        _fotoPerfil.value = foto
        return foto
    }
}
