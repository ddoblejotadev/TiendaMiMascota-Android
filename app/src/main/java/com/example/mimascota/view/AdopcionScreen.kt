package com.example.mimascota.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // ktlint-disable no-wildcard-imports
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mimascota.model.Animal
import com.example.mimascota.viewModel.HuachitosViewModel

// --- Datos para los filtros ---
data class Comuna(val id: Int, val nombre: String)
val comunasMetropolitanas = listOf(
    Comuna(131, "Santiago"), Comuna(127, "Puente Alto"), Comuna(118, "Maipú"), Comuna(111, "La Florida"),
    Comuna(121, "Las Condes"), Comuna(141, "Providencia"), Comuna(151, "San Bernardo"), Comuna(161, "Peñalolén"),
    Comuna(106, "La Reina"), Comuna(126, "Ñuñoa"), Comuna(162, "Macul"), Comuna(105, "La Granja"),
    Comuna(109, "Lo Barnechea"), Comuna(110, "Vitacura"), Comuna(103, "Estación Central"), Comuna(104, "Independencia"),
    Comuna(116, "Pudahuel"), Comuna(117, "Quilicura"), Comuna(120, "Recoleta"), Comuna(122, "Renca"),
    Comuna(125, "San Miguel"), Comuna(130, "Lo Espejo"), Comuna(132, "San Joaquín"), Comuna(134, "La Cisterna"),
    Comuna(135, "El Bosque"), Comuna(136, "Pedro Aguirre Cerda"), Comuna(138, "Lo Prado"), Comuna(139, "Cerro Navia"),
    Comuna(140, "Conchalí"), Comuna(142, "Huechuraba"), Comuna(144, "Quinta Normal"), Comuna(145, "San Ramón")
).sortedBy { it.nombre }

val tipos = listOf("Perro", "Gato")
val estados = listOf("adopcion", "encontrado", "perdido")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdopcionScreen(navController: NavController, huachitosViewModel: HuachitosViewModel = viewModel()) {
    val animales by huachitosViewModel.animales.collectAsState()
    val isLoading by huachitosViewModel.isLoading.collectAsState()
    val error by huachitosViewModel.error.collectAsState()

    var selectedComuna by remember { mutableStateOf(comunasMetropolitanas.first()) }
    var selectedTipo by remember { mutableStateOf<String?>(null) }
    var selectedEstado by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        huachitosViewModel.cargarAnimales(selectedComuna.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adopta un Huachito") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            FilterDropdown(label = "Comuna", options = comunasMetropolitanas.map { it.nombre }, selectedOption = selectedComuna.nombre) { newSelection ->
                selectedComuna = comunasMetropolitanas.find { it.nombre == newSelection } ?: comunasMetropolitanas.first()
            }
            Spacer(modifier = Modifier.height(8.dp))
            FilterDropdown(label = "Tipo", options = tipos, selectedOption = selectedTipo, isOptional = true) { selectedTipo = it }
            Spacer(modifier = Modifier.height(8.dp))
            FilterDropdown(label = "Estado", options = estados, selectedOption = selectedEstado, isOptional = true) { selectedEstado = it }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { huachitosViewModel.cargarAnimales(selectedComuna.id, selectedTipo, selectedEstado) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buscar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                error != null -> {
                    Text("Error: $error", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                animales.isEmpty() -> {
                    Text("No se encontraron animales con estos criterios.", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(animales) { animal ->
                            AdopcionAnimalCard(animal) { animalId ->
                                navController.navigate("animalDetail/$animalId")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(label: String, options: List<String>, selectedOption: String?, isOptional: Boolean = false, onSelectionChanged: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val displayText = selectedOption ?: label

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (isOptional) {
                DropdownMenuItem(text = { Text("Cualquiera") }, onClick = {
                    onSelectionChanged(null)
                    expanded = false
                })
            }
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onSelectionChanged(option)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun AdopcionAnimalCard(animal: Animal, onClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(animal.id) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = animal.imagen,
                contentDescription = animal.nombre ?: "Animal en adopción",
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(animal.nombre ?: "Sin nombre", style = MaterialTheme.typography.titleLarge)
                Text("${animal.tipo ?: ""} - ${animal.edad ?: ""}", style = MaterialTheme.typography.bodyMedium)
                Text("${animal.comuna ?: ""}, ${animal.region ?: ""}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
