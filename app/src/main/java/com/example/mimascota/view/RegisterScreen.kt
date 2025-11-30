package com.example.mimascota.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mimascota.viewModel.AuthViewModel
import com.example.mimascota.util.RutValidator
import android.util.Patterns

@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel) {
    var nombre by remember { mutableStateOf("") }
    var run by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var registroState by remember { viewModel.registroState }

    // Navegar al home si el registro es exitoso
    LaunchedEffect(registroState) {
        if (registroState.contains("exitoso", ignoreCase = true)) {
            val username = nombre.ifBlank { "Usuario" }
            navController.navigate("home/$username") {
                popUpTo("register") { inclusive = true }
            }
        }
    }

    // Estados de error por campo (null = sin error)
    var nombreError by remember { mutableStateOf<String?>(null) }
    var runError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    // var direccionError by remember { mutableStateOf<String?>(null) } // direccionError no se usa por ahora

    // Fondo con gradiente
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Logo y título
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = "Logo",
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            androidx.compose.material3.Text(
                text = "Mi Mascota",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            androidx.compose.material3.Text(
                text = "Crea tu cuenta",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card con el formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.material3.Text(
                        text = "Registro",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Campo Nombre
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = {
                            nombre = it
                            if (!it.isBlank()) nombreError = null
                        },
                        label = { androidx.compose.material3.Text("Nombre completo") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Nombre"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (nombreError != null) {
                        androidx.compose.material3.Text(
                            text = nombreError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Campo RUN
                    OutlinedTextField(
                        value = run,
                        onValueChange = {
                            run = it
                            if (it.isNotBlank() && RutValidator.esValido(it)) runError = null
                        },
                        label = { androidx.compose.material3.Text("RUT (ej: 12345678-9)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = "RUN"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (runError != null) {
                        androidx.compose.material3.Text(
                            text = runError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Campo Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (it.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(it).matches()) emailError = null
                        },
                        label = { androidx.compose.material3.Text("Email") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (emailError != null) {
                        androidx.compose.material3.Text(
                            text = emailError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Campo Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (it.length >= 6) passwordError = null
                        },
                        label = { androidx.compose.material3.Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Contraseña"
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (passwordError != null) {
                        androidx.compose.material3.Text(
                            text = passwordError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Campo Dirección
                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        label = { androidx.compose.material3.Text("Dirección") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Dirección"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón de registro
                    Button(
                        onClick = {
                            // Validación cliente antes de llamar al ViewModel
                            var valid = true

                            if (nombre.isBlank()) {
                                nombreError = "Ingrese nombre"
                                valid = false
                            }
                            if (run.isBlank()) {
                                runError = "Ingrese RUT"
                                valid = false
                            } else if (!RutValidator.esValido(run)) {
                                runError = "RUT inválido"
                                valid = false
                            }
                            if (email.isBlank()) {
                                emailError = "Ingrese email"
                                valid = false
                            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                emailError = "Email inválido"
                                valid = false
                            }
                            if (password.length < 6) {
                                passwordError = "La contraseña debe tener al menos 6 caracteres"
                                valid = false
                            }

                            if (valid) {
                                viewModel.registrarUsuario(run, nombre, email, password, direccion)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "Registrar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Mostrar estado de registro si hay mensaje
                    if (registroState.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        androidx.compose.material3.Text(
                            text = registroState,
                            color = if (registroState.contains("exitoso", ignoreCase = true)) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            },
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón para ir a login
            TextButton(
                onClick = { navController.navigate("login") }
            ) {
                androidx.compose.material3.Text(
                    text = "¿Ya tienes una cuenta? ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                androidx.compose.material3.Text(
                    text = "Inicia sesión",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            androidx.compose.material3.Text(
                text = "© 2025 Mi Mascota",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
