package com.example.mimascota.view

import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mimascota.viewModel.HuachitosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalDetailScreen(animalId: Int, huachitosViewModel: HuachitosViewModel = viewModel()) {
    val animal by huachitosViewModel.selectedAnimal.collectAsState()

    LaunchedEffect(animalId) {
        huachitosViewModel.getAnimalById(animalId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(animal?.nombre ?: "Cargando...") })
        }
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        animal?.let { animalData ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                AsyncImage(
                    model = animalData.imagen,
                    contentDescription = animalData.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(animalData.nombre, style = MaterialTheme.typography.headlineLarge)
                Text("${animalData.tipo} - ${animalData.edad} - ${animalData.genero}", style = MaterialTheme.typography.titleMedium)
                Text("Encontrado en ${animalData.comuna}, ${animalData.region}", style = MaterialTheme.typography.bodyMedium)
                
                Spacer(modifier = Modifier.height(16.dp))
                HtmlText(html = animalData.descFisica, title = "Descripción Física")
                Spacer(modifier = Modifier.height(16.dp))
                HtmlText(html = animalData.descPersonalidad, title = "Personalidad")
                Spacer(modifier = Modifier.height(16.dp))
                HtmlText(html = animalData.descAdicional, title = "Información Adicional")
            }
        }
    }
}

@Composable
fun HtmlText(html: String, title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    AndroidView(factory = {
        WebView(it)
    }, update = {
        it.loadData(html, "text/html", "UTF-8")
    })
}
