package com.example.mimascota.View

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mimascota.Model.Producto
import com.example.mimascota.ViewModel.CatalogoViewModel
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import android.annotation.SuppressLint
import java.util.Locale

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import com.example.mimascota.ViewModel.CartViewModel
import java.util.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight

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
                                    Text("${cartViewModel.getTotalItems()}")
                                    Text("${carrito.size}")
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
        Box(Modifier.padding(padding)) {
            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(productos, key = { it.id }) { producto ->
                            producto = producto,
                            cantidad = cartViewModel.getCantidadProducto(producto.id),
                            onClick = { navController.navigate("Detalle/${producto.id}") },
                            onAgregar = {
                            onClick = { navController.navigate("Detalle/${producto.id}") },
                            onAgregar = {
                                cartViewModel.agregarAlCarrito(producto)
                                        message = "✅ ${producto.name} agregado",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            onDisminuir = {
                                cartViewModel.disminuirCantidad(producto)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "➖ ${producto.name} disminuido",
                                    snackbarHostState.showSnackbar(
                                        message = "✅ ${producto.name} agregado",
                                        duration = SnackbarDuration.Short
                                    )
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
    producto: Producto,
    cantidad: Int,
    onClick: () -> Unit,
    onAgregar: () -> Unit,
    onDisminuir: () -> Unit
) {
            }
        modifier = Modifier.fillMaxWidth(),

@SuppressLint("DefaultLocale")
@Composable
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
                        text = "$${String.format(Locale("es", "CL"), "%,d", producto.price.toInt())}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    producto.description?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                    }
                    contentDescription = producto.name,
                    modifier = Modifier.size(80.dp),

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
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
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
                        text = "Total: $${String.format(Locale("es", "CL"), "%,d", (producto.price * cantidad).toInt())}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    Text(producto.name, style = MaterialTheme.typography.titleMedium)
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
                    Text(
                        text = "$${String.format(Locale("es", "CL"), "%,d", producto.price.toInt())}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
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
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
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
                        text = "Total: $${String.format(Locale("es", "CL"), "%,d", (producto.price * cantidad).toInt())}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
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

