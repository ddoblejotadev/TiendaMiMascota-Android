package com.example.mimascota.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimascota.Model.CartItem
import com.example.mimascota.ViewModel.CartViewModel
import com.example.mimascota.ViewModel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.mimascota.util.formatCurrencyCLP

// Composable Wrapper: Obtiene los datos del ViewModel y los pasa a la pantalla
@Composable
fun CompraExitosaScreenWrapper(
    navController: NavController,
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel
) {
    // Observar los datos del último pedido desde el ViewModel
    val pedidoItems by cartViewModel.ultimoPedido.collectAsState()
    val totalPedido by cartViewModel.totalUltimoPedido.collectAsState()
    val numeroPedido by cartViewModel.numeroUltimoPedido.collectAsState()

    // Llamar a la pantalla principal pasando los datos
    CompraExitosaScreen(
        navController = navController,
        pedidoItems = pedidoItems,
        totalPedido = totalPedido,
        numeroPedido = numeroPedido,
        authViewModel = authViewModel
    )
}

// Pantalla de Compra Exitosa
@Composable
fun CompraExitosaScreen(
    navController: NavController,
    pedidoItems: List<CartItem>,
    totalPedido: Int,
    numeroPedido: String,
    authViewModel: AuthViewModel
) {
    // Obtener la fecha y hora actual
    @Suppress("SimpleDateFormat")
    val fechaActual = remember {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-CL")).format(Date())
    }

    // Pantalla principal
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            // Ícono de éxito (checkmark grande)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Compra exitosa",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(24.dp))

            // Título: Mensaje de éxito
            Text(
                text = "¡Compra Exitosa! 🎉",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            // Subtítulo
            Text(
                text = "Tu pedido ha sido procesado correctamente",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            // Tarjeta con los detalles del pedido
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Título de la tarjeta
                    Text(
                        text = "Detalles del Pedido",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(16.dp))

                    // Mostrar número de pedido
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "N° Pedido:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = numeroPedido,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Mostrar fecha del pedido
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Fecha:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = fechaActual,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Línea divisoria
                    HorizontalDivider()

                    Spacer(Modifier.height(16.dp))

                    // Título de la lista de productos
                    Text(
                        text = "Productos (${pedidoItems.size} items):",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(12.dp))

                    // Lista de productos comprados
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        items(pedidoItems) { item ->
                            // Cada producto en una fila
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Columna izquierda: Nombre y cantidad
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.producto.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Cantidad: ${item.cantidad}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                // Columna derecha: Subtotal
                                Text(
                                    text = "$${formatCurrencyCLP(item.subtotal)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Línea divisoria
                    HorizontalDivider()

                    Spacer(Modifier.height(16.dp))

                    // Mostrar el total pagado
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Pagado:",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$${formatCurrencyCLP(totalPedido)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Tarjeta con mensaje informativo
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = "📦 Tu pedido será procesado y enviado pronto. Recibirás una notificación cuando esté en camino.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(Modifier.weight(1f))

            // Botones de acción
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón principal: Continuar comprando
                Button(
                    onClick = {
                        // Navegar al catálogo
                        navController.navigate("Catalogo") {
                            popUpTo("Carrito") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Continuar Comprando")
                }

                // Botón secundario: Volver al inicio
                OutlinedButton(
                    onClick = {
                        // Navegar al home con el nombre real del usuario
                        val username = authViewModel.usuarioActual.value ?: "Usuario"
                        navController.navigate("home/$username") {
                            popUpTo("register") { inclusive = false }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Volver al Inicio")
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
