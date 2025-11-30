package com.example.mimascota.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimascota.viewModel.AuthViewModel
import com.example.mimascota.viewModel.CartViewModel

@Composable
fun CompraRechazadaScreenWrapper(
    navController: NavController,
    tipoError: String,
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel
) {
    CompraRechazadaScreen(navController, tipoError, cartViewModel, authViewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompraRechazadaScreen(
    navController: NavController,
    tipoError: String,
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel
) {
    val mensaje = when (tipoError) {
        "PAGO" -> "El pago fue rechazado."
        "STOCK" -> "No hay suficiente stock de uno o más productos."
        else -> "Ocurrió un error inesperado."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compra Rechazada") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("Catalogo") }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Lo sentimos", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(mensaje)
        }
    }
}
