package com.example.mimascota.Model

data class Alimento(
    val idPro: String,
    val namePro: String,
    val descriptionPro: String,
    val pricePro: Double,
    val marca: String,
    val tipo: String,
    val peso: Double
) : Product(idPro, namePro, descriptionPro, pricePro) {

}