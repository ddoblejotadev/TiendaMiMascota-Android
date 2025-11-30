package com.example.mimascota.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimascota.R
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
        "PAGO" -> stringResource(id = R.string.pago_rechazado)
        "STOCK" -> stringResource(id = R.string.stock_rechazado)
        else -> stringResource(id = R.string.error_inesperado)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.compra_rechazada_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("Catalogo") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.volver_label))
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
            Text(stringResource(id = R.string.lo_sentimos), style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(mensaje)
        }
    }
}
