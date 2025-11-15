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
    val producto_id: Int = 0,

    @SerializedName("producto_nombre")
    val producto_nombre: String = "",

    val price: Int = 0,
    val category: String = "",
    val description: String? = null,
    val imageUrl: String? = null,
    val stock: Int? = null,
    val destacado: Boolean? = null,
    val valoracion: Double? = null,
    val precioAnterior: Int? = null,
    open val marca: String? = null,
    open val peso: Double? = null,

    // Campos adicionales específicos por categoría (opcionales y open para override)
    open val material: String? = null,
    open val tamano: String? = null,
    open val tipoHigiene: String? = null,
    open val fragancia: String? = null,
    open val tipo: String? = null,
    open val tipoAccesorio: String? = null
) {
    // Propiedades de conveniencia para mantener compatibilidad con código existente
    val id: Int get() = producto_id
    val name: String get() = producto_nombre
}
