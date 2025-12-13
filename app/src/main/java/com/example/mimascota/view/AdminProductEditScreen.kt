package com.example.mimascota.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
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
import com.example.mimascota.R
import com.example.mimascota.model.Producto
import com.example.mimascota.viewModel.AdminViewModel
import java.io.ByteArrayOutputStream
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductEditScreen(navController: NavController, adminViewModel: AdminViewModel, productId: Int) {
    
    val producto by adminViewModel.selectedProduct.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imageUrlInput by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var tipoMascota by remember { mutableStateOf("") }
    var raza by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var pesoMascota by remember { mutableStateOf("") }
    
    LaunchedEffect(key1 = productId) {
        adminViewModel.getProductoById(productId.toString())
    }

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
                imageUri = cameraImageUri
                imageUrlInput = ""
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val newUri = createImageUri()
            cameraImageUri = newUri
            cameraLauncher.launch(newUri)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado.", Toast.LENGTH_SHORT).show()
        }
    }
    
    LaunchedEffect(producto) {
        producto?.let { p ->
            nombre = p.producto_nombre
            var currentDescription = p.description ?: ""
            precio = p.price.toString()
            stock = p.stock?.toString() ?: "0"

            // --- Lógica de Migración y Limpieza de Datos ---
            var finalTipoMascota = p.tipoMascota ?: ""
            var finalRaza = p.raza ?: ""
            var finalEdad = p.edad ?: ""
            var finalPesoMascota = p.pesoMascota?.toString() ?: ""
            var wasDataMigrated = false

            // Si los campos nuevos están vacíos, intentar extraer de la descripción
            if (finalTipoMascota.isBlank() && finalRaza.isBlank() && finalEdad.isBlank() && finalPesoMascota.isBlank()) {
                val linesToKeep = mutableListOf<String>()
                val recommendationKeys = setOf("tipo de mascota", "raza", "edad", "peso")

                currentDescription.lines().forEach { line ->
                    val parts = line.split(":", limit = 2)
                    var isRecommendationLine = false
                    if (parts.size == 2) {
                        val key = parts[0].trim().lowercase()
                        val value = parts[1].trim()
                        if (key in recommendationKeys) {
                            isRecommendationLine = true
                            wasDataMigrated = true
                            when (key) {
                                "tipo de mascota" -> finalTipoMascota = value
                                "raza" -> finalRaza = value
                                "edad" -> finalEdad = value
                                "peso" -> finalPesoMascota = value
                            }
                        }
                    }
                    if (!isRecommendationLine) {
                        linesToKeep.add(line)
                    }
                }

                // Si se migraron datos, actualizar la descripción
                if (wasDataMigrated) {
                    currentDescription = linesToKeep.joinToString(separator = "\n").trim()
                }
            }

            descripcion = currentDescription
            tipoMascota = finalTipoMascota
            raza = finalRaza
            edad = finalEdad
            pesoMascota = finalPesoMascota
            // --- Fin de la Migración ---

            val url = p.imageUrl
            if (url != null && url.startsWith("http")) {
                imageUrlInput = url
            } else {
                imageUrlInput = ""
            }
            imageUri = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) {
        if (isLoading || producto == null) {
            Box(modifier = Modifier.fillMaxSize().padding(it), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val displayModel: Any? = remember(imageUri, imageUrlInput, producto?.imageUrl) {
                    imageUri ?: if (imageUrlInput.isNotBlank()) imageUrlInput else producto?.imageUrl
                }

                ProductImage(
                    imageUrl = displayModel?.toString(),
                    contentDescription = "Imagen del producto",
                    modifier = Modifier.size(150.dp).align(Alignment.CenterHorizontally)
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
                                        val newUri = createImageUri()
                                        cameraImageUri = newUri
                                        cameraLauncher.launch(newUri)
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
                OutlinedTextField(value = tipoMascota, onValueChange = { tipoMascota = it }, label = { Text("Tipo de Mascota (ej. Perro, Gato)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = raza, onValueChange = { raza = it }, label = { Text("Raza (ej. Pastor Alemán)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = edad, onValueChange = { edad = it }, label = { Text("Edad (ej. 3 años)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = pesoMascota, onValueChange = { pesoMascota = it }, label = { Text("Peso en Kg (ej. 4)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())


                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        producto?.let { originalProduct ->
                            val finalImageUrl = imageUri?.let { uri ->
                                try {
                                    val bitmap = if (Build.VERSION.SDK_INT < 28) MediaStore.Images.Media.getBitmap(context.contentResolver, uri) else ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                                    val maxHeight = 800; val maxWidth = 800
                                    val scale = minOf(maxHeight.toFloat() / bitmap.height, maxWidth.toFloat() / bitmap.width)
                                    val newWidth = (bitmap.width * scale).toInt(); val newHeight = (bitmap.height * scale).toInt()
                                    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
                                    val outputStream = ByteArrayOutputStream()
                                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                                    "data:image/jpeg;base64,${Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)}"
                                } catch (e: Exception) { originalProduct.imageUrl }
                            } ?: if (imageUrlInput.isNotBlank()) imageUrlInput else originalProduct.imageUrl
    
                            val productoActualizado = Producto(
                                producto_id = productId,
                                producto_nombre = nombre,
                                description = descripcion,
                                price = precio.toDoubleOrNull() ?: 0.0,
                                stock = stock.toIntOrNull() ?: 0,
                                imageUrl = finalImageUrl,
                                category = originalProduct.category ?: "General",
                                destacado = originalProduct.destacado,
                                valoracion = originalProduct.valoracion,
                                precioAnterior = originalProduct.precioAnterior,
                                marca = originalProduct.marca,
                                peso = originalProduct.peso,
                                material = originalProduct.material,
                                tamano = originalProduct.tamano,
                                tipoHigiene = originalProduct.tipoHigiene,
                                fragancia = originalProduct.fragancia,
                                tipo = originalProduct.tipo,
                                tipoAccesorio = originalProduct.tipoAccesorio,
                                tipoMascota = tipoMascota.takeIf { it.isNotBlank() },
                                raza = raza.takeIf { it.isNotBlank() },
                                edad = edad.takeIf { it.isNotBlank() },
                                pesoMascota = pesoMascota.toDoubleOrNull()
                            )
                            adminViewModel.updateProducto(productId, productoActualizado)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Cambios")
                }
            }
        }
    }
}
