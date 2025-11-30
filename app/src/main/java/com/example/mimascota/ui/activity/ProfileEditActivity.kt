@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.mimascota.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.mimascota.R
import com.example.mimascota.repository.AuthRepository
import com.example.mimascota.util.TokenManager
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

class ProfileEditActivity : ComponentActivity() {

    private val authRepo = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var nombre by remember { mutableStateOf("") }
            var direccion by remember { mutableStateOf("") }
            var telefono by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var run by remember { mutableStateOf("") }
            var fotoUrl by remember { mutableStateOf<String?>(null) }

            var imageUri by remember { mutableStateOf<Uri?>(null) }
            val context = LocalContext.current

            var showImageSourceDialog by remember { mutableStateOf(false) }

            fun createImageUri(): Uri {
                val image = File(context.cacheDir, "profile_pic.jpg")
                return FileProvider.getUriForFile(context, "${context.packageName}.provider", image)
            }

            val galleryLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent(),
                onResult = { uri: Uri? -> if (uri != null) imageUri = uri }
            )

            val cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicture(),
                onResult = { success -> if (success) Log.d("ProfileEditActivity", "✅ Foto tomada con éxito.") }
            )

            val cameraPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    val newUri = createImageUri()
                    imageUri = newUri
                    cameraLauncher.launch(newUri)
                } else {
                    Toast.makeText(context, "Permiso de cámara denegado.", Toast.LENGTH_SHORT).show()
                }
            }

            LaunchedEffect(Unit) {
                TokenManager.getUsuario()?.let { u ->
                    nombre = u.nombre
                    direccion = u.direccion ?: ""
                    telefono = u.telefono ?: ""
                    email = u.email
                    run = u.run ?: ""
                    fotoUrl = u.fotoUrl
                }
            }

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
                Column(modifier = Modifier.padding(padding).padding(16.dp)) {

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = imageUri ?: fotoUrl,
                            contentDescription = "Foto de perfil",
                            placeholder = painterResource(id = R.drawable.logo1),
                            error = painterResource(id = R.drawable.logo1),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(120.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { showImageSourceDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cambiar Foto")
                    }

                    if (showImageSourceDialog) {
                        Dialog(onDismissRequest = { showImageSourceDialog = false }) {
                            Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Cambiar foto de perfil", style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    TextButton(
                                        onClick = {
                                            showImageSourceDialog = false
                                            galleryLauncher.launch("image/*")
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Elegir desde la galería")
                                    }
                                    TextButton(
                                        onClick = {
                                            showImageSourceDialog = false
                                            when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
                                                PackageManager.PERMISSION_GRANTED -> {
                                                    val newUri = createImageUri()
                                                    imageUri = newUri
                                                    cameraLauncher.launch(newUri)
                                                }
                                                else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Tomar foto con la cámara")
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(
                                        onClick = { showImageSourceDialog = false },
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("Cancelar")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Email (no editable)", color = Color.Red)
                    Text(text = email, modifier = Modifier.fillMaxWidth().padding(4.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "RUT (no editable)", color = Color.Red)
                    Text(text = run, modifier = Modifier.fillMaxWidth().padding(4.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            isSaving = true
                            lifecycleScope.launch {
                                try {
                                    val current = TokenManager.getUsuario()
                                    if (current == null) {
                                        Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                                        return@launch
                                    }

                                    val base64Image = imageUri?.let { uri ->
                                        Log.d("ProfileEditActivity", "🔄 Redimensionando y convirtiendo imagen a Base64...")
                                        try {
                                            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                                                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                                            } else {
                                                val source = ImageDecoder.createSource(context.contentResolver, uri)
                                                ImageDecoder.decodeBitmap(source)
                                            }

                                            val maxHeight = 800
                                            val maxWidth = 800
                                            val scale = minOf(maxHeight.toFloat() / bitmap.height, maxWidth.toFloat() / bitmap.width)
                                            val newWidth = (bitmap.width * scale).toInt()
                                            val newHeight = (bitmap.height * scale).toInt()

                                            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

                                            val outputStream = ByteArrayOutputStream()
                                            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                                            val byteArray = outputStream.toByteArray()

                                            "data:image/jpeg;base64,${Base64.encodeToString(byteArray, Base64.DEFAULT)}"
                                        } catch (e: Exception) {
                                            Log.e("ProfileEditActivity", "❌ Error al procesar la imagen: ${e.message}", e)
                                            null
                                        }
                                    }

                                    val updated = current.copy(
                                        nombre = nombre,
                                        direccion = direccion,
                                        telefono = telefono,
                                        fotoUrl = base64Image ?: current.fotoUrl
                                    )

                                    Log.d("ProfileEditActivity", "🚀 Enviando actualización al repositorio...")
                                    val result = authRepo.updateUsuario(updated)

                                    if (result.isSuccess) {
                                        Log.d("ProfileEditActivity", "✅ Perfil actualizado con éxito en el backend.")
                                        TokenManager.saveUsuario(updated)
                                        setResult(RESULT_OK, intent.apply { putExtra("updated_name", updated.nombre) })
                                        Toast.makeText(this@ProfileEditActivity, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                                        finish()
                                    } else {
                                        Log.e("ProfileEditActivity", "❌ Error al actualizar perfil: ${result.exceptionOrNull()?.message}")
                                        Toast.makeText(this@ProfileEditActivity, "Error: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                    }

                                } catch (e: Exception) {
                                    Log.e("ProfileEditActivity", "🔥 Excepción catastrófica al guardar: ${e.message}", e)
                                    Toast.makeText(this@ProfileEditActivity, "Error al actualizar", Toast.LENGTH_LONG).show()
                                } finally {
                                    isSaving = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving
                    ) {
                        Text(if (isSaving) "Guardando..." else "Guardar")
                    }
                }
            }
        }
    }
}
