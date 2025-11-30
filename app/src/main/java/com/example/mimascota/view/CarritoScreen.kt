package com.example.mimascota.view

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimascota.model.CartItem
import com.example.mimascota.viewModel.CartViewModel
import coil.compose.AsyncImage
import com.example.mimascota.util.AppConfig
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import com.example.mimascota.ui.activity.CheckoutActivity
import com.google.gson.Gson
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(navController: NavController, viewModel: CartViewModel) {
    val items by viewModel.items.collectAsState()

    val total by viewModel.total.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(String.format(Locale.getDefault(), "Total: $%.2f", total), style = MaterialTheme.typography.titleLarge)
                    Button(onClick = {
                        // Evitar checkout con carrito vacío
                        if (items.isEmpty()) {
                            Toast.makeText(context, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        // Iniciar CheckoutActivity con extras del carrito (JSON) y total
                        try {
                            val gson = Gson()
                            val itemsJson = gson.toJson(items)
                            val intent = Intent(context, CheckoutActivity::class.java).apply {
                                putExtra("cart_items_json", itemsJson)
                                putExtra("cart_total", total)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Si falla la serialización, abrir igualmente CheckoutActivity sin extras
                            val intent = Intent(context, CheckoutActivity::class.java)
                            context.startActivity(intent)
                        }
                    }) {
                        Text("Comprar")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(items) { item ->
                CartItemView(item = item, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CartItemView(item: CartItem, viewModel: CartViewModel) {
    Row(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        // Imagen del producto
        val imageUrl = AppConfig.toAbsoluteImageUrl(item.producto.imageUrl)
        AsyncImage(
            model = imageUrl,
            contentDescription = item.producto.producto_nombre,
            modifier = Modifier.size(64.dp),
            placeholder = painterResource(id = com.example.mimascota.R.drawable.placeholder_product),
            error = painterResource(id = com.example.mimascota.R.drawable.placeholder_product),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.producto.producto_nombre, style = MaterialTheme.typography.titleMedium)
            // `price` en el modelo es no-nullable Double, por eso usamos directamente
            Text(String.format(Locale.getDefault(), "$%.2f", item.producto.price), style = MaterialTheme.typography.bodySmall)
            // Mostrar stock si disponible
            val stock = item.producto.stock
            if (stock == null) {
                Text("Stock: N/D", style = MaterialTheme.typography.bodySmall)
            } else if (stock <= 0) {
                Text("Agotado", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            } else {
                Text("Stock disponible: ${stock}", style = MaterialTheme.typography.bodySmall)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                val nueva = item.cantidad - 1
                if (nueva <= 0) {
                    viewModel.actualizarCantidad(item.producto, 0)
                } else {
                    viewModel.actualizarCantidad(item.producto, nueva)
                }
            }) {
                Icon(Icons.Filled.Remove, contentDescription = "Quitar")
            }
            Text(item.cantidad.toString())
            IconButton(onClick = {
                // Evitar aumentar más que el stock
                val stockVal = item.producto.stock ?: Int.MAX_VALUE
                if (item.cantidad < stockVal) {
                    viewModel.actualizarCantidad(item.producto, item.cantidad + 1)
                }
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir")
            }
        }
    }
}
