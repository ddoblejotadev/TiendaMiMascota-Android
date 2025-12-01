package com.example.mimascota.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mimascota.R
import com.example.mimascota.viewModel.CatalogoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(navController: NavController, catalogoViewModel: CatalogoViewModel) {
    val productos by catalogoViewModel.productos.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Navigate to create product screen */ },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
            }
        }
    ) { paddingValues ->
        if (productos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No hay productos en el catálogo.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(productos) { producto ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AsyncImage(
                                model = producto.imageUrl,
                                contentDescription = producto.producto_nombre,
                                placeholder = painterResource(id = R.drawable.placeholder_product),
                                error = painterResource(id = R.drawable.placeholder_product),
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(MaterialTheme.shapes.medium),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(producto.producto_nombre, style = MaterialTheme.typography.titleMedium)
                                Text("Precio: $${producto.price}", style = MaterialTheme.typography.bodyMedium)
                                Text("Stock: ${producto.stock ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                            }

                            Column {
                                IconButton(onClick = { /* TODO: navController.navigate("admin_product_edit/${producto.producto_id}") */ }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar Producto", tint = MaterialTheme.colorScheme.secondary)
                                }
                                IconButton(onClick = { catalogoViewModel.deleteProducto(producto.producto_id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar Producto", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}