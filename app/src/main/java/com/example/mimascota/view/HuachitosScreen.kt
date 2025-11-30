package com.example.mimascota.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mimascota.model.Animal
import com.example.mimascota.viewModel.HuachitosViewModel

@Composable
fun HuachitosScreen(huachitosViewModel: HuachitosViewModel = viewModel()) {
    val animales by huachitosViewModel.animales.collectAsState()
    val isLoading by huachitosViewModel.isLoading.collectAsState()
    val error by huachitosViewModel.error.collectAsState()

    // Carga los animales de una comuna específica (ej: 131 para "Santiago")
    LaunchedEffect(Unit) {
        huachitosViewModel.cargarAnimales(131)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (error != null) {
            Text("Error: $error", modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(contentPadding = PaddingValues(8.dp)) {
                items(animales) { animal ->
                    AnimalCard(animal)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalCard(animal: Animal) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = animal.imagen,
                contentDescription = animal.nombre,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(animal.nombre, style = MaterialTheme.typography.titleLarge)
                Text("${animal.tipo} - ${animal.edad}")
                Text("${animal.comuna}, ${animal.region}")
            }
        }
    }
}
