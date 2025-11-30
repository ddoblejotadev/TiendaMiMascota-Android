package com.example.mimascota.view

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mimascota.R
import com.example.mimascota.model.Usuario
import com.example.mimascota.viewModel.AdminViewModel

@Composable
fun AdminUsersScreen(adminViewModel: AdminViewModel = viewModel()) {
    val usuarios by adminViewModel.users.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()
    val errorMsg by adminViewModel.error.collectAsState()
    val context = LocalContext.current

    var selectedDeleteUserId by remember { mutableStateOf<Long?>(null) }

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
            items(usuarios) { user ->
                UserListItem(user, onDelete = { userId ->
                    selectedDeleteUserId = userId
                })
            }
        }
    }

    if (selectedDeleteUserId != null) {
        val idToDelete = selectedDeleteUserId!!
        AlertDialog(
            onDismissRequest = { selectedDeleteUserId = null },
            title = { Text(stringResource(id = R.string.confirm_delete_user_title)) },
            text = { Text(stringResource(id = R.string.confirm_delete_user_message)) },
            confirmButton = {
                TextButton(onClick = {
                    adminViewModel.deleteUser(idToDelete) { success, message ->
                        if (success) {
                            Toast.makeText(context, context.getString(R.string.usuario_eliminado), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                        }
                    }
                    selectedDeleteUserId = null
                }) { Text(stringResource(id = R.string.confirm_button)) }
            },
            dismissButton = {
                TextButton(onClick = { selectedDeleteUserId = null }) { Text(stringResource(id = R.string.cancel_button)) }
            }
        )
    }
}

@Composable
fun UserListItem(user: Usuario, onDelete: (Long) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.nombre, style = MaterialTheme.typography.titleMedium)
                Text(user.email, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { onDelete(user.usuarioId.toLong()) }) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.eliminar_label))
            }
        }
    }
}
