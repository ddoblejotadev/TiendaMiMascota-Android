package com.example.mimascota.Model

/**
 * Alimento: Tipo específico de producto para comida de mascotas
 * Extiende de Producto con campos específicos para alimentos
 */
data class Alimento(
    val idPro: Int,
    val namePro: String,
    val descriptionPro: String,
    val pricePro: Int,
    val stockPro: Int,
    override val marca: String,
    override val tipo: String,
    override val peso: String
) : Producto(
    id = idPro,
    name = namePro,
    description = descriptionPro,
    price = pricePro,
    stock = stockPro,
    category = "Alimento",
    imageUrl = "",
    marca = marca,
    tipo = tipo,
    peso = peso
)
