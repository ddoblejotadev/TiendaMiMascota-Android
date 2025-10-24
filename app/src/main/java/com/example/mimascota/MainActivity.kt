package com.example.mimascota

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.mimascota.View.registerScreen
import com.example.mimascota.ViewModel.AuthViewModel
import androidx.navigation.compose.*
import com.example.mimascota.Model.Producto
import com.example.mimascota.View.CarritoScreen
import com.example.mimascota.View.CatalogoScreen
import com.example.mimascota.View.DetalleProductoScreen
import com.example.mimascota.View.HomeScreen
import com.example.mimascota.View.ProductoCard
import com.example.mimascota.View.loginScreen
import com.example.mimascota.ViewModel.CartViewModel
import com.example.mimascota.ViewModel.CatalogoViewModel

//import com.example.mimascota.ui.theme.MiMascotaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val viewModel: AuthViewModel = viewModel()
            val viewModelC = remember{ CatalogoViewModel() }
            val cartViewModel = remember { CartViewModel() }
            NavHost(navController, startDestination = "register") {
                composable("register") {
                    registerScreen(navController, viewModel)
                }
                composable("login") {
                    loginScreen(navController, viewModel)
                }
                composable("home/{name}") { backStack ->
                    val name = backStack.arguments?.getString("name")
                    HomeScreen(navController,name)
                }
                composable("Catalogo") {
                    CatalogoScreen(navController, viewModelC, cartViewModel)
                }
                composable("Detalle/{id}") { backStack ->
                    val idStr = backStack.arguments?.getString("id")
                    val id = idStr?.toIntOrNull() ?: -1
                    DetalleProductoScreen(id, viewModelC, cartViewModel)
                }
                composable("Carrito") {
                    CarritoScreen(cartViewModel)
                }
            }
        }
    }
}

/*
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MiMascotaTheme {
        Greeting("Android")
    }
}
*/