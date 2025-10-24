package com.example.mimascota.View

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil.compose.rememberAsyncImagePainter
import com.example.mimascota.ViewModel.CatalogoViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mimascota.ViewModel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(productoId: Int, viewModel: CatalogoViewModel, cartViewModel: CartViewModel) {
    val producto = remember { viewModel.buscarProductoPorId(productoId) }
    var mensaje by remember { mutableStateOf("") }

    Scaffold(topBar = {
        TopAppBar(title = { Text(producto?.name ?: "Detalle") })
    }) { padding ->
        producto?.let { p ->
            Column(modifier = Modifier
                .padding(padding)
                .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val painter = rememberAsyncImagePainter(p.imageUrl)
                Image(painter = painter, contentDescription = p.name, modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                    contentScale = ContentScale.Crop)
                Text(text = p.name, style = MaterialTheme.typography.titleLarge)
                Text(text = "$${p.price}", style = MaterialTheme.typography.titleMedium)
                Text(text = p.description ?: "", style = MaterialTheme.typography.bodyMedium)
                Button(
                    onClick = {
                        cartViewModel.agregarAlCarrito(p)
                        mensaje = "Producto agregado al carrito 🛒"
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agregar al carrito")
                }
                if (mensaje.isNotEmpty()) {
                    Text(
                        text = mensaje,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Producto no encontrado")
        }
    }
}
