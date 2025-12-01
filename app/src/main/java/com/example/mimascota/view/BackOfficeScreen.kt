package com.example.mimascota.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mimascota.viewModel.AdminViewModel
import com.example.mimascota.viewModel.AuthViewModel

sealed class AdminScreen(val route: String, val label: String? = null, val icon: ImageVector? = null) {
    object Products : AdminScreen("admin_products", "Productos", Icons.Default.List)
    object Orders : AdminScreen("admin_orders", "Pedidos", Icons.Default.ShoppingCart)
    object Users : AdminScreen("admin_users", "Usuarios", Icons.Default.Person)

    // Rutas para CRUD de productos que no aparecen en la barra de navegación
    object ProductCreate : AdminScreen("admin_product_create")
    object ProductEdit : AdminScreen("admin_product_edit/{productId}") {
        fun createRoute(productId: Int) = "admin_product_edit/$productId"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackOfficeScreen(navController: NavController, authViewModel: AuthViewModel, adminViewModel: AdminViewModel) {
    val adminNavController = rememberNavController()
    val items = listOf(
        AdminScreen.Products,
        AdminScreen.Orders,
        AdminScreen.Users,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administrador") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.cerrarSesion()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by adminNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    screen.label?.let {
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = null) },
                            label = { Text(it) },
                            selected = currentDestination?.hierarchy?.any { dest -> dest.route == screen.route } == true,
                            onClick = {
                                adminNavController.navigate(screen.route) {
                                    popUpTo(adminNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = adminNavController,
            startDestination = AdminScreen.Products.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AdminScreen.Products.route) { AdminProductsScreen(navController = adminNavController, adminViewModel = adminViewModel) }
            composable(AdminScreen.Orders.route) { AdminOrdersScreen() }
            composable(AdminScreen.Users.route) { AdminUsersScreen(navController = adminNavController, adminViewModel = adminViewModel) }

            // Rutas para crear y editar productos (descomentadas)
            composable(AdminScreen.ProductCreate.route) {
                AdminProductCreateScreen(navController = adminNavController, adminViewModel = adminViewModel)
            }
            composable(
                route = AdminScreen.ProductEdit.route,
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId")
                if (productId != null) {
                    AdminProductEditScreen(navController = adminNavController, adminViewModel = adminViewModel, productId = productId)
                }
            }
        }
    }
}
