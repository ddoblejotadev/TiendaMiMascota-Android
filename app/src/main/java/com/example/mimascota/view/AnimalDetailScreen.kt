package com.example.mimascota.view

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mimascota.viewModel.HuachitosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalDetailScreen(navController: NavController, animalId: Int, viewModel: HuachitosViewModel) {
    LaunchedEffect(animalId) {
        viewModel.getAnimalById(animalId)
    }

    val animal by viewModel.selectedAnimal.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(animal?.nombre ?: "Cargando...") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Text(text = "Error: $error", modifier = Modifier.align(Alignment.Center))
                }
                animal == null -> {
                    Text(text = "No se pudo cargar la información del animal.", modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    val animalData = animal!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        AsyncImage(
                            model = animalData.imagenes?.firstOrNull()?.imagen ?: animalData.imagen,
                            contentDescription = animalData.nombre,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.padding(16.dp)) {
                            animalData.imagenes?.let { 
                                if (it.size > 1) {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(vertical = 8.dp)
                                    ) {
                                        items(it) { imagen ->
                                            AsyncImage(
                                                model = imagen.imagen,
                                                contentDescription = "Galería de ${animalData.nombre}",
                                                modifier = Modifier
                                                    .size(100.dp)
                                                    .clip(RoundedCornerShape(8.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = animalData.nombre ?: "Sin nombre",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Button(
                                    onClick = {
                                        animalData.url?.let {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                                            context.startActivity(intent)
                                        }
                                    },
                                    enabled = !animalData.url.isNullOrBlank()
                                ) {
                                    Text("¡Adoptar!")
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("${animalData.tipo ?: "N/A"} • ${animalData.genero ?: "N/A"} • ${animalData.edad ?: "N/A"}", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${animalData.comuna ?: ""}, ${animalData.region ?: ""}", style = MaterialTheme.typography.bodyMedium)
                            
                            Divider(modifier = Modifier.padding(vertical = 16.dp))
                            
                            animalData.descFisica?.let { DetailSection("Descripción Física", it) }
                            animalData.descPersonalidad?.let { DetailSection("Personalidad", it) }
                            animalData.descAdicional?.let { DetailSection("Información Adicional", it) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailSection(title: String, content: String) {
    if (content.isNotBlank()) {
        Column(modifier = Modifier.padding(bottom = 12.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(text = content.replace("<p>", "").replace("</p>", "\n").trim(), style = MaterialTheme.typography.bodyLarge)
        }
    }
}
