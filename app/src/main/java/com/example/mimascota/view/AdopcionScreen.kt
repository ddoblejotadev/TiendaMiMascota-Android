package com.example.mimascota.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
val comunas = listOf(
    // Región Metropolitana
    Comuna(131, "Santiago"), Comuna(127, "Puente Alto"), Comuna(118, "Maipú"), Comuna(111, "La Florida"),
    Comuna(121, "Las Condes"), Comuna(141, "Providencia"), Comuna(151, "San Bernardo"), Comuna(161, "Peñalolén"),
    // Valparaíso
    Comuna(51, "Valparaíso"), Comuna(52, "Viña del Mar"), Comuna(53, "Quilpué"),
    // Biobío
    Comuna(81, "Concepción"), Comuna(82, "Talcahuano"), Comuna(83, "San Pedro de la Paz"),
    // Antofagasta
    Comuna(21, "Antofagasta"), Comuna(22, "Calama"),
    // La Araucanía
    Comuna(91, "Temuco"),
    // O'Higgins
    Comuna(101, "Rancagua"),
    // Maule
    Comuna(71, "Talca"),
    // Arica y Parinacota
    Comuna(1, "Arica"),
    // Tarapacá
    Comuna(11, "Iquique"),
    // Atacama
    Comuna(31, "Copiapó"),
    // Coquimbo
    Comuna(41, "La Serena"),
    // Los Ríos
    Comuna(10, "Valdivia")
).sortedBy { it.nombre }

val tipos = listOf("Perro", "Gato")
val estados = listOf("adopcion", "encontrado", "perdido")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdopcionScreen(navController: NavController, huachitosViewModel: HuachitosViewModel = viewModel()) {
    val animales by huachitosViewModel.animales.collectAsState()
    val isLoading by huachitosViewModel.isLoading.collectAsState()
    val error by huachitosViewModel.error.collectAsState()

    var selectedComuna by remember { mutableStateOf(comunas.first()) }
    var selectedTipo by remember { mutableStateOf<String?>(null) }
    var selectedEstado by remember { mutableStateOf<String?>(null) }

    // Cargar animales al iniciar con la comuna por defecto
    LaunchedEffect(Unit) {
        huachitosViewModel.cargarAnimales(selectedComuna.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Adopta un Huachito") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            FilterDropdown(label = "Comuna", options = comunas.map { it.nombre }, selectedOption = selectedComuna.nombre) { newSelection ->
                selectedComuna = comunas.find { it.nombre == newSelection } ?: comunas.first()
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
            .clickable { onClick(animal.id) }, // Navegar al hacer clic
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = animal.imagen,
                contentDescription = animal.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(animal.nombre, style = MaterialTheme.typography.titleLarge)
                Text("${animal.tipo} - ${animal.edad}", style = MaterialTheme.typography.bodyMedium)
                Text("${animal.comuna}, ${animal.region}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
