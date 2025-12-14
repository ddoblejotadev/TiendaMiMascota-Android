package com.example.mimascota.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mimascota.R
import com.example.mimascota.model.Producto
import com.example.mimascota.viewModel.AdminViewModel
import java.io.ByteArrayOutputStream
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductCreateScreen(navController: NavController, adminViewModel: AdminViewModel) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imageUrlInput by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tipoMascota by remember { mutableStateOf("") }
    var raza by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var pesoMascota by remember { mutableStateOf("") }

    val context = LocalContext.current
    var showImageSourceDialog by remember { mutableStateOf(false) }

    fun createImageUri(): Uri {
        val image = File(context.cacheDir, "new_product_pic.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", image)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                imageUri = uri
                imageUrlInput = ""
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                imageUrlInput = ""
            }
        }
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Nuevo Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = imageUri ?: imageUrlInput.takeIf { it.isNotBlank() },
                contentDescription = "Imagen del producto",
                placeholder = painterResource(id = R.drawable.logo1),
                error = painterResource(id = R.drawable.logo1),
                modifier = Modifier.size(150.dp).align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = imageUrlInput,
                onValueChange = {
                    imageUrlInput = it
                    imageUri = null
                },
                label = { Text("URL de la imagen") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { showImageSourceDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Text("O tomar/elegir foto")
            }

            if (showImageSourceDialog) {
                Dialog(onDismissRequest = { showImageSourceDialog = false }) {
                    Surface(modifier = Modifier.padding(16.dp)) {
                        Column {
                            TextButton(onClick = { showImageSourceDialog = false; galleryLauncher.launch("image/*") }) {
                                Text("Elegir de la Galería")
                            }
                            TextButton(onClick = {
                                showImageSourceDialog = false
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    val newUri = createImageUri(); imageUri = newUri; cameraLauncher.launch(newUri)
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }) {
                                Text("Tomar Foto")
                            }
                        }
                    }
                }
            }

            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

            // Campos para recomendaciones
            Text("Campos para Recomendaciones", style = MaterialTheme.typography.titleMedium)

            // Editable Dropdown for Tipo de Mascota
            val tiposMascotaSugeridos = listOf("Perro", "Gato", "Ambos", "Otro")
            EditableDropdownMenu(
                label = "Tipo de Mascota",
                options = tiposMascotaSugeridos,
                value = tipoMascota,
                onValueChange = { tipoMascota = it }
            )

            // Dropdown for Raza/Tamaño
            val razas = listOf("Raza Pequeña", "Raza Mediana", "Raza Grande", "Todas las razas")
            DropdownMenu(label = "Raza/Tamaño", options = razas, selected = raza, onSelected = { raza = it })

            // Dropdown for Edad
            val edades = listOf("Cachorro/Gatito", "Adulto", "Senior", "Todas las edades")
            DropdownMenu(label = "Edad", options = edades, selected = edad, onSelected = { edad = it })

            OutlinedTextField(value = pesoMascota, onValueChange = { pesoMascota = it }, label = { Text("Peso en Kg (ej. 4)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val finalImageUrl = imageUri?.let { uri ->
                        try {
                            val bitmap = if (Build.VERSION.SDK_INT < 28) MediaStore.Images.Media.getBitmap(context.contentResolver, uri) else ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                            val maxHeight = 800; val maxWidth = 800
                            val scale = minOf(maxHeight.toFloat() / bitmap.height, maxWidth.toFloat() / bitmap.width)
                            val newWidth = (bitmap.width * scale).toInt(); val newHeight = (bitmap.height * scale).toInt()
                            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
                            val outputStream = ByteArrayOutputStream()
                            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                            "data:image/jpeg;base64,${Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)}"
                        } catch (e: Exception) { imageUrlInput }
                    } ?: imageUrlInput

                    val nuevoProducto = Producto(
                        producto_id = 0,
                        producto_nombre = nombre,
                        description = descripcion,
                        price = precio.toDoubleOrNull() ?: 0.0,
                        stock = stock.toIntOrNull() ?: 0,
                        imageUrl = finalImageUrl,
                        category = "General",
                        tipoMascota = tipoMascota.takeIf { it.isNotBlank() },
                        raza = raza.takeIf { it.isNotBlank() },
                        edad = edad.takeIf { it.isNotBlank() },
                        pesoMascota = pesoMascota.toDoubleOrNull()
                    )
                    adminViewModel.createProducto(nuevoProducto)
                    Toast.makeText(context, "Producto creado exitosamente", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nombre.isNotBlank() && precio.isNotBlank()
            ) {
                Text("Crear Producto")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenu(label: String, options: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableDropdownMenu(label: String, options: List<String>, value: String, onValueChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}