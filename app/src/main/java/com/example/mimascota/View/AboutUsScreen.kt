package com.example.mimascota.View

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sobre Mi Mascota 🐾") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF667EEA),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // 🐾 Encabezado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "🐾 Sobre Mi Mascota",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "La mejor tienda para el bienestar de tu mascota",
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 🐶 Nuestra Historia
            SectionHeader("📖 Nuestra Historia")
            HistoriaItem("2020 - El Inicio", "Mi Mascota nació con una misión clara: proporcionar productos de alta calidad para el cuidado y felicidad de las mascotas en Chile.", Color(0xFF667EEA))
            HistoriaItem("2021 - Crecimiento", "Lo que comenzó como una pequeña tienda local se expandió rápidamente, ganando la confianza de miles de familias chilenas.", Color(0xFF34A853))
            HistoriaItem("Hoy - Líderes del Mercado", "Somos una de las tiendas online más confiables del país, seleccionando cuidadosamente cada producto para garantizar calidad y seguridad.", Color(0xFF00BCD4))
            // 💙 Compromiso
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF0FF)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("💙 Nuestro Compromiso", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Tu mascota es parte de tu familia. Cada decisión que tomamos está pensada en su bienestar, garantizando calidad, seguridad y los mejores precios.",
                        color = Color.DarkGray
                    )
                }
            }
            // 🎯 Misión y Visión
            SectionHeader("🎯 Misión y Visión")
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MissionCard(
                    emoji = "🎯",
                    title = "Nuestra Misión",
                    text = "Proporcionar productos de calidad excepcional para el cuidado de mascotas, ofreciendo una experiencia de compra fácil y segura."
                )
                MissionCard(
                    emoji = "🌟",
                    title = "Nuestra Visión",
                    text = "Ser la tienda líder en Chile para productos de mascotas, reconocida por su calidad, servicio y compromiso con el bienestar animal."
                )
            }
            // 💎 Valores
            SectionHeader("💎 Nuestros Valores")
            ValoresGrid()
            // 📊 Estadísticas
            SectionHeader("📊 Nuestros Números")
            EstadisticasGrid()
            // 🐾 Llamado a la Acción
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        )
                    )
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "¿Listo para cuidar mejor a tu mascota?",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { navController.navigate("Catalogo") }) {
                        Text("Ver Productos")
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 8.dp),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
}

@Composable
fun HistoriaItem(title: String, text: String, color: Color) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(title, color = color, fontWeight = FontWeight.Bold)
        Text(text, color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun MissionCard(emoji: String, title: String, text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 36.sp)
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(text, color = Color.Gray, textAlign = TextAlign.Center, fontSize = 13.sp)
        }
    }
}

@Composable
fun ValoresGrid() {
    val valores = listOf(
        "❤️ Amor por los Animales" to "Pensamos siempre en su bienestar",
        "✓ Calidad Garantizada" to "Solo marcas confiables",
        "🤝 Confianza" to "Relaciones basadas en transparencia",
        "🚀 Innovación" to "Mejoramos cada día",
        "🎓 Educación" to "Promovemos el cuidado responsable",
        "🌍 Responsabilidad" to "Compromiso con el medio ambiente"
    )

    Column(Modifier.padding(horizontal = 16.dp)) {
        valores.chunked(2).forEach { fila ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fila.forEach { (titulo, desc) ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(titulo.split(" ").first(), fontSize = 32.sp)
                            Text(
                                titulo,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(desc, color = Color.Gray, textAlign = TextAlign.Center, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EstadisticasGrid() {
    val stats = listOf(
        Triple("😊", "5,000+", "Clientes Felices"),
        Triple("📦", "15,000+", "Productos Vendidos"),
        Triple("🛍️", "500+", "Productos Diferentes"),
        Triple("⭐", "4.8", "Calificación Promedio")
    )

    Column(Modifier.padding(horizontal = 16.dp)) {
        stats.chunked(2).forEach { fila ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fila.forEach { (emoji, numero, texto) ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(emoji, fontSize = 36.sp)
                            Text(numero, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                            Text(texto, color = Color.Gray, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}