package com.example.mimascota.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimascota.R
import com.example.mimascota.model.Producto
import com.example.mimascota.viewModel.CatalogoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(navController: NavController, catalogoViewModel: CatalogoViewModel = viewModel()) {
    val productos by catalogoViewModel.productos.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("agregarProducto") }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.agregar_producto))
            }
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(productos) { producto ->
                ProductListItem(producto, navController, catalogoViewModel)
            }
        }
    }
}

@Composable
fun ProductListItem(producto: Producto, navController: NavController, viewModel: CatalogoViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.producto_nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    stringResource(id = R.string.product_stock, producto.stock ?: 0),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = {
                navController.navigate("agregarProducto?id=${producto.producto_id}")
            }) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.editar_label))
            }
            IconButton(onClick = {
                val id = producto.producto_id
                viewModel.deleteProducto(id)
            }) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.eliminar_label))
            }
        }
    }
}