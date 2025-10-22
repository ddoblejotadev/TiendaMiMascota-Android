package com.example.mimascota.View

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mimascota.Model.Producto
import com.example.mimascota.ViewModel.CatalogoViewModel
import androidx.compose.foundation.Image
import androidx.compose.material3.*

@Composable
fun CatalogoScreen(navController: NavController, viewModel: CatalogoViewModel) {
    val context = LocalContext.current
    val productos by viewModel.productos.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(Unit){
        viewModel.cargarProductos(context)
    }

    Scaffold(topBar = {
        SmallTopAppBar(title = { Text("Catálogo de Productos")})
    }) { padding ->
        Box( modifier = Modifier.padding(padding)) {
            if(loading) {
                Box(
                    Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Box
            }
            LazyColumn(modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp))
            { items(items = productos, key = {it.id}){
                producto ->
                ProductoCard(producto = producto, onClick = {
                    navController.navigate("detalle/${producto.id}")
                } )
            }
            }
        }
    }
}

@Composable
fun SmallTopAppBar(title: @Composable () -> Unit) {
    TODO("Not yet implemented")
}

@Composable
fun ProductoCard(producto: Producto, onClick: () -> Unit){
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable{ onClick() }
        .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ){
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            val painter = rememberAsyncImagePainter(producto.imageUrl)
            Image(
                painter = painter,
                contentDescription = producto.name,
                modifier = Modifier
                    .size(80.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(text = producto.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Precio: $${producto.price}", style = MaterialTheme.typography.bodyMedium)
                producto.description?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = it, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                }
            }
        }
    }
}

