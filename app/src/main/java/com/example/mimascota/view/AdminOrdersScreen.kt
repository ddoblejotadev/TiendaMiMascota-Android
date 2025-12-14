
package com.example.mimascota.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mimascota.model.DatosEnvioResponse
import com.example.mimascota.model.OrdenHistorial
import com.example.mimascota.model.ProductoOrden
import com.example.mimascota.model.UserWithOrders
import com.example.mimascota.util.CurrencyUtils
import com.example.mimascota.viewModel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

// Estados posibles para una orden
private val ORDER_STATUSES = listOf("PENDIENTE", "EN PROCESO", "ENVIADO", "ENTREGADO", "CANCELADO")

// Main entry point
@Composable
fun AdminOrdersScreen(adminViewModel: AdminViewModel = viewModel()) {
    val usersWithOrders by adminViewModel.usersWithOrders.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()
    val error by adminViewModel.error.collectAsState()

    // Filter out users who have no orders to display
    val usersWhoHaveOrdered = usersWithOrders.filter { it.orders.isNotEmpty() }

    when {
        isLoading && usersWhoHaveOrdered.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $error")
            }
        }
        usersWhoHaveOrdered.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay pedidos de ningún usuario.")
            }
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(usersWhoHaveOrdered, key = { it.user.usuarioId }) { userWithOrders ->
                    UserOrdersAccordion(userWithOrders = userWithOrders, adminViewModel = adminViewModel)
                }
            }
        }
    }
}

