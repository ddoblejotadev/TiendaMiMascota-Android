package com.example.mimascota.Model

/**
 * Juguete: Tipo específico de producto para juguetes de mascotas
 * Extiende de Producto con campos específicos para juguetes
 */
data class Juguete(
    val idPro: Int,
    val namePro: String,
    val descriptionPro: String,
    val pricePro: Int,
    val stockPro: Int,
    override val material: String?,
    override val tamano: String?
) : Producto(
    producto_id = idPro,
    producto_nombre = namePro,
    description = descriptionPro,
    price = pricePro,
    stock = stockPro,
    category = "Juguete",
    imageUrl = "",
    material = material,
    tamano = tamano
)
