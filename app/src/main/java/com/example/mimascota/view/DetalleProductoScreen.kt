package com.example.mimascota.view

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mimascota.model.Producto
import com.example.mimascota.util.CurrencyUtils

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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
            Text(text = CurrencyUtils.formatAsCLP(producto.price), style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Stock del producto
            Text(text = "Stock: $stockDisponible", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))

            // --- Sección de Etiquetas de Recomendación ---
            val scrollState = rememberScrollState()
            Row(modifier = Modifier.horizontalScroll(scrollState)) {
                producto.tipoMascota?.takeIf { it.isNotBlank() }?.let {
                    ChipInfo(icon = Icons.Default.Pets, text = it)
                }
                producto.raza?.takeIf { it.isNotBlank() }?.let {
                    ChipInfo(icon = Icons.Default.Pets, text = it) // Puedes usar otro ícono para raza
                }
                producto.edad?.takeIf { it.isNotBlank() }?.let {
                    ChipInfo(icon = Icons.Default.Cake, text = it)
                }
                producto.pesoMascota?.let {
                    ChipInfo(icon = Icons.Default.MonitorWeight, text = "${it}kg")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Descripción del producto
            producto.description?.let {
                if(it.isNotBlank()){
                    Text(text = "Descripción", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = it, style = MaterialTheme.typography.bodyLarge)
                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChipInfo(icon: ImageVector, text: String) {
    SuggestionChip(
        modifier = Modifier.padding(end = 8.dp),
        onClick = { /* No action */ },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(SuggestionChipDefaults.IconSize)
            )
        },
        label = { Text(text) }
    )
}
