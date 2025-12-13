package com.example.mimascota.view

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimascota.model.Producto
import com.example.mimascota.util.CurrencyUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    navController: NavController,
    producto: Producto,
    onAddToCart: (Producto, Int) -> Unit,
    from: String? = null
) {
    var cantidadInput by remember { mutableStateOf("1") }
    val stockDisponible = producto.stock ?: 0
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(producto.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            if (from == "Recomendaciones") {
                BottomAppBar {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(onClick = { navController.navigate("Carrito") }) {
                            Text("Ir al carrito")
                        }
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Seguir comprando")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ProductImage(
                imageUrl = producto.imageUrl,
                contentDescription = null,
                modifier = Modifier.height(200.dp).fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(16.dp))
            Text(text = CurrencyUtils.formatAsCLP(producto.price), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text(text = "Stock: $stockDisponible", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(16.dp))
            Text(text = "Explorar productos similares", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                producto.category.takeIf { it.isNotBlank() }?.let {
                    ChipInfo(icon = Icons.Default.Category, text = it, onClick = { navController.navigate("Catalogo?categoria=$it") })
                }
                producto.tipoMascota?.takeIf { it.isNotBlank() }?.let {
                    ChipInfo(icon = Icons.Default.Pets, text = it, onClick = { /* No action for now */ })
                }
                producto.raza?.takeIf { it.isNotBlank() }?.let {
                    ChipInfo(icon = Icons.Default.Pets, text = it, onClick = { /* No action for now */ })
                }
                producto.edad?.takeIf { it.isNotBlank() }?.let {
                    ChipInfo(icon = Icons.Default.Cake, text = it, onClick = { /* No action for now */ })
                }
                producto.pesoMascota?.let {
                    ChipInfo(icon = Icons.Default.MonitorWeight, text = "${it}kg", onClick = { /* No action for now */ })
                }
            }
            Spacer(Modifier.height(16.dp))
            producto.description?.let {
                if(it.isNotBlank()){
                    Text(text = "Descripción", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(text = it, style = MaterialTheme.typography.bodyLarge)
                }
            }
            Spacer(Modifier.height(16.dp))
            if (stockDisponible > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val cantidad = cantidadInput.toIntOrNull() ?: 0
                    Button(onClick = { if (cantidad > 1) cantidadInput = (cantidad - 1).toString() }, enabled = cantidad > 1) { Text("-") }
                    OutlinedTextField(
                        value = cantidadInput,
                        onValueChange = { newValue ->
                            val filtered = newValue.filter { it.isDigit() }
                            cantidadInput = when {
                                filtered.isEmpty() -> ""
                                else -> {
                                    val num = filtered.toLongOrNull()
                                    when {
                                        num != null && num > stockDisponible -> stockDisponible.toString()
                                        num == null -> stockDisponible.toString()
                                        else -> filtered
                                    }
                                }
                            }
                        },
                        modifier = Modifier.width(100.dp).padding(horizontal = 16.dp),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Button(onClick = { if (cantidad < stockDisponible) cantidadInput = (cantidad + 1).toString() }, enabled = cantidad < stockDisponible) { Text("+") }
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    val finalCantidad = cantidadInput.toIntOrNull() ?: 0
                    if (finalCantidad > 0) {
                        onAddToCart(producto, finalCantidad)
                        scope.launch {
                            snackbarHostState.showSnackbar("¡Producto(s) añadido(s) al carrito!")
                        }
                    }
                    if (from != "Recomendaciones") {
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = stockDisponible > 0 && (cantidadInput.toIntOrNull() ?: 0) > 0
            ) {
                Text(if (stockDisponible > 0) "Agregar al carrito" else "Sin Stock")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChipInfo(icon: ImageVector, text: String, onClick: () -> Unit) {
    SuggestionChip(
        modifier = Modifier.padding(end = 8.dp),
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null, modifier = Modifier.size(SuggestionChipDefaults.IconSize)) },
        label = { Text(text) }
    )
}