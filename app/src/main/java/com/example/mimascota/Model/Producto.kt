package com.example.mimascota.Model

import com.google.gson.annotations.SerializedName

/**
 * Producto: Clase base para todos los productos de la tienda
 *
 * Esta clase representa el modelo de producto sincronizado con el backend Spring Boot
 * Campos opcionales para permitir flexibilidad entre diferentes categorías
 */
open class Producto(
    @SerializedName("producto_id")
    val id: Int = 0,

    @SerializedName("producto_nombre")
    val name: String = "",

    val description: String? = null,
    val price: Int = 0,
    val stock: Int = 0,
    val category: String = "",
    val imageUrl: String? = null,
    val destacado: Boolean = false,
    val valoracion: Double? = null,
    val precioAnterior: Int? = null,

    // Campos específicos por categoría (opcionales y open para override)
    open val material: String? = null,
    open val tamano: String? = null,
    open val tipoHigiene: String? = null,
    open val fragancia: String? = null,
    open val marca: String? = null,
    open val tipo: String? = null,
    open val peso: String? = null,
    open val tipoAccesorio: String? = null
)
