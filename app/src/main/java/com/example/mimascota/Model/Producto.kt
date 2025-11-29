package com.example.mimascota.model

import com.google.gson.annotations.SerializedName

/**
 * Producto: Modelo base y unificado para todos los productos.
 * Es una clase abierta para permitir la herencia de tipos de productos específicos.
 */
open class Producto(
    @SerializedName("producto_id")
    open val producto_id: Int = 0,

    @SerializedName("producto_nombre")
    open val producto_nombre: String = "",

    @SerializedName("price")
    // Cambiado a Double para coincidir con el backend (ej. 25990.0)
    open val price: Double = 0.0,

    @SerializedName("category")
    open val category: String = "",

    @SerializedName("description")
    open val description: String? = null,

    @SerializedName("imageUrl")
    open val imageUrl: String? = null,

    @SerializedName("stock")
    open val stock: Int? = null,

    @SerializedName("destacado")
    open val destacado: Boolean? = null,

    @SerializedName("valoracion")
    open val valoracion: Double? = null,

    @SerializedName("precioAnterior")
    // Cambiado a Double?
    open val precioAnterior: Double? = null,

    // Propiedades para herencia
    @SerializedName("marca")
    open val marca: String? = null,

    @SerializedName("peso")
    open val peso: Double? = null,

    @SerializedName("material")
    open val material: String? = null,

    @SerializedName("tamano")
    open val tamano: String? = null,

    @SerializedName("tipoHigiene")
    open val tipoHigiene: String? = null,

    @SerializedName("fragancia")
    open val fragancia: String? = null,

    @SerializedName("tipo")
    open val tipo: String? = null,

    @SerializedName("tipoAccesorio")
    open val tipoAccesorio: String? = null
) {
    // Propiedades de conveniencia para mantener compatibilidad
    val id: Int get() = producto_id
    val name: String get() = producto_nombre
}
