package com.example.mimascota.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mimascota.viewModel.CartViewModel
import com.example.mimascota.viewModel.RecomendacionesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecomendacionesScreen(
    navController: NavController,
    recomendacionesViewModel: RecomendacionesViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val tiposAnimal by recomendacionesViewModel.tiposAnimal.collectAsState(initial = emptyList())
    val categorias by recomendacionesViewModel.categorias.collectAsState(initial = emptyList())

    val tipoAnimalSeleccionado by recomendacionesViewModel.tipoAnimalSeleccionado.collectAsState()
    val categoriaSeleccionada by recomendacionesViewModel.categoriaSeleccionada.collectAsState()
    val raza by recomendacionesViewModel.raza.collectAsState()
    val edad by recomendacionesViewModel.edad.collectAsState()
    val peso by recomendacionesViewModel.peso.collectAsState()

    val recomendaciones by recomendacionesViewModel.recomendaciones.collectAsState()
    val cartItems by cartViewModel.items.collectAsState()
    val cantidadesPorProducto = remember(cartItems) { cartItems.associate { it.producto.producto_id to it.cantidad } }
    var submitted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recomendaciones Inteligentes") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
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
            Dropdown(label = "Tipo de Animal", options = tiposAnimal, selected = tipoAnimalSeleccionado, onSelected = recomendacionesViewModel::onTipoAnimalChange)
            Spacer(Modifier.height(8.dp))
            Dropdown(label = "Categoría", options = categorias, selected = categoriaSeleccionada, onSelected = recomendacionesViewModel::onCategoriaChange)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = raza, onValueChange = recomendacionesViewModel::onRazaChange, label = { Text("Raza") }, modifier = Modifier.fillMaxWidth())            
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = edad, onValueChange = recomendacionesViewModel::onEdadChange, label = { Text("Edad (años)") }, modifier = Modifier.fillMaxWidth())            
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = peso, onValueChange = recomendacionesViewModel::onPesoChange, label = { Text("Peso (kg)") }, modifier = Modifier.fillMaxWidth())            
            Spacer(Modifier.height(16.dp))
            
            Button(onClick = { 
                submitted = true
                recomendacionesViewModel.buscarRecomendaciones() 
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Obtener Recomendaciones")
            }
            
            Spacer(Modifier.height(16.dp))
            
            if (submitted) {
                if (recomendaciones.isEmpty()) {
                    Text("No se encontraron productos con esos criterios.")
                } else {
                    LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(recomendaciones) { producto ->
                            val qtyInCart = cantidadesPorProducto[producto.producto_id] ?: 0
                            ProductoCard(
                                producto = producto,
                                onProductoClick = { navController.navigate("Detalle/${producto.producto_id}?from=Recomendaciones") },
                                onAddToCart = { cartViewModel.agregarAlCarrito(producto) },
                                cartQuantity = qtyInCart
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Dropdown(label: String, options: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
