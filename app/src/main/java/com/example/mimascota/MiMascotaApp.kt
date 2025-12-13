package com.example.mimascota

import android.app.Application
import android.util.Log
import coil.Coil
import coil.ImageLoader
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.util.AppConfig
import com.example.mimascota.util.Base64Fetcher

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

        // Configurar Coil para que soporte Base64
        val imageLoader = ImageLoader.Builder(this)
            .components {
                add(Base64Fetcher.Factory())
            }
            .build()
        Coil.setImageLoader(imageLoader)

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
