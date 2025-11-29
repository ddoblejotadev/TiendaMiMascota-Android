package com.example.mimascota.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimascota.model.Producto
import com.example.mimascota.viewModel.CatalogoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoScreen(
    navController: NavController,
    viewModel: CatalogoViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
            OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") })
            OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") })
            OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") })
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("URL de Imagen") })

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val priceDouble = precio.toDoubleOrNull() ?: 0.0
                val nuevoProducto = Producto(
                    producto_nombre = nombre,
                    price = priceDouble,
                    stock = stock.toIntOrNull() ?: 0,
                    category = categoria,
                    description = descripcion,
                    imageUrl = imageUrl
                )
                viewModel.createProducto(nuevoProducto, "General") // Asumiendo 'General' como tipo por defecto
                navController.popBackStack()
            }) {
                Text("Guardar Producto")
            }
        }
    }
}
