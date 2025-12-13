package com.example.mimascota.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mimascota.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    navController: NavController,
    producto: Producto,
    onAddToCart: (Producto, Int) -> Unit
) {
    var cantidad by remember { mutableStateOf(1) }
    val stockDisponible = producto.stock ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(producto.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Imagen del producto
            ProductImage(
                imageUrl = producto.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Precio del producto
            Text(text = "Precio: $${producto.price}", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // Stock del producto
            Text(text = "Stock: $stockDisponible", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // Descripción del producto
            producto.description?.let {
                Text(text = "Descripción: $it", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))


            // Selector de cantidad
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { if (cantidad > 1) cantidad-- }) {
                    Text("-")
                }
                Text(
                    text = "$cantidad",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Button(onClick = { if (cantidad < stockDisponible) cantidad++ }) {
                    Text("+")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón agregar al carrito
            Button(
                onClick = {
                    onAddToCart(producto, cantidad)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = stockDisponible > 0
            ) {
                Text(if (stockDisponible > 0) "Agregar al carrito" else "Sin Stock")
            }
        }
    }
}
