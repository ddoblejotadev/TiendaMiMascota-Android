package com.example.mimascota.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mimascota.model.OrdenHistorial
import com.example.mimascota.model.UserWithOrders
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
                items(usersWhoHaveOrdered) { userWithOrders ->
                    UserOrdersSection(userWithOrders = userWithOrders, adminViewModel = adminViewModel)
                }
            }
        }
    }
}

@Composable
fun UserOrdersSection(userWithOrders: UserWithOrders, adminViewModel: AdminViewModel) {
    var isExpanded by remember { mutableStateOf(false) }

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
                    Text(userWithOrders.user.nombre, fontWeight = FontWeight.Bold)
                    Text(userWithOrders.user.email, style = MaterialTheme.typography.bodySmall)
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Desplegar",
                    modifier = Modifier.rotate(if (isExpanded) 180f else 0f)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    userWithOrders.orders.forEach { order ->
                        Divider()
                        AdminOrderCard(orden = order, onStatusChange = {
                            adminViewModel.updateOrderStatus(order.id, it)
                        })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderCard(orden: OrdenHistorial, onStatusChange: (String) -> Unit) {
    var isStatusMenuExpanded by remember { mutableStateOf(false) }

    val formattedDate = remember(orden.fecha) {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val date = orden.fecha?.let { parser.parse(it) }
            date?.let { SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(it) } ?: "Fecha inválida"
        } catch (e: Exception) {
            orden.fecha ?: "Fecha desconocida"
        }
    }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Orden #${orden.id}", fontWeight = FontWeight.SemiBold)
        Text("Fecha: $formattedDate")
        Text("Total: $${String.format("%.2f", orden.total ?: 0.0)}")

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = isStatusMenuExpanded,
            onExpandedChange = { isStatusMenuExpanded = !isStatusMenuExpanded },
        ) {
            OutlinedTextField(
                value = orden.estado ?: "SIN ESTADO",
                onValueChange = {}, // No editable directamente
                readOnly = true,
                label = { Text("Estado del Pedido") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusMenuExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = isStatusMenuExpanded,
                onDismissRequest = { isStatusMenuExpanded = false },
            ) {
                ORDER_STATUSES.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status) },
                        onClick = {
                            onStatusChange(status)
                            isStatusMenuExpanded = false
                        }
                    )
                }
            }
        }
    }
}
