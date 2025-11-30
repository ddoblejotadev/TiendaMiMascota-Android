package com.example.mimascota.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.mimascota.model.Usuario
import com.example.mimascota.repository.AuthRepository
import com.example.mimascota.util.TokenManager
import kotlinx.coroutines.launch

class ProfileEditActivity : ComponentActivity() {

    private val authRepo = AuthRepository()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Inline UI here (previously in ProfileEditScreen())
            val usuario = TokenManager.getUsuario()
            var nombre by remember { mutableStateOf(usuario?.nombre ?: "") }
            var direccion by remember { mutableStateOf(usuario?.direccion ?: "") }
            var telefono by remember { mutableStateOf(usuario?.telefono ?: "") }
            var numeroCasa by remember { mutableStateOf("") }
            var isSaving by remember { mutableStateOf(false) }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Editar Perfil") },
                        navigationIcon = {
                            IconButton(onClick = { finish() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                            }
                        }
                    )
                }
            ) { padding ->
                Column(modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)) {

                    // Nombre editable
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Email (no editable) en rojo
                    Text(text = "Email (no editable)", color = Color.Red)
                    Text(text = usuario?.email ?: "", modifier = Modifier.fillMaxWidth().padding(4.dp))
                    Spacer(modifier = Modifier.height(8.dp))

                    // RUN (no editable) en rojo
                    Text(text = "RUT (no editable)", color = Color.Red)
                    Text(text = usuario?.run ?: "", modifier = Modifier.fillMaxWidth().padding(4.dp))
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = numeroCasa, onValueChange = { numeroCasa = it }, label = { Text("Número / Casa") }, modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        // Guardar cambios async
                        isSaving = true
                        lifecycleScope.launch {
                            try {
                                val current = TokenManager.getUsuario()
                                if (current != null) {
                                    val updated = current.copy(nombre = nombre, direccion = direccion, telefono = telefono)
                                    val result = authRepo.updateUsuario(updated)
                                    if (result.isSuccess) {
                                        // Devolver el nombre actualizado al Activity que lanzó
                                        val out = Intent().apply {
                                            putExtra("updated_name", updated.nombre)
                                        }
                                        setResult(Activity.RESULT_OK, out)
                                        Toast.makeText(this@ProfileEditActivity, "Perfil actualizado", Toast.LENGTH_SHORT).show()
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
                        Text(if (isSaving) "Guardando..." else "Guardar")
                    }
                }
            }
        }
    }
}
