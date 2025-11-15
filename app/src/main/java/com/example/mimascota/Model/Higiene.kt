package com.example.mimascota.Model

/**
 * Higiene: Tipo específico de producto para productos de higiene de mascotas
 * Extiende de Producto con campos específicos para higiene
 */
data class Higiene(
    val idPro: Int,
    val namePro: String,
    val descriptionPro: String,
    val pricePro: Int,
    val stockPro: Int,
    override val tipoHigiene: String?,
    override val fragancia: String?
) : Producto(
    producto_id = idPro,
    producto_nombre = namePro,
    description = descriptionPro,
    price = pricePro,
    stock = stockPro,
    category = "Higiene",
    imageUrl = "",
    tipoHigiene = tipoHigiene,
    fragancia = fragancia
)
