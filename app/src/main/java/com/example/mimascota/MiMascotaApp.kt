package com.example.mimascota

import android.app.Application
import com.example.mimascota.client.RetrofitClient

class MiMascotaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializamos Retrofit y TokenManager una sola vez
        RetrofitClient.init(this) { /* Callback 401: aquí podrías lanzar intento de relogin o navegar al login */ }
    }
}

