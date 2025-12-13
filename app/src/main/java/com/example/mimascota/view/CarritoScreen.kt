package com.example.mimascota.view

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mimascota.model.CartItem
import com.example.mimascota.ui.activity.CheckoutActivity
import com.example.mimascota.util.AppConfig
import com.example.mimascota.util.CurrencyUtils
import com.example.mimascota.viewModel.CartViewModel
import com.google.gson.Gson
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(navController: NavController, viewModel: CartViewModel) {
    val items by viewModel.items.collectAsState()
    val total by viewModel.total.collectAsState()
    val context = LocalContext.current

    val iva = CurrencyUtils.getIVAFromTotalPrice(total)
    val subtotal = total - iva

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = com.example.mimascota.R.string.ver_carrito)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = com.example.mimascota.R.string.volver_label))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            if (items.isEmpty()){
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    Text(text = "El carrito esta vacio")
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(items, key = { it.producto.producto_id }) { item ->
                        CartItemView(item = item, viewModel = viewModel)
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Subtotal: ${CurrencyUtils.formatAsCLP(subtotal)}", style = MaterialTheme.typography.bodyMedium)
                        Text("IVA (19%): ${CurrencyUtils.formatAsCLP(iva)}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Total: ${CurrencyUtils.formatAsCLP(total)}", style = MaterialTheme.typography.titleLarge)
                    }
                    Button(onClick = {
                        if (items.isEmpty()) {
                            Toast.makeText(context, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        try {
                            val gson = Gson()
                            val itemsJson = gson.toJson(items)
                            val intent = Intent(context, CheckoutActivity::class.java).apply {
                                putExtra("cart_items_json", itemsJson)
                                putExtra("cart_total", total)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val intent = Intent(context, CheckoutActivity::class.java)
                            context.startActivity(intent)
                        }
                    },
                        enabled = items.isNotEmpty()
                    ) {
                        Text("Pagar")
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemView(item: CartItem, viewModel: CartViewModel) {
    var textQuantity by remember(item.cantidad) { mutableStateOf(item.cantidad.toString()) }
    val context = LocalContext.current

    LaunchedEffect(textQuantity) {
        delay(600)

        val newQuantity = textQuantity.toIntOrNull()

        if (newQuantity != null && newQuantity != item.cantidad) {
            val stock = item.producto.stock ?: Int.MAX_VALUE
            val validatedQuantity = newQuantity.coerceIn(0, stock)
            viewModel.actualizarCantidad(item.producto, validatedQuantity)
        }
    }

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
            Text(CurrencyUtils.formatAsCLP(item.producto.price), style = MaterialTheme.typography.bodySmall)
            val stock = item.producto.stock
            if (stock == null) {
                Text(stringResource(id = com.example.mimascota.R.string.no_products), style = MaterialTheme.typography.bodySmall)
            } else if (stock <= 0) {
                Text(stringResource(id = com.example.mimascota.R.string.stock_insuficiente), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            } else {
                Text(String.format(stringResource(id = com.example.mimascota.R.string.stock_disponible), stock), style = MaterialTheme.typography.bodySmall)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
            IconButton(onClick = {
                viewModel.actualizarCantidad(item.producto, (item.cantidad - 1).coerceAtLeast(0))
            }) {
                Icon(Icons.Filled.Remove, contentDescription = "Quitar")
            }

            OutlinedTextField(
                value = textQuantity,
                onValueChange = { newText ->
                    val filteredText = newText.filter { it.isDigit() }.take(7)
                    val newQuantityValue = filteredText.toIntOrNull()
                    val stock = item.producto.stock ?: Int.MAX_VALUE

                    if (newQuantityValue != null && newQuantityValue > stock) {
                        textQuantity = stock.toString()
                        Toast.makeText(context, "Stock máximo: $stock", Toast.LENGTH_SHORT).show()
                    } else {
                        textQuantity = filteredText
                    }
                },
                modifier = Modifier.width(70.dp),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            IconButton(onClick = {
                val stock = item.producto.stock ?: Int.MAX_VALUE
                if (item.cantidad < stock) {
                    viewModel.actualizarCantidad(item.producto, item.cantidad + 1)
                } else {
                    Toast.makeText(context, "No hay más stock disponible", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir")
            }
        }
    }
}