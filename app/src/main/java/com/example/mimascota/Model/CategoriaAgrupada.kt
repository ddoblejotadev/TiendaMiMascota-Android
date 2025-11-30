package com.example.mimascota.model

/**
 * Representa una categoría que contiene una lista de productos.
 * Utilizado para mostrar productos agrupados por categoría en la UI.
 */
data class CategoriaAgrupada(
    val nombre: String,
    val productos: List<Producto>
)
