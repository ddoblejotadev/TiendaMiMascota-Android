package com.example.mimascota.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mimascota.R
import com.example.mimascota.model.Producto
import com.example.mimascota.util.AppConfig
import com.example.mimascota.viewModel.CartViewModel
import com.example.mimascota.viewModel.CatalogoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    navController: NavController,
    productoId: Int,
    viewModel: CatalogoViewModel,
    cartViewModel: CartViewModel
) {
    val producto by viewModel.getProductoById(productoId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(producto?.producto_nombre ?: stringResource(id = R.string.descripcion)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.volver_label))
                    }
                }
            )
        }
    ) { padding ->
        producto?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Imagen del producto (Coil)
                val imageUrl = AppConfig.toAbsoluteImageUrl(it.imageUrl)
                AsyncImage(
                    model = imageUrl,
                    contentDescription = it.producto_nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = com.example.mimascota.R.drawable.placeholder_product),
                    error = painterResource(id = com.example.mimascota.R.drawable.placeholder_product)
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(it.producto_nombre, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(6.dp))
                Text(String.format("$%.2f", it.price), style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(it.description ?: "", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { cartViewModel.agregarAlCarrito(it) }) {
                    Text(stringResource(id = com.example.mimascota.R.string.agregar_al_carrito))
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
