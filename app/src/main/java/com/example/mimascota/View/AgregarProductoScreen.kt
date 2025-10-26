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

// Pantalla para agregar producto (solo visual, sin funcionalidad)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoScreen(navController: NavController) {
    // Estados para los campos del formulario
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }

    // Estado para mostrar mensaje
    var mostrarMensaje by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Agregar Producto")
                },
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
            // Card informativa
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "📝 Formulario visual (sin funcionalidad de guardado)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Información del Producto",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(20.dp))

                    // Campo: Nombre del producto
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre del producto *") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = "Nombre"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    // Campo: Precio
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio (CLP) *") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = "Precio"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("Ej: 9990") }
                    )

                    Spacer(Modifier.height(12.dp))

                    // Campo: Stock
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Stock disponible *") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Inventory,
                                contentDescription = "Stock"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("Ej: 10") }
                    )

                    Spacer(Modifier.height(12.dp))

                    // Campo: Categoría
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = { categoria = it },
                        label = { Text("Categoría *") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = "Categoría"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("Ej: Juguete, Alimento, Higiene") }
                    )

                    Spacer(Modifier.height(12.dp))

                    // Campo: Descripción
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "Descripción"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("Describe el producto...") }
                    )

                    Spacer(Modifier.height(12.dp))

                    // Campo: URL de la imagen
                    OutlinedTextField(
                        value = imagenUrl,
                        onValueChange = { imagenUrl = it },
                        label = { Text("URL de la imagen") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Imagen"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("https://ejemplo.com/imagen.jpg") }
                    )

                    Spacer(Modifier.height(8.dp))

                    // Nota sobre campos obligatorios
                    Text(
                        text = "* Campos obligatorios",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón: Cancelar
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Cancelar")
                }

                // Botón: Guardar (solo visual)
                Button(
                    onClick = {
                        // Solo mostrar mensaje, no guarda realmente
                        mostrarMensaje = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = nombre.isNotBlank() &&
                              precio.isNotBlank() &&
                              stock.isNotBlank() &&
                              categoria.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Guardar")
                }
            }

            // Mostrar mensaje informativo cuando se hace clic en guardar
            if (mostrarMensaje) {
                Spacer(Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "✅ Producto registrado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "(Funcionalidad visual - No se guardó en la base de datos)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        TextButton(
                            onClick = { navController.popBackStack() }
                        ) {
                            Text("Volver al Panel Administrativo")
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