// Composable for the User accordion
@Composable
fun UserOrdersAccordion(userWithOrders: UserWithOrders, adminViewModel: AdminViewModel) {
    var isUserSectionExpanded by remember { mutableStateOf(false) }
    val userRotationAngle by animateFloatAsState(targetValue = if (isUserSectionExpanded) 180f else 0f)

    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isUserSectionExpanded = !isUserSectionExpanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(userWithOrders.user.nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Text(userWithOrders.user.email, style = MaterialTheme.typography.bodyMedium)
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Desplegar Usuario",
                    modifier = Modifier.rotate(userRotationAngle)
                )
            }

            AnimatedVisibility(visible = isUserSectionExpanded) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    userWithOrders.orders.forEach { order ->
                        AdminOrderCard(
                            orden = order,
                            onStatusChange = { orderId, newStatus ->
                                adminViewModel.updateOrderStatus(orderId, newStatus)
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

// New, refactored AdminOrderCard that mimics "Mis Pedidos"
@Composable
fun AdminOrderCard(orden: OrdenHistorial, onStatusChange: (Long, String) -> Unit) {
    var isOrderExpanded by remember { mutableStateOf(false) }
    val orderRotationAngle by animateFloatAsState(targetValue = if (isOrderExpanded) 180f else 0f)

    val formattedDate = remember(orden.fecha) { formatDate(orden.fecha) }

    Column {
        // Clickable header for each order
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isOrderExpanded = !isOrderExpanded }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text("Orden #${orden.numeroOrden ?: orden.id}", style = MaterialTheme.typography.titleMedium)
                Text(formattedDate, style = MaterialTheme.typography.bodySmall)
                Text(orden.estado ?: "Desconocido", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
             Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Desplegar Pedido",
                modifier = Modifier.rotate(orderRotationAngle)
            )
        }

        // Expanded view with all details
        AnimatedVisibility(visible = isOrderExpanded) {
            Column(Modifier.padding(top = 8.dp, bottom = 8.dp)) {
                // Status Editor
                OrderStatusEditor(
                    currentStatus = orden.estado ?: "SIN ESTADO",
                    onStatusChange = { newStatus -> onStatusChange(orden.id, newStatus) }
                )
                Spacer(Modifier.height(16.dp))

                // Products List
                orden.productos?.forEach { product ->
                    ProductOrderItem(item = product)
                    Spacer(Modifier.height(8.dp))
                }
                Spacer(Modifier.height(8.dp))

                // Price Breakdown
                val totalConIva = orden.total ?: 0.0
                val subtotal = CurrencyUtils.getBasePrice(totalConIva)
                val iva = totalConIva - subtotal

                PriceDetailRow("Subtotal", CurrencyUtils.formatAsCLP(subtotal))
                PriceDetailRow("IVA (19%)", CurrencyUtils.formatAsCLP(iva))
                PriceDetailRow("Envío", "A convenir") // Placeholder
                Divider(Modifier.padding(vertical = 8.dp))
                PriceDetailRow("Total", CurrencyUtils.formatAsCLP(totalConIva), isTotal = true)

                // Shipping Details
                orden.datosEnvio?.let {
                    Spacer(Modifier.height(16.dp))
                    Text("Datos de Envío", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    ShippingDetails(datos = it)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderStatusEditor(currentStatus: String, onStatusChange: (String) -> Unit) {
    var isStatusMenuExpanded by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf("") }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirmar Cambio de Estado") },
            text = { Text("¿Estás seguro de que quieres cambiar el estado a '$selectedStatus'?") },
            confirmButton = {
                Button(onClick = {
                    onStatusChange(selectedStatus)
                    showConfirmationDialog = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showConfirmationDialog = false }) { Text("Cancelar") }
            }
        )
    }

    ExposedDropdownMenuBox(
        expanded = isStatusMenuExpanded,
        onExpandedChange = { isStatusMenuExpanded = !isStatusMenuExpanded },
    ) {
        OutlinedTextField(
            value = currentStatus,
            onValueChange = {},
            readOnly = true,
            label = { Text("Actualizar Estado del Pedido") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusMenuExpanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(expanded = isStatusMenuExpanded, onDismissRequest = { isStatusMenuExpanded = false }) {
            ORDER_STATUSES.forEach { status ->
                if (status != currentStatus) {
                    DropdownMenuItem(
                        text = { Text(status) },
                        onClick = {
                            selectedStatus = status
                            showConfirmationDialog = true
                            isStatusMenuExpanded = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun ProductOrderItem(item: ProductoOrden) {
    val subtotal = (item.cantidad ?: 0) * (item.precioUnitario ?: 0.0)
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProductImage(
            imageUrl = item.imagen,
            contentDescription = item.nombre,
            modifier = Modifier.size(64.dp).clip(MaterialTheme.shapes.small)
        )
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(item.nombre ?: "Nombre no disponible", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text("Cant: ${item.cantidad ?: 0} x ${CurrencyUtils.formatAsCLP(item.precioUnitario)}", style = MaterialTheme.typography.bodyMedium)
        }
        Text(CurrencyUtils.formatAsCLP(subtotal), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PriceDetailRow(label: String, value: String, isTotal: Boolean = false) {
    val fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
    val style = if(isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = fontWeight, style = style)
        Text(value, fontWeight = fontWeight, style = style)
    }
}

@Composable
private fun ShippingDetails(datos: DatosEnvioResponse) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(start = 8.dp)) {
        ShippingDetailItem(Icons.Default.Person, datos.nombre)
        ShippingDetailItem(Icons.Default.Email, datos.email)
        ShippingDetailItem(Icons.Default.Phone, datos.telefono)
        ShippingDetailItem(Icons.Default.Home, "${datos.direccion}, ${datos.ciudad}, ${datos.region}")
    }
}

@Composable
private fun ShippingDetailItem(icon: ImageVector, text: String?) {
     Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(8.dp))
        Text(text ?: "N/A", style = MaterialTheme.typography.bodyMedium)
    }
}

private fun formatDate(dateString: String?): String {
    if (dateString.isNullOrBlank()) return "Fecha desconocida"
    return try {
        // Updated to handle format with microseconds
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(dateString)
        val formatter = SimpleDateFormat("dd MMMM, yyyy", Locale("es", "ES"))
        formatter.format(date!!)
    } catch (e: Exception) {
        // Fallback for format without microseconds
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val date = parser.parse(dateString)
            val formatter = SimpleDateFormat("dd MMMM, yyyy", Locale("es", "ES"))
            formatter.format(date!!)
        } catch (e2: Exception) {
            dateString.split("T").firstOrNull() ?: "Fecha inválida"
        }
    }
}
