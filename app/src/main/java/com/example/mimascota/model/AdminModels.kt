package com.example.mimascota.model

/**
 * Modelo para el panel de administrador que agrupa a un usuario con sus órdenes.
 */
data class UserWithOrders(
    val user: Usuario,
    val orders: List<OrdenHistorial>
)
