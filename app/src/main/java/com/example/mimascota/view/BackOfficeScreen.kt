package com.example.mimascota.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackOfficeScreen(navController: NavController, viewModel: CatalogoViewModel) {
    val productos by viewModel.productos.collectAsState()

    // AdminViewModel local
    val adminViewModel: AdminViewModel = composeViewModel()
    val usuarios by adminViewModel.users.collectAsState()
    val ordenes by adminViewModel.orders.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Productos", "Usuarios", "Órdenes")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Back Office") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(onClick = { navController.navigate("agregarProducto") }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
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
                    adminViewModel.deleteUser(userId) { success, message ->
                        if (success) Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show() else Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                    }
                }, isLoading = isLoading)
                2 -> OrdersTab(ordenes = ordenes, onUpdateStatus = { orderId, newStatus ->
                    adminViewModel.updateOrderStatus(orderId, newStatus) { success, message ->
                        if (success) Toast.makeText(context, "Estado actualizado", Toast.LENGTH_SHORT).show() else Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                    }
                }, isLoading = isLoading)
            }
        }
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
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar usuario")
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
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(ordenes) { orden ->
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = "Orden: ${orden.numeroOrden}")
                    Text(text = "Usuario: ${orden.usuarioId}")
                    Text(text = "Estado: ${orden.estado}")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { onUpdateStatus(orden.id, "procesando") }) { Text("Procesando") }
                        TextButton(onClick = { onUpdateStatus(orden.id, "enviado") }) { Text("Enviar") }
                        TextButton(onClick = { onUpdateStatus(orden.id, "cancelado") }) { Text("Cancelar") }
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
                Text("Stock: ${producto.stock ?: 0}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = {
                navController.navigate("agregarProducto?id=${producto.producto_id}")
            }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = {
                val id = producto.producto_id
                viewModel.deleteProducto(id)
            }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}
