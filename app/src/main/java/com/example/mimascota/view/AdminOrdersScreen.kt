package com.example.mimascota.view

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mimascota.R
import com.example.mimascota.model.OrdenHistorial
import com.example.mimascota.model.UserWithOrders
import com.example.mimascota.viewModel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminOrdersScreen(adminViewModel: AdminViewModel = viewModel()) {
    val usersWithOrders by adminViewModel.usersWithOrders.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()
    val errorMsg by adminViewModel.error.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(errorMsg) {
        if (!errorMsg.isNullOrBlank()) {
            Toast.makeText(context, context.getString(R.string.admin_error, errorMsg), Toast.LENGTH_LONG).show()
        }
    }

    if (isLoading && usersWithOrders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(usersWithOrders) { userWithOrders ->
                UserOrdersItem(userWithOrders, adminViewModel)
            }
        }
    }
}

@Composable
fun UserOrdersItem(userWithOrders: UserWithOrders, adminViewModel: AdminViewModel) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(userWithOrders.user.nombre, style = MaterialTheme.typography.titleLarge)
                    Text(userWithOrders.user.email, style = MaterialTheme.typography.bodyMedium)
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Colapsar" else "Expandir"
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    userWithOrders.orders.forEach { order ->
                        OrderListItemAdmin(order, adminViewModel)
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
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
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = parser.parse(orden.fecha)
        SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(date)
    } catch (e: Exception) {
        orden.fecha
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Orden #${orden.numeroOrden}", fontWeight = FontWeight.Bold)
        Text("Fecha: $formattedDate")
        Text("Estado: ${orden.estado}")
        Text("Total: $${String.format("%.2f", orden.total)}")
        
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

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(id = R.string.confirm_update_order_title)) },
            text = { Text(stringResource(id = R.string.confirm_update_order_message, selectedStatus)) },
            confirmButton = {
                TextButton(onClick = {
                    adminViewModel.updateOrderStatus(orden.id, selectedStatus) { success, message ->
                        if (success) {
                            Toast.makeText(context, context.getString(R.string.estado_actualizado), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                        }
                    }
                    showDialog = false
                }) { Text(stringResource(id = R.string.confirm_button)) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text(stringResource(id = R.string.cancel_button)) }
            }
        )
    }
}
