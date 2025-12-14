package com.example.mimascota.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mimascota.model.Animal

@Composable
fun HuachitosScreen(
    navController: NavController,
    animales: List<Animal> // Lista de animales para mostrar
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(animales) { animal ->
            HuachitoCard(navController, animal)
        }
    }
}

@Composable
fun HuachitoCard(
    navController: NavController,
    animal: Animal
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("animalDetail/${animal.id}") },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            AsyncImage(
                model = animal.imagen,
                contentDescription = animal.nombre ?: "Animal en adopción",
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(animal.nombre ?: "Sin nombre", style = MaterialTheme.typography.titleMedium)
                Text(animal.tipo ?: "", style = MaterialTheme.typography.bodySmall)
                Text("${animal.edad ?: ""} - ${animal.genero ?: ""}", style = MaterialTheme.typography.bodySmall)
                Text(animal.comuna ?: "", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
