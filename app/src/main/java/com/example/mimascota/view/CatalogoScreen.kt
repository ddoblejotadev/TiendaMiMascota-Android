package com.example.mimascota.view

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mimascota.model.Producto
import com.example.mimascota.util.AppConfig
import com.example.mimascota.util.CurrencyUtils
import com.example.mimascota.viewModel.CartViewModel
import com.example.mimascota.viewModel.CatalogoViewModel
import android.widget.Toast
import androidx.navigation.NavController
import java.util.Locale
import androidx.compose.ui.draw.scale
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(navController: NavController, viewModel: CatalogoViewModel, cartViewModel: CartViewModel) {
    val productos by viewModel.productos.collectAsState()

    // Obtener items del carrito para badges y cantidades por producto
    val cartItems by cartViewModel.items.collectAsState()
    val totalEnCarrito = cartItems.sumOf { it.cantidad }
    val cantidadesPorProducto = remember(cartItems) { cartItems.associate { it.producto.producto_id to it.cantidad } }

    // --- CATEGORÍAS calculadas localmente a partir de productos ---
    val categorias = remember(productos) {
        val cats = productos.map { it.category.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .sortedBy { it.lowercase(Locale.getDefault()) }
        listOf("Todas") + cats
    }

    var selectedCategoria by remember { mutableStateOf("Todas") }

    // Filtrado local por categoría
    val productosMostrados = remember(productos, selectedCategoria) {
        if (selectedCategoria == "Todas") productos
        else productos.filter { it.category.equals(selectedCategoria, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = com.example.mimascota.R.string.productos)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = com.example.mimascota.R.string.volver_label))
                    }
                },
                actions = {
                    TextButton(onClick = { navController.navigate("Recomendaciones") }) {
                        Text("Recomendaciones")
                    }
                }
            )
        },
        floatingActionButton = {
            // Botón flotante para ir al carrito con badge si hay items
            BadgedBox(badge = {
                if (totalEnCarrito > 0) {
                    Badge { Text(totalEnCarrito.toString()) }
                }
            }) {
                FloatingActionButton(onClick = { navController.navigate("Carrito") }) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = stringResource(id = com.example.mimascota.R.string.ver_carrito)
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(8.dp)) {

            // --- Chips horizontales para categorías (más moderno) ---
            val scrollState = rememberScrollState()
            Row(modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(bottom = 8.dp)
            ) {
                categorias.forEach { categoria ->
                    val selected = categoria == selectedCategoria
                    FilterChip(
                        selected = selected,
                        onClick = { selectedCategoria = categoria },
                        label = { Text(categoria) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            // Grid de productos filtrados
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(productosMostrados, key = { it.producto_id }) { producto ->
                    val qtyInCart = cantidadesPorProducto[producto.producto_id] ?: 0
                    ProductoCard(producto = producto, onProductoClick = {
                        // Navegar al detalle usando id seguro
                        navController.navigate("Detalle/${producto.producto_id}")
                    }, onAddToCart = {
                        Log.d("CatalogoScreen", "Agregar al carrito pedido para id=${producto.producto_id}")
                        cartViewModel.agregarAlCarrito(producto)
                    }, cartQuantity = qtyInCart)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoCard(producto: Producto, onProductoClick: () -> Unit, onAddToCart: () -> Unit, cartQuantity: Int = 0) {
    val context = LocalContext.current
    val agregadoMsg = stringResource(id = com.example.mimascota.R.string.producto_agregado)
    val sinStockMsg = stringResource(id = com.example.mimascota.R.string.stock_insuficiente)
    val stockVal = producto.stock
    val estaAgotado = stockVal != null && stockVal <= 0

    // small press animation when user adds to cart
    val pressed = remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (pressed.value) 0.96f else 1f)

    // coroutine scope para ejecutar delays desde onClick
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .padding(8.dp),
        onClick = onProductoClick
    ) {
        Column(modifier = Modifier.padding(8.dp).scale(scale)) {
            // Imagen del producto usando Coil Compose
            val imageUrl = AppConfig.toAbsoluteImageUrl(producto.imageUrl)
            ProductImage(
                imageUrl = imageUrl,
                contentDescription = producto.producto_nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(producto.producto_nombre, style = MaterialTheme.typography.titleMedium)
            // Mostrar stock (null = N/D)
            if (stockVal == null) {
                Text(stringResource(id = com.example.mimascota.R.string.no_products), style = MaterialTheme.typography.bodySmall)
            } else if (stockVal <= 0) {
                Text(stringResource(id = com.example.mimascota.R.string.stock_insuficiente), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            } else {
                Text(String.format(stringResource(id = com.example.mimascota.R.string.product_stock), stockVal), style = MaterialTheme.typography.bodySmall)
            }

            // Precio (producto.price ya es Double)
            Text(CurrencyUtils.formatAsCLP(producto.price), style = MaterialTheme.typography.bodyMedium)

            // Mostrar cantidad en carrito si existe
            if (cartQuantity > 0) {
                Text(stringResource(id = com.example.mimascota.R.string.en_carrito, cartQuantity), style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = {
                    if (estaAgotado) {
                        Toast.makeText(context, sinStockMsg, Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    pressed.value = true
                    onAddToCart()
                    // efecto visual breve usando coroutineScope
                    coroutineScope.launch {
                        kotlinx.coroutines.delay(180)
                        pressed.value = false
                    }
                    Toast.makeText(context, agregadoMsg, Toast.LENGTH_SHORT).show()
                },
                enabled = !estaAgotado,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = com.example.mimascota.R.string.add_to_cart))
            }
        }
    }
}
