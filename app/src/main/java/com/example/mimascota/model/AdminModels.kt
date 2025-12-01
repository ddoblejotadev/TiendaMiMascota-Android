package com.example.mimascota.model

/**
 * Data class que agrupa un usuario con su lista de órdenes.
 * Utilizado en el panel de administración para mostrar los pedidos por usuario.
 */
data class UserWithOrders(val user: Usuario, val orders: List<OrdenHistorial>)
