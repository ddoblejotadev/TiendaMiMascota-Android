package com.example.mimascota.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mimascota.Model.User

class AuthViewModel: ViewModel() {
    var mensaje = mutableStateOf("")
    var userActual = mutableStateOf<String?>(null)
    fun register(run: String, username: String, email: String, password: String, direccion: String) {
        val nuevo = User(0, run, username, email, password, direccion)
    }
}