package com.example.mimascota.Model

data class Accesorios(
    val idPro: String,
    val namePro: String,
    val descriptionPro: String,
    val pricePro: Int,
    val tipoAccesorio: String,
    val material: String
) : Product(idPro, namePro, descriptionPro, pricePro) {
}