package com.example.mimascota.model

data class Juguete(
    val idPro: String,
    val namePro: String,
    val descriptionPro: String,
    val pricePro: Int,
    val material: String,
    val tamano: String
) : Producto(idPro, namePro, descriptionPro, pricePro) {
}