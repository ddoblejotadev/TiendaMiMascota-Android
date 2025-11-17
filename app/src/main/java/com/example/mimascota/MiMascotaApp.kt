package com.example.mimascota

import android.app.Application
import android.util.Log
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.util.AppConfig

/**
 * MiMascotaApp: Clase Application de la aplicación
 *
 * Se ejecuta al iniciar la app, antes que cualquier Activity
 * Inicializa servicios globales y configuraciones
 */
class MiMascotaApp : Application() {

    companion object {
        private const val TAG = "MiMascotaApp"
    }

    override fun onCreate() {
        super.onCreate()

        // Mostrar configuración de URLs
        Log.d(TAG, "\n${AppConfig.getConfigInfo()}")

        // Inicializar Retrofit y TokenManager
        RetrofitClient.init(this) {
            // Callback 401: cuando el token expira o es inválido
            Log.w(TAG, "⚠️ Token inválido o expirado - Usuario deslogueado")
            // Aquí podrías lanzar un Intent para volver al login
        }

        Log.d(TAG, "✅ Aplicación iniciada correctamente")
    }
}

