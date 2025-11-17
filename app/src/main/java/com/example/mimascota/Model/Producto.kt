package com.example.mimascota.Model

import com.google.gson.annotations.SerializedName

/**
 * Producto: Modelo alineado con la respuesta del backend Spring Boot.
 * Backend devuelve números como Double (price, precioAnterior) y stock como Int.
 * Mantenemos las propiedades auxiliares id y name para compatibilidad con UI existente.
 */
open class Producto(
    @SerializedName("producto_id")
    val producto_id: Int = 0,

    @SerializedName("producto_nombre")
    val producto_nombre: String = "",

    @SerializedName("price")
    val price: Int = 0,

    @SerializedName("category")
    val category: String = "",

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("imageUrl")
    val imageUrl: String? = null,

    @SerializedName("stock")
    val stock: Int? = null,

    @SerializedName("destacado")
    val destacado: Boolean? = null,

    @SerializedName("valoracion")
    val valoracion: Double? = null,

    @SerializedName("precioAnterior")
    val precioAnterior: Int? = null,

    @SerializedName("marca")
    open val marca: String? = null,

    @SerializedName("peso")
    open val peso: Double? = null,

    // Campos adicionales opcionales (el backend podría agregarlos más adelante)
    open val material: String? = null,
    open val tamano: String? = null,
    open val tipoHigiene: String? = null,
    open val fragancia: String? = null,
    open val tipo: String? = null,
    open val tipoAccesorio: String? = null
) {
    // Propiedades de conveniencia para código existente
    val id: Int get() = producto_id
    val name: String get() = producto_nombre
}
