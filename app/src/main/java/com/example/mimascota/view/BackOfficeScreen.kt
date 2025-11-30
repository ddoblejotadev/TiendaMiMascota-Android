package com.example.mimascota.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimascota.model.Producto
import com.example.mimascota.viewModel.CatalogoViewModel
import com.example.mimascota.viewModel.AdminViewModel
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import com.example.mimascota.model.Usuario
import com.example.mimascota.model.OrdenHistorial
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.example.mimascota.R
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_VALUE")
@Composable
fun BackOfficeScreen(navController: NavController, viewModel: CatalogoViewModel) {
    val productos by viewModel.productos.collectAsState()

    // AdminViewModel local
    val adminViewModel: AdminViewModel = composeViewModel()
    val usuarios by adminViewModel.users.collectAsState()
    val ordenes by adminViewModel.orders.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val errorMsg by adminViewModel.error.collectAsState()

    // Mostrar error en Toast cuando cambie
    LaunchedEffect(errorMsg) {
        if (!errorMsg.isNullOrBlank()) {
            Toast.makeText(context, context.getString(R.string.admin_error, errorMsg), Toast.LENGTH_LONG).show()
        }
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        stringResource(id = R.string.productos_tab),
        stringResource(id = R.string.usuarios_tab),
        stringResource(id = R.string.ordenes_tab)
    )

    // Use nullable selected ids to control dialog visibility (null = closed)
    var selectedDeleteUserId by remember { mutableStateOf<Long?>(null) }
    var selectedUpdateOrderId by remember { mutableStateOf<Long?>(null) }
    var selectedUpdateStatus by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.back_office_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.volver_label))
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(onClick = { navController.navigate("agregarProducto") }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.agregar_producto))
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }) {
                        Text(title, modifier = Modifier.padding(16.dp))
                    }
                }
            }

            when (selectedTab) {
                0 -> ProductsTab(productos = productos, navController = navController, viewModel = viewModel)
                1 -> UsersTab(usuarios = usuarios, onDelete = { userId ->
                    selectedDeleteUserId = userId
                }, isLoading = isLoading)
                2 -> OrdersTab(ordenes = ordenes, onUpdateStatus = { orderId, newStatus ->
                    selectedUpdateOrderId = orderId
                    selectedUpdateStatus = newStatus
                }, isLoading = isLoading)
            }
        }
    }

    // Delete confirmation dialog
    if (selectedDeleteUserId != null) {
        val idToDelete = selectedDeleteUserId!!
        AlertDialog(onDismissRequest = {
            selectedDeleteUserId = null
        },
            title = { Text(stringResource(id = R.string.confirm_delete_user_title)) },
            text = { Text(stringResource(id = R.string.confirm_delete_user_message)) },
            confirmButton = {
                TextButton(onClick = {
                    adminViewModel.deleteUser(idToDelete) { success, message ->
                        if (success) Toast.makeText(context, context.getString(R.string.usuario_eliminado), Toast.LENGTH_SHORT).show() else Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                    }
                    selectedDeleteUserId = null
                }) { Text(stringResource(id = R.string.confirm_button)) }
            },
            dismissButton = { TextButton(onClick = {
                selectedDeleteUserId = null
            }) { Text(stringResource(id = R.string.cancel_button)) } }
        )
    }

    // Update order confirmation dialog
    if (selectedUpdateOrderId != null && selectedUpdateStatus != null) {
        val orderId = selectedUpdateOrderId!!
        val newStatus = selectedUpdateStatus!!
        AlertDialog(onDismissRequest = {
            selectedUpdateOrderId = null
            selectedUpdateStatus = null
        },
            title = { Text(stringResource(id = R.string.confirm_update_order_title)) },
            text = { Text(stringResource(id = R.string.confirm_update_order_message, newStatus)) },
            confirmButton = {
                TextButton(onClick = {
                    adminViewModel.updateOrderStatus(orderId, newStatus) { success, message ->
                        if (success) Toast.makeText(context, context.getString(R.string.estado_actualizado), Toast.LENGTH_SHORT).show() else Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                    }
                    selectedUpdateOrderId = null
                    selectedUpdateStatus = null
                }) { Text(stringResource(id = R.string.confirm_button)) }
            },
            dismissButton = { TextButton(onClick = {
                selectedUpdateOrderId = null
                selectedUpdateStatus = null
            }) { Text(stringResource(id = R.string.cancel_button)) } }
        )
    }
}

@Composable
fun ProductsTab(productos: List<Producto>, navController: NavController, viewModel: CatalogoViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(productos) { producto ->
            ProductoBackOfficeItem(producto, navController, viewModel)
        }
    }
}

@Composable
fun UsersTab(usuarios: List<Usuario>, onDelete: (Long) -> Unit, isLoading: Boolean) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(usuarios) { user ->
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.nombre)
                        Text(user.email)
                    }
                    IconButton(onClick = { onDelete(user.usuarioId.toLong()) }) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.eliminar_label))
                    }
                }
            }
        }
    }
}

@Composable
fun OrdersTab(ordenes: List<OrdenHistorial>, onUpdateStatus: (Long, String) -> Unit, isLoading: Boolean) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    // Capturar los textos fuera de los lambdas para poder usarlos en onClick
    val estadoProcesando = stringResource(id = R.string.estado_procesando)
    val estadoEnviado = stringResource(id = R.string.estado_enviado)
    val estadoCancelado = stringResource(id = R.string.estado_cancelado)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(ordenes) { orden ->
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = stringResource(id = com.example.mimascota.R.string.order_label, orden.numeroOrden))
                    Text(text = stringResource(id = com.example.mimascota.R.string.user_label, orden.usuarioId.toString()))
                    Text(text = stringResource(id = com.example.mimascota.R.string.estado_label, orden.estado))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { onUpdateStatus(orden.id, estadoProcesando) }) { Text(estadoProcesando) }
                        TextButton(onClick = { onUpdateStatus(orden.id, estadoEnviado) }) { Text(estadoEnviado) }
                        TextButton(onClick = { onUpdateStatus(orden.id, estadoCancelado) }) { Text(estadoCancelado) }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoBackOfficeItem(producto: Producto, navController: NavController, viewModel: CatalogoViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.producto_nombre, style = MaterialTheme.typography.titleMedium)
                Text(String.format(stringResource(id = R.string.product_stock), producto.stock ?: 0), style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = {
                navController.navigate("agregarProducto?id=${producto.producto_id}")
            }) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.editar_label))
            }
            IconButton(onClick = {
                val id = producto.producto_id
                viewModel.deleteProducto(id)
            }) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.eliminar_label))
            }
        }
    }
}
