package com.example.mimascota.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimascota.R
import com.example.mimascota.model.CartItem
import com.example.mimascota.viewModel.AuthViewModel
import com.example.mimascota.viewModel.CartViewModel

@Composable
fun CompraExitosaScreenWrapper(
    navController: NavController,
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel
) {
    val items by cartViewModel.items.collectAsState()
    // Limpiar el carrito al entrar a la pantalla
    LaunchedEffect(Unit) {
        cartViewModel.vaciarCarrito()
    }
    CompraExitosaScreen(navController, items, authViewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompraExitosaScreen(
    navController: NavController,
    items: List<CartItem>,
    authViewModel: AuthViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.compra_exitosa_title)) },
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
            Text(stringResource(id = R.string.thanks_for_purchase), style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(id = R.string.order_summary))
            LazyColumn {
                items(items) { item ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${item.cantidad}x ${item.producto.producto_nombre}")
                        val subtotal = item.producto.price * item.cantidad
                        Text(String.format("$%.2f", subtotal.toDouble()))
                    }
                }
            }
        }
    }
}
