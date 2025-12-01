package com.example.mimascota.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mimascota.model.OrdenHistorial
import com.example.mimascota.viewModel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminOrdersScreen(adminViewModel: AdminViewModel = viewModel()) {
    val usersWithOrders by adminViewModel.usersWithOrders.collectAsState()
    val orders = usersWithOrders.flatMap { it.orders }
    val isLoading by adminViewModel.isLoading.collectAsState()
    val error by adminViewModel.error.collectAsState()

    when {
        isLoading && orders.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $error")
            }
        }
        orders.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay pedidos para mostrar.")
            }
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    OrderListItemAdmin(orden = order, adminViewModel = adminViewModel)
                }
            }
        }
    }
}

@Composable
fun OrderListItemAdmin(orden: OrdenHistorial, adminViewModel: AdminViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf("") }

    val formattedDate = try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        val date = orden.fecha?.let { parser.parse(it) }
        if (date != null) {
            SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(date)
        } else {
            "Fecha no disponible"
        }
    } catch (e: Exception) {
        orden.fecha ?: "Fecha no disponible"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Orden #${orden.id}", fontWeight = FontWeight.Bold)
            orden.usuarioId?.let { Text("Usuario ID: $it") }
            Text("Fecha: $formattedDate")
            Text("Estado: ${orden.estado ?: "N/A"}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            Text("Total: $${String.format("%.2f", orden.total ?: 0.0)}")

            Spacer(modifier = Modifier.height(8.dp))

            Text("Actualizar Estado:", style = MaterialTheme.typography.labelMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val estados = listOf("procesando", "enviado", "entregado", "cancelado")
                estados.forEach { estado ->
                    Button(
                        onClick = {
                            selectedStatus = estado
                            showDialog = true
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(estado.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar Actualización") },
            text = { Text("¿Estás seguro de que quieres cambiar el estado de la orden #${orden.id} a '$selectedStatus'?") },
            confirmButton = {
                TextButton(onClick = {
                    orden.id.let { adminViewModel.updateOrderStatus(it, selectedStatus) }
                    showDialog = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
