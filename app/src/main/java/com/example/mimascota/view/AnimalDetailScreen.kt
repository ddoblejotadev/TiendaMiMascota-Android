package com.example.mimascota.view

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mimascota.viewModel.HuachitosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalDetailScreen(navController: NavController, animalId: Int, huachitosViewModel: HuachitosViewModel) {
    val animal by huachitosViewModel.selectedAnimal.collectAsState()
    val isLoading by huachitosViewModel.isLoading.collectAsState()


    LaunchedEffect(animalId) {
        huachitosViewModel.getAnimalById(animalId)
    }

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
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val scrollState = rememberScrollState()
            animal?.let { animalData ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(scrollState)
                ) {
                    val context = LocalContext.current
                    AsyncImage(
                        model = animalData.imagen,
                        contentDescription = animalData.nombre ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(animalData.nombre ?: "Sin nombre", style = MaterialTheme.typography.headlineLarge)
                    Text(
                        "${animalData.tipo ?: ""} - ${animalData.edad ?: ""} - ${animalData.genero ?: ""}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Encontrado en ${animalData.comuna ?: ""}, ${animalData.region ?: ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HtmlText(html = animalData.descFisica, title = "Descripción Física")
                    Spacer(modifier = Modifier.height(16.dp))
                    HtmlText(html = animalData.descPersonalidad, title = "Personalidad")
                    Spacer(modifier = Modifier.height(16.dp))
                    HtmlText(html = animalData.descAdicional, title = "Información Adicional")

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón para abrir la URL de origen
                    animalData.url?.let { url ->
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ver perfil completo en Huachitos.cl")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HtmlText(html: String?, title: String) {
    if (!html.isNullOrBlank()) {
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium)
            val textColor = if (isSystemInDarkTheme()) "white" else "black"
            val htmlContent = """
                <html><head>
                <style>
                    body {
                        font-family: sans-serif;
                        color: $textColor;
                        padding: 8px;
                    }
                </style>
                </head><body>
                ${html ?: ""}
                </body></html>
            """.trimIndent()
            AndroidView(factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    settings.javaScriptEnabled = true
                    setBackgroundColor(0x00000000) // Transparent background
                }
            }, update = {
                it.loadData(htmlContent, "text/html", "UTF-8")
            })
        }
    }
}
