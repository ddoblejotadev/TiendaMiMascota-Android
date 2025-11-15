package com.example.mimascota.Model

/**
 * Accesorios: Tipo específico de producto para accesorios de mascotas
 * Extiende de Producto con campos específicos para accesorios
 */
data class Accesorios(
    val idPro: Int,
    val namePro: String,
    val descriptionPro: String,
    val pricePro: Int,
    val stockPro: Int,
    override val tipoAccesorio: String?,
    override val material: String?
) : Producto(
    producto_id = idPro,
    producto_nombre = namePro,
    description = descriptionPro,
    price = pricePro,
    stock = stockPro,
    category = "Accesorios",
    imageUrl = "",
    tipoAccesorio = tipoAccesorio,
    material = material
)
