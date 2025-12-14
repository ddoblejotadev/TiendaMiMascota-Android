
package com.example.mimascota.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val tiposAnimal by recomendacionesViewModel.tiposAnimal.collectAsStateWithLifecycle()
    val categorias by recomendacionesViewModel.categorias.collectAsStateWithLifecycle()

    val tipoAnimalSeleccionado by recomendacionesViewModel.tipoAnimalSeleccionado.collectAsStateWithLifecycle()
    val categoriaSeleccionada by recomendacionesViewModel.categoriaSeleccionada.collectAsStateWithLifecycle()
    val razaSeleccionada by recomendacionesViewModel.raza.collectAsStateWithLifecycle()
    val edadSeleccionada by recomendacionesViewModel.edad.collectAsStateWithLifecycle()

    val recomendaciones by recomendacionesViewModel.recomendaciones.collectAsStateWithLifecycle()
    val isSearching by recomendacionesViewModel.isSearching.collectAsStateWithLifecycle()
    val cartItems by cartViewModel.items.collectAsStateWithLifecycle()
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
            Spacer(Modifier.height(16.dp))

            // Filtros con Chips
            Text("Filtros Adicionales", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            val edades = listOf("Cachorro/Gatito", "Adulto", "Senior", "Todas las edades")
            FilterChips(title = "Edad", options = edades, selected = edadSeleccionada, onSelected = recomendacionesViewModel::onEdadChange)

            val razas = listOf("Raza Pequeña", "Raza Mediana", "Raza Grande", "Todas las razas")
            FilterChips(title = "Raza/Tamaño", options = razas, selected = razaSeleccionada, onSelected = recomendacionesViewModel::onRazaChange)

            Spacer(Modifier.height(16.dp))
            
            Button(onClick = { 
                submitted = true
                recomendacionesViewModel.buscarRecomendaciones() 
            }, modifier = Modifier.fillMaxWidth(), enabled = !isSearching) {
                if(isSearching) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Obtener Recomendaciones")
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            if (submitted && !isSearching) {
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
            } else if (isSearching) {
                 Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FilterChips(title: String, options: List<String>, selected: String, onSelected: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(4.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                FilterChip(
                    selected = selected == option,
                    onClick = { onSelected(if (selected == option) "" else option) }, // Permite deseleccionar
                    label = { Text(option) }
                )
            }
        }
    }
}
