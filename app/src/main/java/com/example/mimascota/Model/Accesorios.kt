package com.example.mimascota.Model

/**
 * Accesorios: Tipo específico de producto para accesorios de mascotas
 * Extiende de Producto agregando tipo de accesorio y material
 */
data class Accesorios(
    val idPro: Int,
    val namePro: String,
    val descriptionPro: String,
    val pricePro: Int,
    val stockPro: Int,
    val tipoAccesorio: String,
    val material: String
) : Producto(
    id = idPro,
    name = namePro,
    description = descriptionPro,
    price = pricePro,
    stock = stockPro,
    category = "Accesorios",
    imageUrl = ""
)
