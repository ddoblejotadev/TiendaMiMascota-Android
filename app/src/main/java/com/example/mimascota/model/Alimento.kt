package com.example.mimascota.model

data class Alimento(
    val idPro: String,
    val namePro: String,
    val descriptionPro: String,
    val pricePro: Int,
    val marca: String,
    val tipo: String,
    val peso: Double
) : Producto(idPro, namePro, descriptionPro, pricePro) {

}