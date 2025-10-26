package com.example.mimascota.ViewModel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.Model.User
import com.example.mimascota.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = UserRepository()
    var registroState = mutableStateOf<String>("")
    var loginState = mutableStateOf<String>("")
    var usuarioActual = mutableStateOf<String?>(null)

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
                repo.guardarUsuarioEnArchivo(contexto, nuevoUsuario)
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
            val contexto = getApplication<Application>()

            // Verificar si es el admin (credenciales hardcodeadas)
            if (email.equals("admin", ignoreCase = true) && password == "admin") {
                usuarioActual.value = "admin"
                loginState.value = "Login exitoso 🎉 (Administrador)"
                return@launch
            }

            // Si no es admin, buscar en usuarios normales
            val usuarioEncontrado = withContext(Dispatchers.IO) {
                repo.obtenerUsuarios(contexto).find {
                    it.email.equals(email, ignoreCase = true) && it.password == password
                }
            }

            loginState.value = if (usuarioEncontrado != null) {
                usuarioActual.value = usuarioEncontrado.username
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
        loginState.value = ""
        registroState.value = ""
    }
}