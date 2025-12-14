package com.example.mimascota.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimascota.R
import com.example.mimascota.model.CartItem
import com.example.mimascota.util.CurrencyUtils
import com.example.mimascota.viewModel.AuthViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.URLDecoder

@Composable
fun CompraExitosaScreenWrapper(
    navController: NavController,
    authViewModel: AuthViewModel,
    numeroOrden: String?,
    itemsJson: String?
) {
    val items = remember(itemsJson) {
        try {
            val decodedJson = URLDecoder.decode(itemsJson, "UTF-8")
            val type = object : TypeToken<List<CartItem>>() {}.type
            Gson().fromJson<List<CartItem>>(decodedJson, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList<CartItem>()
        }
    }

    CompraExitosaScreen(navController, items, authViewModel, numeroOrden)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompraExitosaScreen(
    navController: NavController,
    items: List<CartItem>,
    authViewModel: AuthViewModel,
    numeroOrden: String?
) {
    val total = items.sumOf { (it.producto.price ?: 0.0) * it.cantidad }
    val iva = CurrencyUtils.getIVAFromTotalPrice(total)
    val subtotal = total - iva

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.compra_exitosa_title)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Scrollable content area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Compra Exitosa",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(96.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.thanks_for_purchase),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                if (numeroOrden != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tu número de orden es: #$numeroOrden",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))

                // Order Summary Card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(id = R.string.order_summary),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${item.cantidad}x ${item.producto.producto_nombre}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                                )
                                Text(
                                    text = CurrencyUtils.formatAsCLP((item.producto.price ?: 0.0) * item.cantidad),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.End
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 16.dp))

                        // Totals
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", style = MaterialTheme.typography.bodyLarge)
                            Text(CurrencyUtils.formatAsCLP(subtotal), style = MaterialTheme.typography.bodyLarge)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("IVA (19%)", style = MaterialTheme.typography.bodyLarge)
                            Text(CurrencyUtils.formatAsCLP(iva), style = MaterialTheme.typography.bodyLarge)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "Total",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                CurrencyUtils.formatAsCLP(total),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Shipping Info Card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(32.dp).padding(end = 16.dp))
                        Text(
                            "Tu pedido llegará en 2-3 días hábiles.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp)) // Space at the end of scroll
            }
            
            // Action Buttons
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Button(
                    onClick = { navController.navigate("Catalogo") { popUpTo("Catalogo") { inclusive = true } } },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Seguir Comprando", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        navController.navigate("MisPedidos") {
                            popUpTo(navController.graph.startDestinationId)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Ver mis pedidos", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
