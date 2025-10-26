package com.example.mimascota.View

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mimascota.Model.Producto
import com.example.mimascota.ViewModel.CartViewModel
import com.example.mimascota.ViewModel.CatalogoViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(navController: NavController, viewModel: CatalogoViewModel, cartViewModel: CartViewModel) {
    val context = LocalContext.current
    val productos by viewModel.productos.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val carrito by cartViewModel.carrito.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.cargarProductos(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo") },
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
        floatingActionButton = {
            // Mostrar botón solo si hay productos en el carrito
            if (carrito.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate("Carrito") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Ir a pagar"
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Ir a pagar",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$${String.format(Locale("es", "CL"), "%,d", cartViewModel.getTotal())}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 8.dp,
                        end = 8.dp,
                        top = 8.dp,
                        bottom = if (carrito.isNotEmpty()) 80.dp else 8.dp // Espacio para el FAB
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(productos, key = { it.id }) { producto ->
                        // Calcular cantidad desde el estado del carrito
                        val cantidad = carrito.find { it.producto.id == producto.id }?.cantidad ?: 0

                        ProductoCard(
                            producto = producto,
                            cantidad = cantidad,
                            onClick = { navController.navigate("Detalle/${producto.id}") },
                            onAgregar = {
                                val resultado = cartViewModel.agregarAlCarrito(producto)
                                scope.launch {
                                    when (resultado) {
                                        is com.example.mimascota.ViewModel.AgregarResultado.Exito -> {
                                            snackbarHostState.showSnackbar(
                                                message = "✅ ${producto.name} agregado",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                        is com.example.mimascota.ViewModel.AgregarResultado.ExcedeStock -> {
                                            snackbarHostState.showSnackbar(
                                                message = "⚠️ ${producto.name} agregado • Stock limitado: ${resultado.stockDisponible} disponibles (tienes ${resultado.cantidadEnCarrito} en carrito)",
                                                duration = SnackbarDuration.Long
                                            )
                                        }
                                    }
                                }
                            },
                            onDisminuir = {
                                cartViewModel.disminuirCantidad(producto)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "➖ ${producto.name} disminuido",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Suppress("DEPRECATION")
@Composable
fun ProductoCard(
    producto: Producto,
    cantidad: Int,
    onClick: () -> Unit,
    onAgregar: () -> Unit,
    onDisminuir: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onClick() }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(producto.imageUrl),
                    contentDescription = producto.name,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(producto.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "$${String.format(Locale("es", "CL"), "%,d", producto.price)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    // Mostrar stock disponible
                    val stockDisponible = producto.stock - cantidad
                    Text(
                        text = if (stockDisponible > 0) {
                            "Stock: $stockDisponible disponible${if (stockDisponible <= 5) " ⚠️" else ""}"
                        } else {
                            "Sin stock disponible ❌"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            stockDisponible == 0 -> MaterialTheme.colorScheme.error
                            stockDisponible <= 5 -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontWeight = if (stockDisponible <= 5) FontWeight.Bold else FontWeight.Normal
                    )
                    producto.description?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                    }
                }
            }

            // Controles de cantidad
            if (cantidad > 0) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onDisminuir,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Disminuir",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(
                                text = "$cantidad",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = onAgregar,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Agregar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Text(
                        text = "Total: $${String.format(Locale("es", "CL"), "%,d", producto.price * cantidad)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onAgregar,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Agregar al carrito")
                }
            }
        }
    }
}

