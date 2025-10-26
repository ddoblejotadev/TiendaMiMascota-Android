package com.example.mimascota.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimascota.ViewModel.CartViewModel

// Tipos de error que pueden ocurrir
enum class TipoError {
    STOCK_INSUFICIENTE,
    ERROR_PAGO,
    ERROR_CONEXION
}

// Composable Wrapper: Obtiene el tipo de error de la navegación
@Composable
fun CompraRechazadaScreenWrapper(
    navController: NavController,
    tipoError: String,
    cartViewModel: CartViewModel
) {
    // Convertir el string a enum
    val error = when (tipoError) {
        "STOCK" -> TipoError.STOCK_INSUFICIENTE
        "PAGO" -> TipoError.ERROR_PAGO
        "CONEXION" -> TipoError.ERROR_CONEXION
        else -> TipoError.ERROR_PAGO
    }

    // Llamar a la pantalla principal
    CompraRechazadaScreen(
        navController = navController,
        tipoError = error,
        cartViewModel = cartViewModel
    )
}

// Pantalla de Compra Rechazada
@Composable
fun CompraRechazadaScreen(
    navController: NavController,
    tipoError: TipoError,
    cartViewModel: CartViewModel
) {
    // Obtener carrito para mostrar productos con problema
    val carrito by cartViewModel.carrito.collectAsState()
    val productosConProblema = carrito.filter { it.cantidad > it.producto.stock }

    // Obtener el mensaje y título según el tipo de error
    val (titulo, mensaje) = when (tipoError) {
        TipoError.STOCK_INSUFICIENTE -> Pair(
            "Stock Insuficiente 📦",
            if (productosConProblema.isNotEmpty()) {
                "Los siguientes productos en tu carrito exceden el stock disponible:"
            } else {
                "Lo sentimos, algunos productos de tu carrito no tienen stock disponible en este momento."
            }
        )
        TipoError.ERROR_PAGO -> Pair(
            "Error en el Pago 💳",
            "No se pudo procesar tu pago. Por favor, verifica tus datos de pago e intenta nuevamente."
        )
        TipoError.ERROR_CONEXION -> Pair(
            "Error de Conexión 📶",
            "No se pudo conectar con el servidor. Por favor, verifica tu conexión a internet e intenta nuevamente."
        )
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
            Spacer(Modifier.height(60.dp))

            // Ícono de error grande
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(24.dp))

            // Título del error
            Text(
                text = titulo,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            // Mensaje explicativo
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(32.dp))

            // Mostrar productos con problema si es error de stock
            if (tipoError == TipoError.STOCK_INSUFICIENTE && productosConProblema.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Productos con problema:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )

                        Spacer(Modifier.height(12.dp))

                        productosConProblema.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "• ${item.producto.name}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        text = "Tienes: ${item.cantidad} | Disponibles: ${item.producto.stock}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }

            // Tarjeta con información adicional
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "¿Qué puedes hacer?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )

                    Spacer(Modifier.height(12.dp))

                    // Sugerencias según el tipo de error
                    when (tipoError) {
                        TipoError.STOCK_INSUFICIENTE -> {
                            Text(
                                text = "• Revisa tu carrito y elimina productos sin stock\n" +
                                        "• Reduce las cantidades de los productos\n" +
                                        "• Intenta nuevamente más tarde",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        TipoError.ERROR_PAGO -> {
                            Text(
                                text = "• Verifica los datos de tu tarjeta\n" +
                                        "• Asegúrate de tener fondos suficientes\n" +
                                        "• Intenta con otro método de pago",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        TipoError.ERROR_CONEXION -> {
                            Text(
                                text = "• Verifica tu conexión a internet\n" +
                                        "• Intenta conectarte a otra red Wi-Fi\n" +
                                        "• Espera un momento e intenta nuevamente",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Botones de acción
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón principal: Intentar nuevamente
                Button(
                    onClick = {
                        // Volver al carrito para intentar de nuevo
                        navController.navigate("Carrito") {
                            popUpTo("compraRechazada") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Intentar Nuevamente")
                }

                // Botón secundario: Revisar carrito
                OutlinedButton(
                    onClick = {
                        // Volver al carrito para revisar
                        navController.navigate("Carrito") {
                            popUpTo("compraRechazada") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Revisar Carrito")
                }

                // Botón terciario: Volver al inicio
                TextButton(
                    onClick = {
                        // Navegar al home
                        navController.navigate("home/Usuario") {
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

