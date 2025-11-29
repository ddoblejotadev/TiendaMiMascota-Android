package com.example.mimascota.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimascota.Model.Producto
import com.example.mimascota.ViewModel.CatalogoViewModel

// Pantalla para agregar producto (ahora funcional)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoScreen(navController: NavController, viewModel: CatalogoViewModel, productoEdit: Producto? = null) {
    // Si productoEdit no fue pasado, intentar obtenerlo desde viewModel.selectedProductId
    val selectedId by viewModel.selectedProductId.collectAsState()
    val editProduct = productoEdit ?: selectedId?.let { viewModel.getProductoFromCache(it) }

    // Estados para los campos del formulario, con valores por defecto para edición
    var nombre by remember { mutableStateOf(editProduct?.name ?: "") }
    var precio by remember { mutableStateOf(editProduct?.price?.toString() ?: "") }
    var stock by remember { mutableStateOf(editProduct?.stock?.toString() ?: "") }
    var categoria by remember { mutableStateOf(editProduct?.category ?: "") }
    var descripcion by remember { mutableStateOf(editProduct?.description ?: "") }
    var imagenUrl by remember { mutableStateOf(editProduct?.imageUrl ?: "") }

    // Estados para mensajes de error y éxito
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var successMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productoEdit == null) "Agregar Producto" else "Editar Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Formulario
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del producto *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it.filter { ch -> ch.isDigit() } },
                label = { Text("Precio (CLP) *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Ej: 9990") }
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it.filter { ch -> ch.isDigit() } },
                label = { Text("Stock *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Ej: 10") }
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = { Text("Categoría *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Ej: Juguete, Alimento, Higiene") }
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Describe el producto...") }
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = imagenUrl,
                onValueChange = { imagenUrl = it },
                label = { Text("URL de la imagen") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("https://ejemplo.com/imagen.jpg") }
            )

            Spacer(Modifier.height(16.dp))

            // Mostrar mensajes de error o éxito
            errorMsg?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            successMsg?.let { Text(it, color = MaterialTheme.colorScheme.primary) }

            Spacer(Modifier.height(12.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón: Cancelar
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar")
                }

                // Botón: Guardar
                Button(
                    onClick = {
                        // Validaciones
                        if (nombre.isBlank() || precio.isBlank() || stock.isBlank() || categoria.isBlank()) {
                            errorMsg = "Completa todos los campos obligatorios"
                            return@Button
                        }

                        val producto = Producto(
                            producto_id = productoEdit?.producto_id ?: 0,
                            producto_nombre = nombre,
                            price = precio.toIntOrNull() ?: 0,
                            category = categoria,
                            description = descripcion.ifBlank { null },
                            imageUrl = imagenUrl.ifBlank { null },
                            stock = stock.toIntOrNull() ?: 0
                        )

                        // Crear o actualizar producto según corresponda
                        if (productoEdit == null) {
                            viewModel.createProducto(producto) { success, err ->
                                if (success) {
                                    successMsg = "Producto creado exitosamente"
                                    navController.popBackStack()
                                } else {
                                    errorMsg = err ?: "Error al crear producto"
                                }
                            }
                        } else {
                            viewModel.updateProducto(producto.id, producto) { success, err ->
                                if (success) {
                                    successMsg = "Producto actualizado"
                                    navController.popBackStack()
                                } else {
                                    errorMsg = err ?: "Error al actualizar"
                                }
                            }
                        }

                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}
