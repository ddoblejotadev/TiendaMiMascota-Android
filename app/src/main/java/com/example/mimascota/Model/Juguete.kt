package com.example.mimascota.Model

data class Juguete(
    val idPro: String,
    val namePro: String,
    val descriptionPro: String,
    val pricePro: Int,
    val material: String,
    val tamano: String
) : Product(idPro, namePro, descriptionPro, pricePro) {
}