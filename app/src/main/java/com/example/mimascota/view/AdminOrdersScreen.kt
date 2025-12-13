package com.example.mimascota.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import java.util.*

// Estados posibles para una orden
private val ORDER_STATUSES = listOf("PENDIENTE", "EN PROCESO", "ENVIADO", "ENTREGADO", "CANCELADO")

@Composable
fun AdminOrdersScreen(adminViewModel: AdminViewModel = viewModel()) {
    val usersWithOrders by adminViewModel.usersWithOrders.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()
    val error by adminViewModel.error.collectAsState()

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

@Composable
fun UserOrdersAccordion(userWithOrders: UserWithOrders, adminViewModel: AdminViewModel) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
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
                    contentDescription = "Desplegar",
                    modifier = Modifier.rotate(rotationAngle)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    userWithOrders.orders.forEach { order ->
                        AdminOrderCard(orden = order, onStatusChange = adminViewModel::updateOrderStatus)
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrderCard(orden: OrdenHistorial, onStatusChange: (Long, String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // --- Detalles del Pedido ---
            OrderDetails(orden = orden, onStatusChange = onStatusChange)

            // --- Productos ---
            orden.productos?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Productos", style = MaterialTheme.typography.titleMedium)
                it.forEach { item ->
                    ProductOrderItem(item)
                }
            }

            // --- Datos de Envío ---
            orden.datosEnvio?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Datos de Envío", style = MaterialTheme.typography.titleMedium)
                ShippingDetails(it)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderDetails(orden: OrdenHistorial, onStatusChange: (Long, String) -> Unit) {
    var isStatusMenuExpanded by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf("") }

    val formattedDate = remember(orden.fecha) {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val date = orden.fecha?.let { parser.parse(it) }
            date?.let { SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(it) } ?: "Fecha inválida"
        } catch (e: Exception) {
            orden.fecha ?: "Fecha desconocida"
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirmar Cambio de Estado") },
            text = { Text("¿Estás seguro de que quieres cambiar el estado del pedido a '$selectedStatus'?") },
            confirmButton = {
                Button(onClick = {
                    onStatusChange(orden.id, selectedStatus)
                    showConfirmationDialog = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                Button(onClick = { showConfirmationDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Text("Orden #${orden.id} - $formattedDate", style = MaterialTheme.typography.titleMedium)
    Text("Total: ${CurrencyUtils.formatAsCLP(orden.total)}", style = MaterialTheme.typography.bodyLarge)
    Spacer(modifier = Modifier.height(8.dp))

    ExposedDropdownMenuBox(
        expanded = isStatusMenuExpanded,
        onExpandedChange = { isStatusMenuExpanded = !isStatusMenuExpanded },
    ) {
        OutlinedTextField(
            value = orden.estado ?: "SIN ESTADO",
            onValueChange = {}, // No editable
            readOnly = true,
            label = { Text("Estado del Pedido") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusMenuExpanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = isStatusMenuExpanded, onDismissRequest = { isStatusMenuExpanded = false }) {
            ORDER_STATUSES.forEach { status ->
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

@Composable
private fun ProductOrderItem(item: ProductoOrden) {
    val subtotal = (item.cantidad ?: 0) * (item.precioUnitario ?: 0.0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProductImage(
            imageUrl = item.imagen,
            contentDescription = item.nombre,
            modifier = Modifier
                .size(64.dp)
                .clip(MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.nombre ?: "Nombre no disponible",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "ID: ${item.productoId}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Cant: ${item.cantidad ?: 0} x ${CurrencyUtils.formatAsCLP(item.precioUnitario)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            CurrencyUtils.formatAsCLP(subtotal),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ShippingDetails(datos: DatosEnvioResponse) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(datos.nombre ?: "N/A")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(datos.email ?: "N/A")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(datos.telefono ?: "N/A")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("${datos.direccion}, ${datos.ciudad}, ${datos.region}")
        }
    }
}
