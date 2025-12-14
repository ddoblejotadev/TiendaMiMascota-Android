package com.example.mimascota

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.platform.LocalContext
import com.example.mimascota.view.*
import com.example.mimascota.view.AboutUsScreen
import com.example.mimascota.viewModel.AdminViewModel
import com.example.mimascota.viewModel.AuthViewModel
import com.example.mimascota.viewModel.CartViewModel
import com.example.mimascota.viewModel.CatalogoViewModel
import com.example.mimascota.util.ConnectionTester
import com.example.mimascota.viewModel.HuachitosViewModel
import com.example.mimascota.viewModel.RecomendacionesViewModel
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

        probarConexionBackend()

        setContent {
            val navController = rememberNavController()
            val authViewModel: AuthViewModel = viewModel()
            val catalogoViewModel: CatalogoViewModel = viewModel()
            val cartViewModel: CartViewModel = viewModel()
            val adminViewModel: AdminViewModel = viewModel()
            val recomendacionesViewModel: RecomendacionesViewModel = viewModel()

            // Handles the intent that started the activity, and any new intents received
            LaunchedEffect(navController) {
                handleIntent(intent, navController, cartViewModel)
            }

            NavHost(navController, startDestination = "register") {
                composable("register") { RegisterScreen(navController, authViewModel) }
                composable("login") { LoginScreen(navController, authViewModel) }
                composable("home/{name}") { backStack ->
                    val name = backStack.arguments?.getString("name")
                    HomeScreen(navController, name, authViewModel)
                }
                composable(
                    route = "Catalogo?categoria={categoria}",
                    arguments = listOf(navArgument("categoria") { 
                        type = NavType.StringType
                        nullable = true
                    })
                ) { backStackEntry ->
                    val categoria = backStackEntry.arguments?.getString("categoria")
                    CatalogoScreen(navController, catalogoViewModel, cartViewModel, categoria)
                }
                composable(
                    route = "Detalle/{id}?from={from}",
                    arguments = listOf(
                        navArgument("id") { type = NavType.IntType },
                        navArgument("from") { type = NavType.StringType; nullable = true }
                    )
                ) { backStack ->
                    val id = backStack.arguments?.getInt("id")
                    val from = backStack.arguments?.getString("from")
                    if (id != null) {
                        LaunchedEffect(id) {
                            catalogoViewModel.getProductoById(id)
                        }
                        val producto by catalogoViewModel.selectedProduct.collectAsState()
                        producto?.let { prod ->
                            DetalleProductoScreen(
                                navController = navController,
                                producto = prod,
                                onAddToCart = { p, cantidad ->
                                    repeat(cantidad) {
                                        cartViewModel.agregarAlCarrito(p)
                                    }
                                },
                                from = from
                            )
                        }
                    }
                }
                composable("Carrito") { CarritoScreen(navController, cartViewModel) }
                composable(
                    route = "compraExitosa?numeroOrden={numeroOrden}&items={items}",
                    arguments = listOf(
                        navArgument("numeroOrden") { 
                            type = NavType.StringType
                            nullable = true 
                        },
                        navArgument("items") { 
                            type = NavType.StringType
                            nullable = true
                        }
                    )
                ) { backStackEntry ->
                    val numeroOrden = backStackEntry.arguments?.getString("numeroOrden")
                    val itemsJson = backStackEntry.arguments?.getString("items")
                    CompraExitosaScreenWrapper(
                        navController = navController,
                        authViewModel = authViewModel,
                        numeroOrden = numeroOrden,
                        itemsJson = itemsJson
                    )
                }
                composable("compraRechazada/{tipoError}") { backStack ->
                    val tipoError = backStack.arguments?.getString("tipoError") ?: "PAGO"
                    CompraRechazadaScreenWrapper(navController, tipoError, cartViewModel, authViewModel)
                }
                composable("Acerca") { AboutUsScreen(navController) }
                composable("Recomendaciones") { RecomendacionesScreen(navController, recomendacionesViewModel, cartViewModel) }
                
                adoptionNavGraph(navController)
                
                composable("backOffice") { BackOfficeScreen(navController, authViewModel, adminViewModel) }
                
                composable(
                    route = "agregarProducto?id={id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = -1 })
                ) {
                    val productoId = it.arguments?.getInt("id")
                    AgregarProductoScreen(navController, catalogoViewModel, if (productoId == -1) null else productoId)
                }
                composable("MisPedidos") {
                    val context = LocalContext.current
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        context.startActivity(android.content.Intent(context, com.example.mimascota.ui.activity.MisPedidosActivity::class.java))
                        navController.popBackStack()
                    }
                }
                composable("editarPerfil") {
                    val context = LocalContext.current
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        context.startActivity(android.content.Intent(context, com.example.mimascota.ui.activity.ProfileEditActivity::class.java))
                        navController.popBackStack()
                    }
                }
                composable("fotoDePerfil") { FotoDePerfil(navController, authViewModel) }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        recreate()
    }

    private fun handleIntent(intent: Intent?, navController: NavHostController, cartViewModel: CartViewModel) {
        intent?.let {
            if (it.getBooleanExtra("CLEAR_CART", false)) {
                cartViewModel.vaciarCarrito()
                it.removeExtra("CLEAR_CART")
            }
            val navigateTo = it.getStringExtra("NAVIGATE_TO")
            if (navigateTo == "Catalogo") {
                navController.navigate("Catalogo") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
                it.removeExtra("NAVIGATE_TO")
            }
        }
    }

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

fun NavGraphBuilder.adoptionNavGraph(navController: NavHostController) {
    navigation(startDestination = "adopcion_list", route = "huachitos") {
        composable("adopcion_list") {
            val huachitosViewModel: HuachitosViewModel = viewModel()
            AdopcionScreen(navController, huachitosViewModel)
        }
        composable("animalDetail/{animalId}") { backStackEntry ->
            val animalId = backStackEntry.arguments?.getString("animalId")?.toIntOrNull() ?: -1
            val huachitosViewModel: HuachitosViewModel = viewModel(navController.getBackStackEntry("huachitos"))
            AnimalDetailScreen(navController, animalId, huachitosViewModel)
        }
    }
}
