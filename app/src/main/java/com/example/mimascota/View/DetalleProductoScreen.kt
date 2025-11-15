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
            val cantidadEnCarrito = carrito.find { it.producto.producto_id == p.producto_id }?.cantidad ?: 0
            val stockDisponible = (p.stock ?: 0) - cantidadEnCarrito

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val painter = rememberAsyncImagePainter(p.imageUrl)
                Image(
                    painter = painter,
                    contentDescription = p.producto_nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Text(text = p.producto_nombre, style = MaterialTheme.typography.titleLarge)
                Text(text = "$${p.price}", style = MaterialTheme.typography.titleMedium)

                // Mostrar stock disponible
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            cantidadEnCarrito > (p.stock ?: 0) -> MaterialTheme.colorScheme.errorContainer
                            stockDisponible <= 5 -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.secondaryContainer
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Stock total: ${p.stock ?: 0} unidades",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (cantidadEnCarrito > (p.stock ?: 0)) {
                                Text("❌", style = MaterialTheme.typography.titleMedium)
                            } else if (stockDisponible <= 5 && stockDisponible >= 0) {
                                Text("⚠️", style = MaterialTheme.typography.titleMedium)
                            }
                        }

                        if (cantidadEnCarrito > 0) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Ya tienes $cantidadEnCarrito en tu carrito",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (cantidadEnCarrito > (p.stock ?: 0)) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            )
                        }

                        if (cantidadEnCarrito > (p.stock ?: 0)) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "⚠️ Excedes el stock disponible. Se validará al finalizar la compra.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Text(text = p.description ?: "", style = MaterialTheme.typography.bodyMedium)

                Button(
                    onClick = {
                        val resultado = cartViewModel.agregarAlCarrito(p)
                        scope.launch {
                            when (resultado) {
                                is com.example.mimascota.ViewModel.AgregarResultado.Exito -> {
                                    snackbarHostState.showSnackbar(
                                        message = "✅ Producto agregado al carrito",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                                is com.example.mimascota.ViewModel.AgregarResultado.ExcedeStock -> {
                                    snackbarHostState.showSnackbar(
                                        message = "⚠️ Producto agregado • Stock limitado: ${resultado.stockDisponible} disponibles (tienes ${resultado.cantidadEnCarrito} en carrito)",
                                        duration = SnackbarDuration.Long
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Agregar al carrito")
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
