package com.example.mimascota.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // ktlint-disable no-wildcard-imports
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

val tipos = listOf("Perro", "Gato", "Roedor", "Conejo", "Ave", "Exótico")
val estados = listOf("adopcion", "encontrado", "perdido")
val generos = listOf("Macho", "Hembra")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdopcionScreen(navController: NavController, huachitosViewModel: HuachitosViewModel = viewModel()) {
    val animales by huachitosViewModel.animales.collectAsState()
    val isLoading by huachitosViewModel.isLoading.collectAsState()
    val error by huachitosViewModel.error.collectAsState()
    var hasSearched by remember { mutableStateOf(false) }

    var selectedComuna by remember { mutableStateOf(comunasMetropolitanas.first()) }
    var selectedTipo by remember { mutableStateOf<String?>(null) }
    var selectedEstado by remember { mutableStateOf<String?>(null) }
    var selectedGenero by remember { mutableStateOf<String?>(null) }

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
        Column(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
            // --- Sección de Filtros ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Filtros de Búsqueda", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    FilterDropdown(label = "Comuna", options = comunasMetropolitanas.map { it.nombre }, selectedOption = selectedComuna.nombre) { newSelection ->
                        selectedComuna = comunasMetropolitanas.find { it.nombre == newSelection } ?: comunasMetropolitanas.first()
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Tipo", style = MaterialTheme.typography.bodyLarge)
                    FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        tipos.forEach { tipo ->
                            SelectableChip(text = tipo, selected = selectedTipo == tipo) {
                                selectedTipo = if (selectedTipo == tipo) null else tipo
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)){
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Género", style = MaterialTheme.typography.bodyLarge)
                            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                generos.forEach { genero ->
                                    SelectableChip(text = genero, selected = selectedGenero == genero) {
                                        selectedGenero = if (selectedGenero == genero) null else genero
                                    }
                                }
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Estado", style = MaterialTheme.typography.bodyLarge)
                            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                estados.forEach { estado ->
                                    SelectableChip(text = estado, selected = selectedEstado == estado) {
                                        selectedEstado = if (selectedEstado == estado) null else estado
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    hasSearched = true
                    huachitosViewModel.cargarAnimales(selectedComuna.id, selectedTipo?.lowercase(), selectedEstado?.lowercase(), selectedGenero?.lowercase())
                          },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Buscar", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Lista de Resultados ---
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    val errorMessage = if (error?.contains("404") == true) {
                        "No se encontraron animales con estos criterios."
                    } else {
                        "Ocurrió un error al buscar. Por favor, intenta de nuevo."
                    }
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = errorMessage,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                animales.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (hasSearched) "No se encontraron animales con los filtros seleccionados." else "Usa los filtros para buscar un huachito.",
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
fun FilterDropdown(label: String, options: List<String>, selectedOption: String, onSelectionChanged: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {}, // No-op
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onSelectionChanged(option)
                    expanded = false
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableChip(text: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) },
        leadingIcon = if (selected) {
            { Icon(imageVector = Icons.Default.Done, contentDescription = "Selected", modifier = Modifier.size(FilterChipDefaults.IconSize)) }
        } else { null }
    )
}

@Composable
fun AdopcionAnimalCard(animal: Animal, onClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(animal.id) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = animal.imagen,
                contentDescription = animal.nombre ?: "Animal en adopción",
                modifier = Modifier.fillMaxWidth().height(200.dp),
                 contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(animal.nombre ?: "Sin nombre", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("${animal.tipo ?: ""} - ${animal.edad ?: ""}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Ubicación", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text("${animal.comuna ?: ""}, ${animal.region ?: ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
