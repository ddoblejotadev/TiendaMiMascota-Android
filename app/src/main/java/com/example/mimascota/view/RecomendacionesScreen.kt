package com.example.mimascota.view

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mimascota.model.Producto
import com.example.mimascota.viewModel.CartViewModel
import com.example.mimascota.viewModel.CatalogoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecomendacionesScreen(navController: NavController, catalogoViewModel: CatalogoViewModel = viewModel(), cartViewModel: CartViewModel = viewModel()) {
    var petType by rememberSaveable { mutableStateOf("") }
    var breed by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }
    val productos by catalogoViewModel.productos.collectAsState()
    var filteredProducts by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var submitted by remember { mutableStateOf(false) }
    val cartItems by cartViewModel.items.collectAsState()
    val cantidadesPorProducto = remember(cartItems) { cartItems.associate { it.producto.producto_id to it.cantidad } }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recomendaciones") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(text = "Recomendaciones para tu mascota")
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = petType,
                onValueChange = { petType = it },
                label = { Text("Tipo de mascota (ej. perro, gato)") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text("Raza") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Edad") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Peso (kg)") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                submitted = true
                val ageAsInt = age.toIntOrNull()
                val ageTerm = if (ageAsInt != null) {
                    when {
                        ageAsInt < 1 -> "cachorro"
                        ageAsInt <= 2 -> "joven"
                        ageAsInt <= 7 -> "adulto"
                        else -> "viejo"
                    }
                } else {
                    age
                }

                if (petType.isBlank() && breed.isBlank() && age.isBlank() && weight.isBlank()) {
                    filteredProducts = emptyList()
                } else {
                    filteredProducts = productos.filter { producto ->
                        val productoText = (
                            producto.producto_nombre + " " +
                            producto.category + " " +
                            (producto.description ?: "")
                        ).lowercase()

                        val typeMatch = petType.isBlank() || productoText.contains(petType.lowercase())

                        val breedMatch = breed.isBlank() ||
                                         productoText.contains(breed.lowercase()) ||
                                         productoText.contains("todas las razas")

                        val ageMatch = ageTerm.isBlank() ||
                                       productoText.contains(ageTerm.lowercase()) ||
                                       productoText.contains("todas las edades")

                        val weightMatch = weight.isBlank() ||
                                          productoText.contains(weight.lowercase()) ||
                                          productoText.contains("cualquier peso")

                        typeMatch && breedMatch && ageMatch && weightMatch
                    }
                }
            }) {
                Text("Buscar Recomendaciones")
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (submitted) {
                if (filteredProducts.isEmpty()) {
                    Text("producto no encontrado")
                } else {
                    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                        items(filteredProducts) { producto ->
                            val qtyInCart = cantidadesPorProducto[producto.producto_id] ?: 0
                            ProductoCard(
                                producto = producto,
                                onProductoClick = {
                                    navController.navigate("Detalle/${producto.producto_id}")
                                },
                                onAddToCart = {
                                    Log.d("RecomendacionesScreen", "Agregar al carrito pedido para id=${producto.producto_id}")
                                    cartViewModel.agregarAlCarrito(producto)
                                },
                                cartQuantity = qtyInCart
                            )
                        }
                    }
                }
            }
        }
    }
}
