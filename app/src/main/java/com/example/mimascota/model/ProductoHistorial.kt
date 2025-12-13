package com.example.mimascota.model

data class ProductoHistorial(
    val productoId: String,
    val nombre: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val imagen: String
)
