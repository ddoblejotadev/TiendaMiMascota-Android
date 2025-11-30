package com.example.mimascota.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.mimascota.view.CompraRechazadaScreen
import com.example.mimascota.viewModel.AuthViewModel
import com.example.mimascota.viewModel.CartViewModel

class CompraRechazadaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tipoError = intent?.getStringExtra("tipoError") ?: "PAGO"
        setContent {
            CompraRechazadaWrapper(tipoError)
        }
    }
}

@Composable
fun CompraRechazadaWrapper(tipoError: String) {
    val navController = rememberNavController()
    // Obtener ViewModels en scope de Activity
    val cartViewModel: CartViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    CompraRechazadaScreen(navController, tipoError, cartViewModel, authViewModel)
}

