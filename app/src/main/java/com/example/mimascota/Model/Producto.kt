package com.example.mimascota.Model

import com.google.gson.annotations.SerializedName

/**
 * Producto: Clase base para todos los productos de la tienda
 *
 * Esta clase usa herencia para permitir diferentes tipos de productos
 * (Alimento, Accesorios, Higiene, Juguete)
 */
open class Producto(
    @SerializedName("producto_id")
    val id: Int,
    @SerializedName("producto_nombre")
    val name: String,
    val description: String?,
    val price: Int,
    val stock: Int,
    val category: String,
    val imageUrl: String?
)
