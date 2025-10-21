package com.example.mimascota.ViewModel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.AndroidViewModel
import com.example.mimascota.Model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val gson = Gson()
    private val fileName = "users.json"
    var mensaje = mutableStateOf("")
    var userActual = mutableStateOf<String?>(null)

    private fun copyAssetToInternalIfNeeded() {
        val file = File(getApplication<Application>().filesDir, fileName)
        if (!file.exists()) {
            getApplication<Application>().assets.open(fileName).use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }
    private fun readUsers(): MutableList<User> {
        copyAssetToInternalIfNeeded()
        val file = File(getApplication<Application>().filesDir, fileName)
        if (!file.exists()) return mutableListOf()
        val json = file.readText()
        if (json.isBlank()) return mutableListOf()
        val type = object : TypeToken<List<User>>() {}.type
        return gson.fromJson<List<User>>(json, type).toMutableList()
    }

    private fun saveUsers(users: List<User>) {
        val file = File(getApplication<Application>().filesDir, fileName)
        file.writeText(gson.toJson(users))
    }

    fun register(run: String, username: String, email: String, password: String, direccion: String) {
        val users = readUsers()
        if (users.any { it.run == run }) {
            mensaje.value = "Run ya registrado"
            return
        }
        if (users.any { it.email.equals(email, ignoreCase = true) }) {
            mensaje.value = "Email ya registrado"
            return
        }
        val nextId = (users.maxOfOrNull { it.id } ?: 0) + 1
        val nuevo = User(nextId, run, username, email, password, direccion)
        users.add(nuevo)
        saveUsers(users)
        mensaje.value = "Registro exitoso"
        userActual.value = username
    }
    fun login(email: String, password: String) {
        val users = readUsers()
        val user = users.find { it.email.equals(email, ignoreCase = true) && it.password == password }
        if (user != null) {
            mensaje.value = "Login exitoso"
            userActual.value = user.username
        } else {
            mensaje.value = "Email o contraseña incorrectos"
        }
    }
}