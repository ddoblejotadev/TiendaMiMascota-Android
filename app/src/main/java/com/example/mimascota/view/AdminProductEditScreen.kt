package com.example.mimascota.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimascota.model.Producto
import com.example.mimascota.viewModel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductEditScreen(navController: NavController, adminViewModel: AdminViewModel, productId: Int) {
    val producto by remember(productId) {
        derivedStateOf { adminViewModel.productos.value.find { it.producto_id == productId } }
    }

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    LaunchedEffect(producto) {
        producto?.let {
            nombre = it.producto_nombre
            descripcion = it.description ?: ""
            precio = it.price.toString()
            stock = it.stock?.toString() ?: "0"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) {
        if (producto == null) {
            Box(modifier = Modifier.fillMaxSize().padding(it)) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // Para evitar que el botón se oculte
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del producto") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        producto?.let { productoOriginal ->
                            // Corregido: No usar .copy(), crear un nuevo objeto Producto
                            val productoActualizado = Producto(
                                producto_id = productoOriginal.producto_id,
                                producto_nombre = nombre,
                                description = descripcion,
                                price = precio.toDoubleOrNull() ?: productoOriginal.price,
                                stock = stock.toIntOrNull() ?: productoOriginal.stock,
                                // Mantener los campos no editables del producto original
                                imageUrl = productoOriginal.imageUrl,
                                category = productoOriginal.category,
                                destacado = productoOriginal.destacado,
                                valoracion = productoOriginal.valoracion,
                                precioAnterior = productoOriginal.precioAnterior,
                                marca = productoOriginal.marca,
                                peso = productoOriginal.peso,
                                material = productoOriginal.material,
                                tamano = productoOriginal.tamano,
                                tipoHigiene = productoOriginal.tipoHigiene,
                                fragancia = productoOriginal.fragancia,
                                tipo = productoOriginal.tipo,
                                tipoAccesorio = productoOriginal.tipoAccesorio
                            )
                            adminViewModel.updateProducto(productId, productoActualizado)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Cambios")
                }
            }
        }
    }
}