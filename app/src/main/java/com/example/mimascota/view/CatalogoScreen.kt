package com.example.mimascota.view

import android.util.Log
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mimascota.viewModel.CartViewModel
import com.example.mimascota.viewModel.CatalogoViewModel
import androidx.navigation.NavController
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    navController: NavController, 
    viewModel: CatalogoViewModel, 
    cartViewModel: CartViewModel, 
    categoriaInicial: String?
) {
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

    LaunchedEffect(categoriaInicial) {
        if (categoriaInicial != null) {
            selectedCategoria = categoriaInicial
        }
    }

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
