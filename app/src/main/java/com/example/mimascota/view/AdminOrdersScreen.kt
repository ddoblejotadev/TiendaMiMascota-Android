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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mimascota.R
import com.example.mimascota.model.OrdenHistorial
import com.example.mimascota.viewModel.AdminViewModel

@Composable
fun AdminOrdersScreen(adminViewModel: AdminViewModel = viewModel()) {
    val ordenes by adminViewModel.orders.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()
    val errorMsg by adminViewModel.error.collectAsState()
    val context = LocalContext.current

    var selectedUpdateOrderId by remember { mutableStateOf<Long?>(null) }
    var selectedUpdateStatus by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(errorMsg) {
        if (!errorMsg.isNullOrBlank()) {
            Toast.makeText(context, context.getString(R.string.admin_error, errorMsg), Toast.LENGTH_LONG).show()
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(ordenes) { orden ->
                OrderListItem(orden, onUpdateStatus = { orderId, newStatus ->
                    selectedUpdateOrderId = orderId
                    selectedUpdateStatus = newStatus
                })
            }
        }
    }

    if (selectedUpdateOrderId != null && selectedUpdateStatus != null) {
        val orderId = selectedUpdateOrderId!!
        val newStatus = selectedUpdateStatus!!
        AlertDialog(
            onDismissRequest = { 
                selectedUpdateOrderId = null
                selectedUpdateStatus = null
            },
            title = { Text(stringResource(id = R.string.confirm_update_order_title)) },
            text = { Text(stringResource(id = R.string.confirm_update_order_message, newStatus)) },
            confirmButton = {
                TextButton(onClick = {
                    adminViewModel.updateOrderStatus(orderId, newStatus) { success, message ->
                        if (success) {
                            Toast.makeText(context, context.getString(R.string.estado_actualizado), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                        }
                    }
                    selectedUpdateOrderId = null
                    selectedUpdateStatus = null
                }) { Text(stringResource(id = R.string.confirm_button)) }
            },
            dismissButton = {
                TextButton(onClick = { 
                    selectedUpdateOrderId = null
                    selectedUpdateStatus = null
                }) { Text(stringResource(id = R.string.cancel_button)) }
            }
        )
    }
}

@Composable
fun OrderListItem(orden: OrdenHistorial, onUpdateStatus: (Long, String) -> Unit) {
    val estadoProcesando = stringResource(id = R.string.estado_procesando)
    val estadoEnviado = stringResource(id = R.string.estado_enviado)
    val estadoCancelado = stringResource(id = R.string.estado_cancelado)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.order_label, orden.numeroOrden),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(id = R.string.user_label, orden.usuarioId.toString()),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(id = R.string.estado_label, orden.estado),
                style = MaterialTheme.typography.bodyMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { onUpdateStatus(orden.id, estadoProcesando) }) { Text(estadoProcesando) }
                TextButton(onClick = { onUpdateStatus(orden.id, estadoEnviado) }) { Text(estadoEnviado) }
                TextButton(onClick = { onUpdateStatus(orden.id, estadoCancelado) }) { Text(estadoCancelado) }
            }
        }
    }
}
