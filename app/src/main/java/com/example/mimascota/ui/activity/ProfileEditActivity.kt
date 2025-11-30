@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.mimascota.repository.AuthRepository
import com.example.mimascota.util.TokenManager
import kotlinx.coroutines.launch

class ProfileEditActivity : ComponentActivity() {

    private val authRepo = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Inline UI here (previously in ProfileEditScreen())
            // Load usuario from TokenManager and react to updates
            var nombre by remember { mutableStateOf("") }
            var direccion by remember { mutableStateOf("") }
            var telefono by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var run by remember { mutableStateOf("") }
            // Populate once on composition and when TokenManager has data
            LaunchedEffect(Unit) {
                TokenManager.getUsuario()?.let { u ->
                    nombre = u.nombre
                    direccion = u.direccion ?: ""
                    telefono = u.telefono ?: ""
                    email = u.email
                    run = u.run ?: ""
                }
            }

            var numeroCasa by remember { mutableStateOf("") }
            var isSaving by remember { mutableStateOf(false) }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Editar Perfil") },
                        navigationIcon = {
                            IconButton(onClick = { finish() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
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
                    Text(text = email, modifier = Modifier.fillMaxWidth().padding(4.dp))
                    Spacer(modifier = Modifier.height(8.dp))

                    // RUN (no editable) en rojo
                    Text(text = "RUT (no editable)", color = Color.Red)
                    Text(text = run, modifier = Modifier.fillMaxWidth().padding(4.dp))
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
                                        setResult(RESULT_OK, out)
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
