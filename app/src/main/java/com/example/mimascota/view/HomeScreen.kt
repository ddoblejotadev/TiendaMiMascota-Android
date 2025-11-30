package com.example.mimascota.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Portrait
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimascota.R
import com.example.mimascota.viewModel.AuthViewModel
import android.app.Activity
import android.content.Intent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, name: String?, authViewModel: AuthViewModel) {
    val esAdmin = authViewModel.esAdmin() || (name?.equals("admin", ignoreCase = true) == true)
    val fotoPerfil = authViewModel.fotoPerfil.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.obtenerFotoPerfilActual()
    }

    val context = LocalContext.current
    val profileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val updatedName = activityResult.data?.getStringExtra("updated_name")
            if (!updatedName.isNullOrEmpty()) {
                navController.navigate("home/$updatedName") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo1),
                            contentDescription = "Logo",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mi Mascota")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        if (fotoPerfil.value != null)
                            MaterialTheme.colorScheme.secondaryContainer
                        else
                            MaterialTheme.colorScheme.primaryContainer
                    )
                    .clickable {
                        val intent = Intent(context, com.example.mimascota.ui.activity.ProfileEditActivity::class.java)
                        profileLauncher.launch(intent)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (fotoPerfil.value != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Foto guardada",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "✓",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                } else {
                    val iniciales = name?.take(2)?.uppercase() ?: "?"
                    Text(
                        text = iniciales,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (fotoPerfil.value != null) "Foto guardada - Toca para cambiar" else "Toca para agregar foto",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            val displayName = name ?: com.example.mimascota.util.TokenManager.getUserName() ?: "Invitado"
            Text(
                text = "¡Bienvenido, $displayName! 👋",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Has iniciado sesión como:",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = displayName,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate("Catalogo") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(imageVector = Icons.Default.Store, contentDescription = "Catálogo")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ir al Catálogo")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { navController.navigate("Carrito") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Carrito")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver Carrito")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón para la nueva sección de Huachitos
            OutlinedButton(
                onClick = { navController.navigate("huachitos") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(imageVector = Icons.Default.Pets, contentDescription = "Adoptar")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adopta un Huachito")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { navController.navigate("Acerca") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(imageVector = Icons.Default.Portrait, contentDescription = "Sobre Nosotros")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver Sobre Nosotros")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { navController.context?.let { navController.navigate("MisPedidos") } ?: navController.navigate("MisPedidos") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Mis Pedidos")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mis Pedidos")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { val intent = Intent(context, com.example.mimascota.ui.activity.ProfileEditActivity::class.java); profileLauncher.launch(intent) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(imageVector = Icons.Default.Portrait, contentDescription = "Editar Perfil")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar Perfil")
            }

            if (esAdmin) {
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Panel de Administración",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { navController.navigate("backOffice") },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(imageVector = Icons.Default.Store, contentDescription = "Panel Administrativo")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Panel Administrativo")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    authViewModel.cerrarSesion()
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Cerrar Sesión",
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }
}