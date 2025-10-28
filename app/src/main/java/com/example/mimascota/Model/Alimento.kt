package com.example.mimascota.Model

/**
 * Alimento: Tipo específico de producto para comida de mascotas
 * Extiende de Producto agregando propiedades específicas como marca, tipo y peso
 */
data class Alimento(
    val idPro: Int,
    val namePro: String,
    val descriptionPro: String,
    val pricePro: Int,
    val stockPro: Int,
    val marca: String,
    val tipo: String,
    val peso: Double
) : Producto(
    id = idPro,
    name = namePro,
    description = descriptionPro,
    price = pricePro,
    stock = stockPro,
    category = "Alimento",
    imageUrl = ""
)
