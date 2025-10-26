package com.example.mimascota.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimascota.ViewModel.CartViewModel
import com.example.mimascota.Model.CartItem
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(navController: NavController, cartViewModel: CartViewModel) {
    val carrito by cartViewModel.carrito.collectAsState()
    val total = cartViewModel.getTotal()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🛒 Carrito de Compras") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (carrito.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Tu carrito está vacío 🐾", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { navController.navigate("Catalogo") }) {
                        Text("Ir al catálogo")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(carrito) { cartItem ->
                        CartItemCard(
                            cartItem = cartItem,
                            onAgregar = { cartViewModel.agregarAlCarrito(cartItem.producto) },
                            onDisminuir = { cartViewModel.disminuirCantidad(cartItem.producto) },
                            onEliminar = { cartViewModel.eliminarDelCarrito(cartItem.producto) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total items:", style = MaterialTheme.typography.titleMedium)
                            Text("${cartViewModel.getTotalItems()}", style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total a pagar:", style = MaterialTheme.typography.titleLarge)
                            Text(
                                "$${String.format(Locale("es", "CL"), "%,d", total)}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // Intentar procesar la compra (puede fallar aleatoriamente)
                        val error = cartViewModel.intentarProcesarCompra()

                        if (error == null) {
                            // Compra exitosa - navegar a pantalla de éxito
                            navController.navigate("compraExitosa") {
                                popUpTo("Carrito") { inclusive = true }
                            }
                        } else {
                            // Compra rechazada - navegar a pantalla de error
                            navController.navigate("compraRechazada/$error") {
                                popUpTo("Carrito") { inclusive = false }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Finalizar compra 🐶")
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onAgregar: () -> Unit,
    onDisminuir: () -> Unit,
    onEliminar: () -> Unit
) {
    // Verificar si excede el stock
    val excedeStock = cartItem.cantidad > cartItem.producto.stock

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (excedeStock) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text(cartItem.producto.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Precio unitario: $${String.format(Locale("es", "CL"), "%,d", cartItem.producto.price)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "Subtotal: $${String.format(Locale("es", "CL"), "%,d", cartItem.subtotal)}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Mostrar advertencia si excede stock
                    if (excedeStock) {
                        Spacer(Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Advertencia",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Stock insuficiente (${cartItem.producto.stock} disponibles)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        }
                    }
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onDisminuir) {
                    Icon(Icons.Default.Remove, contentDescription = "Disminuir", tint = MaterialTheme.colorScheme.error)
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "${cartItem.cantidad}",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                IconButton(onClick = onAgregar) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}