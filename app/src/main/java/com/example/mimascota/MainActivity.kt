package com.example.mimascota

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.platform.LocalContext
import com.example.mimascota.view.*
import com.example.mimascota.view.AboutUsScreen
import com.example.mimascota.viewModel.AuthViewModel
import com.example.mimascota.viewModel.CartViewModel
import com.example.mimascota.viewModel.CatalogoViewModel
import com.example.mimascota.util.ConnectionTester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Probar conexión con el backend al iniciar
        probarConexionBackend()

        setContent {
            val navController = rememberNavController()
            val viewModel: AuthViewModel = viewModel()
            // Usar viewModel() para que las instancias sean gestionadas por el Activity
            val viewModelC: CatalogoViewModel = viewModel()
            val cartViewModel: CartViewModel = viewModel()

            NavHost(navController, startDestination = "register") {
                composable("register") {
                    RegisterScreen(navController, viewModel)
                }
                composable("login") {
                    LoginScreen(navController, viewModel)
                }
                composable("home/{name}") { backStack ->
                    val name = backStack.arguments?.getString("name")
                    HomeScreen(navController, name, viewModel)
                }
                composable("Catalogo") {
                    CatalogoScreen(navController, viewModelC, cartViewModel)
                }
                composable("Detalle/{id}") { backStack ->
                    val idStr = backStack.arguments?.getString("id")
                    val id = idStr?.toIntOrNull() ?: -1
                    DetalleProductoScreen(navController, id, viewModelC, cartViewModel)
                }
                composable("Carrito") {
                    CarritoScreen(navController, cartViewModel)
                }
                composable("compraExitosa") {
                    CompraExitosaScreenWrapper(navController, cartViewModel, viewModel)
                }
                composable("compraRechazada/{tipoError}") { backStack ->
                    val tipoError = backStack.arguments?.getString("tipoError") ?: "PAGO"
                    CompraRechazadaScreenWrapper(navController, tipoError, cartViewModel, viewModel)
                }
                composable("Acerca"){
                    AboutUsScreen(navController)
                }
                composable("backOffice") {
                    BackOfficeScreen(navController, viewModelC)
                }
                composable(
                    route = "agregarProducto?id={id}",
                    arguments = listOf(navArgument("id") { 
                        type = NavType.IntType
                        defaultValue = -1 
                    })
                ) {
                    val productoId = it.arguments?.getInt("id")
                    AgregarProductoScreen(navController, viewModelC, if (productoId == -1) null else productoId)
                }
                // Ruta para lanzar la Activity de Mis Pedidos
                composable("MisPedidos") {
                    val context = LocalContext.current
                    // Start activity and immediately return to previous nav entry
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        context.startActivity(android.content.Intent(context, com.example.mimascota.ui.activity.MisPedidosActivity::class.java))
                        // Navegar atrás para no quedarse en pantalla vacía
                        navController.popBackStack()
                    }
                }
                // Ruta para lanzar la Activity de editar perfil
                composable("editarPerfil") {
                    val context = LocalContext.current
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        context.startActivity(android.content.Intent(context, com.example.mimascota.ui.activity.ProfileEditActivity::class.java))
                        navController.popBackStack()
                    }
                }
                composable("fotoDePerfil") {
                    FotoDePerfil(navController, viewModel)
                }
            }
        }
    }

    /**
     * Prueba la conexión con el backend al iniciar la app
     */
    private fun probarConexionBackend() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val connectionInfo = ConnectionTester.getConnectionInfo()
                Log.d(TAG, connectionInfo.toString())

                if (!connectionInfo.isConnected) {
                    Log.w(TAG, "⚠️ No se pudo conectar con el backend")
                    Log.w(TAG, "💡 Verifica que el servidor esté corriendo")
                }            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al probar conexión: ${e.message}")
            }
        }
    }
}
