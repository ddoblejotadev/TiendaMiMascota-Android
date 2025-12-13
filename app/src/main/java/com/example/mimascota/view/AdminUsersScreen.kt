package com.example.mimascota.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mimascota.model.Usuario
import com.example.mimascota.viewModel.AdminViewModel
import com.example.mimascota.viewModel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(navController: NavController, adminViewModel: AdminViewModel, authViewModel: AuthViewModel = viewModel()) {
    val usersWithOrders by adminViewModel.usersWithOrders.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()
    var userToDelete by remember { mutableStateOf<Usuario?>(null) }

    // Obtener el ID del administrador actual
    val adminId by authViewModel.usuarioActualId

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Usuarios") }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(usersWithOrders) { userWithOrders ->
                    val user = userWithOrders.user
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(user.nombre, style = MaterialTheme.typography.titleMedium)
                                Text("ID: ${user.usuarioId}", style = MaterialTheme.typography.bodySmall)
                                Text(user.email, style = MaterialTheme.typography.bodyMedium)
                                Text("Rol: ${user.rol ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                            }

                            // Solo mostrar el botón de eliminar si el usuario no es el admin actual
                            if (user.usuarioId != adminId) {
                                IconButton(onClick = { userToDelete = user }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar Usuario", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Diálogo de confirmación para eliminar usuario
        userToDelete?.let { user ->
            AlertDialog(
                onDismissRequest = { userToDelete = null },
                title = { Text("Confirmar Eliminación") },
                text = { Text("¿Estás seguro de que quieres eliminar al usuario '${user.nombre}' (ID: ${user.usuarioId})? Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            adminViewModel.deleteUser(user.usuarioId.toLong())
                            userToDelete = null // Cerrar el diálogo
                        }
                    ) { Text("Eliminar") }
                },
                dismissButton = {
                    TextButton(onClick = { userToDelete = null }) { Text("Cancelar") }
                }
            )
        }
    }
}