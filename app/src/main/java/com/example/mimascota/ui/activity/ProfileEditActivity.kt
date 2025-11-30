package com.example.mimascota.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.mimascota.model.Usuario
import com.example.mimascota.repository.AuthRepository
import com.example.mimascota.util.TokenManager
import kotlinx.coroutines.launch

class ProfileEditActivity : ComponentActivity() {

    private val authRepo = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileEditScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProfileEditScreen() {
        val usuario = TokenManager.getUsuario()
        var direccion by remember { mutableStateOf(usuario?.direccion ?: "") }
        var telefono by remember { mutableStateOf(usuario?.telefono ?: "") }
        var region by remember { mutableStateOf("") }
        var ciudad by remember { mutableStateOf("") }
        var codigoPostal by remember { mutableStateOf("") }
        var isSaving by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(title = { Text("Editar Dirección de Envío") })
            }
        ) { padding ->
            Column(modifier = Modifier
                .padding(padding)
                .padding(16.dp)) {

                OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = ciudad, onValueChange = { ciudad = it }, label = { Text("Ciudad / Comuna") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = codigoPostal, onValueChange = { codigoPostal = it }, label = { Text("Número / Casa") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    // Guardar cambios async
                    isSaving = true
                    lifecycleScope.launch {
                        try {
                            val current = TokenManager.getUsuario()
                            if (current != null) {
                                val updated = current.copy(direccion = direccion, telefono = telefono)
                                val result = authRepo.updateUsuario(updated)
                                if (result.isSuccess) {
                                    Toast.makeText(this@ProfileEditActivity, "Dirección actualizada", Toast.LENGTH_SHORT).show()
                                    finish()
                                } else {
                                    Toast.makeText(this@ProfileEditActivity, "Error: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(this@ProfileEditActivity, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this@ProfileEditActivity, "Error al actualizar", Toast.LENGTH_LONG).show()
                        } finally {
                            isSaving = false
                        }
                    }
                }, modifier = Modifier.fillMaxWidth(), enabled = !isSaving) {
                    Text(if (isSaving) "Guardando..." else "Guardar Dirección")
                }
            }
        }
    }
}
