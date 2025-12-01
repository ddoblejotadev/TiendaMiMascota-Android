package com.example.mimascota.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mimascota.model.OrdenHistorial
import com.example.mimascota.viewModel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminOrdersScreen(adminViewModel: AdminViewModel = viewModel()) {
    val orders by adminViewModel.orders.collectAsState()

    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay pedidos para mostrar.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { order ->
                OrderListItemAdmin(order, adminViewModel)
            }
        }
    }
}

@Composable
fun OrderListItemAdmin(orden: OrdenHistorial, adminViewModel: AdminViewModel) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf("") }

    val formattedDate = try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        val date = parser.parse(orden.fecha)
        SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(date!!)
    } catch (e: Exception) {
        orden.fecha
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Orden #${orden.id}", fontWeight = FontWeight.Bold)
            Text("Fecha: $formattedDate")
            Text("Estado: ${orden.estado}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            Text("Total: $${String.format("%.2f", orden.total)}")
            Text("Usuario ID: ${orden.usuarioId}")

            Spacer(modifier = Modifier.height(16.dp))

            Text("Actualizar Estado:", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                val estados = listOf("procesando", "enviado", "entregado", "cancelado")
                estados.forEach { estado ->
                    Button(onClick = {
                        selectedStatus = estado
                        showDialog = true
                    }) {
                        Text(estado.replaceFirstChar { it.uppercase() })
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar Actualización") },
            text = { Text("¿Estás seguro de que quieres cambiar el estado a '$selectedStatus'?") },
            confirmButton = {
                TextButton(onClick = {
                    adminViewModel.updateOrderStatus(orden.id, selectedStatus)
                    showDialog = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
