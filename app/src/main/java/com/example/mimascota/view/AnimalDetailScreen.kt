package com.example.mimascota.view

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    val context = LocalContext.current

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
        },
        floatingActionButton = {
            animal?.url?.let { url ->
                ExtendedFloatingActionButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    icon = { Icon(Icons.Default.Pets, contentDescription = "Adoptar") },
                    text = { Text("¡Adóptame!") }
                )
            }
        }
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            animal != null -> {
                val animalData = animal!!
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    AsyncImage(
                        model = animalData.imagen,
                        contentDescription = animalData.nombre ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                        contentScale = ContentScale.Fit
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            animalData.nombre ?: "Sin nombre",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            animalData.tipo ?: "",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Card(elevation = CardDefaults.cardElevation(2.dp)) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                InfoRow(icon = Icons.Default.CalendarMonth, text = animalData.edad ?: "")
                                InfoRow(icon = Icons.Default.Wc, text = animalData.genero ?: "")
                                InfoRow(icon = Icons.Default.LocationOn, text = "${animalData.comuna}, ${animalData.region}")
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        HtmlText(html = animalData.descFisica, title = "Descripción Física")
                        HtmlText(html = animalData.descPersonalidad, title = "Personalidad")
                        HtmlText(html = animalData.descAdicional, title = "Información Adicional")

                        // Spacer to push content above FAB
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }

            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se pudo cargar la información del animal.")
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun HtmlText(html: String?, title: String) {
    if (!html.isNullOrBlank()) {
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            val textColor = String.format("#%06X", (0xFFFFFF and MaterialTheme.colorScheme.onSurface.toArgb()));
            val htmlContent = """
                <html><head>
                <style>
                    body {
                        font-family: sans-serif;
                        color: $textColor;
                    }
                     p { margin: 0; padding: 0; }
                </style>
                </head><body>
                ${html.replace("<p>", "").replace("</p>", "<br>")}
                </body></html>
            """.trimIndent()
            AndroidView(factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    settings.javaScriptEnabled = true
                    setBackgroundColor(0x00000000) // Fondo transparente
                }
            }, update = {
                it.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            })
        }
    }
}
