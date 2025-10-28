package com.example.mimascota.Model

/**
 * Higiene: Tipo específico de producto para productos de higiene de mascotas
 * Extiende de Producto agregando tipo de higiene y fragancia
 */
data class Higiene(
    val idPro: Int,
    val namePro: String,
    val descriptionPro: String,
    val pricePro: Int,
    val stockPro: Int,
    val tipoHigiene: String,
    val fragancia: String
) : Producto(
    id = idPro,
    name = namePro,
    description = descriptionPro,
    price = pricePro,
    stock = stockPro,
    category = "Higiene",
    imageUrl = ""
)
