package com.example.mimascota.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class AdminScreen(val route: String, val label: String, val icon: ImageVector) {
    object Products : AdminScreen("admin_products", "Productos", Icons.Default.List)
    object Orders : AdminScreen("admin_orders", "Pedidos", Icons.Default.ShoppingCart)
    object Users : AdminScreen("admin_users", "Usuarios", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackOfficeScreen(navController: NavController) {
    val adminNavController = rememberNavController()
    val items = listOf(
        AdminScreen.Products,
        AdminScreen.Orders,
        AdminScreen.Users,
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by adminNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
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
    ) { innerPadding ->
        NavHost(
            adminNavController,
            startDestination = AdminScreen.Products.route,
            Modifier.padding(innerPadding)
        ) {
            composable(AdminScreen.Products.route) { AdminProductsScreen(navController = navController) }
            composable(AdminScreen.Orders.route) { AdminOrdersScreen() }
            composable(AdminScreen.Users.route) { AdminUsersScreen() }
        }
    }
}
