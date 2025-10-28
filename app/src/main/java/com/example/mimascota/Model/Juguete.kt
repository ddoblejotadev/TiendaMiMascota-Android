package com.example.mimascota.Model

/**
 * Juguete: Tipo específico de producto para juguetes de mascotas
 * Extiende de Producto agregando material y tamaño
 */
data class Juguete(
    val idPro: Int,
    val namePro: String,
    val descriptionPro: String,
    val pricePro: Int,
    val stockPro: Int,
    val material: String,
    val tamano: String
) : Producto(
    id = idPro,
    name = namePro,
    description = descriptionPro,
    price = pricePro,
    stock = stockPro,
    category = "Juguete",
    imageUrl = ""
)
