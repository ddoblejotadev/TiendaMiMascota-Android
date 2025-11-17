package com.example.mimascota.util

import android.app.Application
import android.util.Log

/**
 * MiMascotaApplication: Clase Application personalizada
 *
 * Esta clase se ejecuta al iniciar la app, antes que cualquier Activity
 * Es ideal para inicializar configuraciones globales
 */
class MiMascotaApplication : Application() {

    companion object {
        private const val TAG = "MiMascotaApp"
    }

    override fun onCreate() {
        super.onCreate()

        // Inicializar TokenManager
        TokenManager.init(this)

        // Mostrar configuración actual
        Log.d(TAG, "\n${AppConfig.getConfigInfo()}")

        Log.d(TAG, "✅ Aplicación iniciada correctamente")
    }
}

