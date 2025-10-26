package com.example.mimascota.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mimascota.ViewModel.CartViewModel
import com.example.mimascota.ViewModel.CatalogoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(navController: NavController, productoId: Int, viewModel: CatalogoViewModel, cartViewModel: CartViewModel) {
    val producto = remember { viewModel.buscarProductoPorId(productoId) }
    val carrito by cartViewModel.carrito.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(producto?.name ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (carrito.isNotEmpty()) {
                                Badge {
                                    Text("${cartViewModel.getTotalItems()}")
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = { navController.navigate("Carrito") }) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Carrito"
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        producto?.let { p ->
            // Calcular cantidad en carrito y stock disponible
            val cantidadEnCarrito = carrito.find { it.producto.id == p.id }?.cantidad ?: 0
            val stockDisponible = p.stock - cantidadEnCarrito

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val painter = rememberAsyncImagePainter(p.imageUrl)
                Image(
                    painter = painter,
                    contentDescription = p.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Text(text = p.name, style = MaterialTheme.typography.titleLarge)
                Text(text = "$${p.price}", style = MaterialTheme.typography.titleMedium)

                // Mostrar stock disponible
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            stockDisponible == 0 -> MaterialTheme.colorScheme.errorContainer
                            stockDisponible <= 5 -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.secondaryContainer
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (stockDisponible > 0) {
                                "Stock disponible: $stockDisponible unidades"
                            } else {
                                "Sin stock disponible"
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (stockDisponible <= 5 && stockDisponible > 0) {
                            Text("⚠️", style = MaterialTheme.typography.titleMedium)
                        } else if (stockDisponible == 0) {
                            Text("❌", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                if (cantidadEnCarrito > 0) {
                    Text(
                        text = "Ya tienes $cantidadEnCarrito en tu carrito",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(text = p.description ?: "", style = MaterialTheme.typography.bodyMedium)

                Button(
                    onClick = {
                        val agregado = cartViewModel.agregarAlCarrito(p)
                        scope.launch {
                            if (agregado) {
                                snackbarHostState.showSnackbar(
                                    message = "✅ Producto agregado al carrito",
                                    duration = SnackbarDuration.Short
                                )
                            } else {
                                snackbarHostState.showSnackbar(
                                    message = "⚠️ No hay más stock disponible",
                                    duration = SnackbarDuration.Long
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = stockDisponible > 0  // Deshabilitar si no hay stock
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (stockDisponible > 0) "Agregar al carrito" else "Sin stock")
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Producto no encontrado")
        }
    }
}
