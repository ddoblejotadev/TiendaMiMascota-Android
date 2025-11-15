package com.example.mimascota.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mimascota.R
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.ui.fragment.LoginFragment
import com.example.mimascota.ui.fragment.ProductoListaFragment

/**
 * ProductoListaActivity: Activity principal que maneja autenticación JWT
 *
 * Flujo:
 * - Inicializa RetrofitClient con TokenManager
 * - Verifica si hay usuario logueado
 * - Si está logueado -> ProductoListaFragment
 * - Si no está logueado -> LoginFragment
 * - En caso de 401 Unauthorized -> vuelve a LoginFragment
 */
class ProductoListaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto_lista)

        // Inicializar RetrofitClient con callback para manejar 401 Unauthorized
        RetrofitClient.init(this) {
            // Callback ejecutado cuando el backend responde 401
            runOnUiThread {
                navigateToLogin()
            }
        }

        if (savedInstanceState == null) {
            // Verificar si hay usuario logueado
            val tokenManager = RetrofitClient.getTokenManager()

            if (tokenManager.isLoggedIn()) {
                // Mostrar lista de productos
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProductoListaFragment())
                    .commit()
            } else {
                // Mostrar login
                navigateToLogin()
            }
        }
    }

    /**
     * Navega a la pantalla de login
     */
    private fun navigateToLogin() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LoginFragment())
            .commit()
    }
}
