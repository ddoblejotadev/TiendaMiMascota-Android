package com.example.mimascota.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimascota.model.Producto
import com.example.mimascota.viewModel.CatalogoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoScreen(
    navController: NavController,
    viewModel: CatalogoViewModel,
    productoId: Int? = null
) {
    // Determinar si estamos creando o editando
    val isEditing = productoId != null
    val productoToEdit = if (isEditing) viewModel.productos.collectAsState().value.find { it.producto_id == productoId } else null

    // Estados para los campos del formulario, inicializados si estamos editando
    var nombre by remember { mutableStateOf(productoToEdit?.producto_nombre ?: "") }
    var precio by remember { mutableStateOf(productoToEdit?.price?.toString() ?: "") }
    var stock by remember { mutableStateOf(productoToEdit?.stock?.toString() ?: "") }
    var categoria by remember { mutableStateOf(productoToEdit?.category ?: "") }
    var descripcion by remember { mutableStateOf(productoToEdit?.description ?: "") }
    var imageUrl by remember { mutableStateOf(productoToEdit?.imageUrl ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Producto" else "Agregar Producto") },
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
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("URL de Imagen") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val priceDouble = precio.toDoubleOrNull() ?: 0.0
                    val stockInt = stock.toIntOrNull() ?: 0

                    val producto = Producto(
                        producto_id = productoId ?: 0, // Si es nuevo, el id no importa, se generará en el backend
                        producto_nombre = nombre,
                        price = priceDouble,
                        stock = stockInt,
                        category = categoria,
                        description = descripcion,
                        imageUrl = imageUrl
                    )

                    if (isEditing) {
                        viewModel.updateProducto(producto.producto_id, producto)
                    } else {
                        viewModel.createProducto(producto)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Guardar Cambios" else "Agregar Producto")
            }
        }
    }
}
